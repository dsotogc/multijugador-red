package com.combate.client;

import java.io.*;
import java.net.*;
import com.combate.net.Mensaje;
import com.combate.net.TipoMensaje;
import com.combate.view.VentanaJuego;

/**
 * Cliente UDP para el juego.
 * Gestiona la conexión con el servidor, el envío y recepción de mensajes,
 * y la actualización de la interfaz gráfica en tiempo real.
 * 
 * @author David Soto García
 */
public class ClienteUDP 
{
    private DatagramSocket socket;
    private InetAddress servidor_ip;
    private int servidor_puerto = 9000;
    
    private int mi_numero;
    private int mi_equipo;
    private String mi_clase;
    
    private VentanaJuego ventana;
    private Thread hilo_receptor;
    
    /**
     * Inicializa el socket, la ventana gráfica y el hilo receptor de mensajes.
     * 
     * @throws SocketException Si no se puede crear el socket UDP
     * @throws UnknownHostException Si no se puede resolver localhost
     */
    public ClienteUDP() throws SocketException, UnknownHostException 
    {
        socket = new DatagramSocket();
        servidor_ip = InetAddress.getLocalHost();
        
        ventana = new VentanaJuego(this);
        
        iniciarHiloReceptor();
    }
    
    /**
     * Inicia un hilo secundario que escucha continuamente mensajes del servidor.
     * Este hilo se ejecuta en paralelo al hilo principal de la interfaz gráfica.
     */
    private void iniciarHiloReceptor() 
    {
        hilo_receptor = new Thread(() -> {
            try 
            {
                while (!socket.isClosed()) 
                {
                    Mensaje msg = recibir();
                    procesarMensaje(msg);
                }
            } 
            catch (Exception e) 
            {
                if (!socket.isClosed()) 
                {
                    e.printStackTrace();
                }
            }
        });
        hilo_receptor.start();
    }
    
    /**
     * Envía un mensaje de conexión inicial al servidor.
     * 
     * @throws IOException Si ocurre un error al enviar el mensaje
     */
    public void conectar() throws IOException 
    {
        Mensaje msg = new Mensaje(TipoMensaje.CONEXION);
        enviar(msg);
    }
    
    /**
     * Envía al servidor la clase de personaje seleccionada.
     * 
     * @param clase Nombre de la clase ("Luchador", "Mago" o "Curandero")
     * @throws IOException Si ocurre un error al enviar el mensaje
     */
    public void enviarClase(String clase) throws IOException 
    {
        Mensaje msg = new Mensaje(TipoMensaje.SELECCION_CLASE, clase);
        enviar(msg);
    }
    
    /**
     * Envía una acción de combate al servidor.
     * 
     * @param accion Número de acción (1, 2 o 3)
     * @param obj1 Índice del primer objetivo (0-3)
     * @param obj2 Índice del segundo objetivo (0-3) o -1 si no se usa
     * @throws IOException Si ocurre un error al enviar el mensaje
     */
    public void enviarAccion(int accion, int obj1, int obj2) throws IOException 
    {
        String datos = accion + "|" + obj1 + "|" + obj2;
        Mensaje msg = new Mensaje(TipoMensaje.ACCION, datos);
        enviar(msg);
    }
    
    /**
     * Envía un mensaje de chat global a todos los jugadores.
     * 
     * @param mensaje Texto del mensaje a enviar
     * @throws IOException Si ocurre un error al enviar el mensaje
     */
    public void enviarMensajeChat(String mensaje) throws IOException 
    {
        String datos = mi_numero + "|" + mi_clase + "|" + mensaje;
        Mensaje msg = new Mensaje(TipoMensaje.MENSAJE_CHAT, datos);
        enviar(msg);
    }
    
    /**
     * Procesa los mensajes recibidos del servidor y actualiza la interfaz gráfica.
     * 
     * @param msg Mensaje recibido del servidor
     */
    private void procesarMensaje(Mensaje msg) 
    {
        switch (msg.getTipo()) 
        {
            case CONFIRMACION_CONEXION:
                String[] datos = msg.getDatos().split("\\|");
                mi_numero = Integer.parseInt(datos[0]);
                mi_equipo = Integer.parseInt(datos[1]);
                ventana.getPanelCombate().agregarLog("Conectado como Jugador " + mi_numero + " (Equipo " + mi_equipo + ")");
                break;
                
            case ESPERA:
                ventana.getPanelSeleccion().mostrarMensajeEspera(msg.getDatos());
                break;
                
            case TODOS_CONECTADOS:
                ventana.getPanelSeleccion().habilitarSeleccion();
                break;
                
            case INICIO_PARTIDA:
                ventana.mostrarCombate();
                ventana.getPanelCombate().configurarJuego(mi_numero, mi_equipo, mi_clase);
                ventana.getPanelCombate().agregarLog("=== PARTIDA INICIADA ===");
                break;
                
            case ESTADO_PARTIDA:
                actualizarEstado(msg.getDatos());
                break;
                
            case TURNO:
                ventana.getPanelCombate().activarTurno();
                break;
                
            case CHAT_GLOBAL:
                String[] datos_chat = msg.getDatos().split("\\|", 3);
                int num_jugador = Integer.parseInt(datos_chat[0]);
                String clase = datos_chat[1];
                String mensaje = datos_chat[2];
                ventana.getPanelCombate().agregarMensajeChat(num_jugador, clase, mensaje);
                break;
                
            case FIN_PARTIDA:
                int equipo_ganador = Integer.parseInt(msg.getDatos().split(" ")[2]);
                boolean es_mi_equipo = (equipo_ganador == mi_equipo);
                
                ventana.getPanelCombate().agregarLog("=== FIN DE LA PARTIDA ===");
                ventana.getPanelCombate().finalizarPartida("Ganó Equipo " + equipo_ganador);
                
                try 
                {
                    Thread.sleep(2000);
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                
                ventana.mostrarVictoria(equipo_ganador, es_mi_equipo);
                break;
                
            case ERROR:
                ventana.getPanelCombate().agregarLog("ERROR: " + msg.getDatos());
                break;
        }
    }
    
    /**
     * Actualiza el estado de la partida (vida de jugadores y turno actual).
     * Formato esperado: "vida0|vida1|vida2|vida3|turno|estado"
     * 
     * @param datos String con el estado actualizado de la partida
     */
    private void actualizarEstado(String datos) 
    {
        String[] partes = datos.split("\\|");
        
        for (int i = 0; i < 4; i++) 
        {
            int vida = Integer.parseInt(partes[i]);
            ventana.getPanelCombate().actualizarVida(i, vida);
        }
        
        int turno = Integer.parseInt(partes[4]);
        ventana.getPanelCombate().actualizarTurno(turno);
    }
    
    /**
     * Serializa y envía un mensaje al servidor mediante UDP.
     * 
     * @param msg Mensaje a enviar
     * @throws IOException Si ocurre un error al enviar el paquete
     */
    private void enviar(Mensaje msg) throws IOException 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        oos.close();
        
        byte[] datos = baos.toByteArray();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, servidor_ip, servidor_puerto);
        socket.send(paquete);
    }
    
    /**
     * Recibe y deserializa un mensaje del servidor.
     * 
     * @return Mensaje recibido del servidor
     * @throws IOException Si ocurre un error al recibir el paquete
     * @throws ClassNotFoundException Si no se puede deserializar el objeto
     */
    private Mensaje recibir() throws IOException, ClassNotFoundException 
    {
        byte[] buffer = new byte[2048];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Mensaje msg = (Mensaje) ois.readObject();
        ois.close();
        
        return msg;
    }
    
    /**
     * Establece la clase del jugador actual.
     * 
     * @param clase Nombre de la clase seleccionada
     */
    public void setMiClase(String clase) 
    {
        this.mi_clase = clase;
    }
    
    /**
     * Cierra el socket UDP y finaliza la conexión.
     */
    public void cerrar() 
    {
        socket.close();
    }
    
    /**
     * Punto de entrada de la aplicación cliente.
     * Crea una instancia del cliente y se conecta al servidor.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) 
    {
        try 
        {
            ClienteUDP cliente = new ClienteUDP();
            cliente.conectar();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}