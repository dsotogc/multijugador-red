package com.combate.client;

import java.io.*;
import java.net.*;
import com.combate.net.Mensaje;
import com.combate.net.TipoMensaje;
import com.combate.view.VentanaJuego;

/**
 * Cliente UDP con interfaz gráfica para el juego de combate por turnos.
 * Se conecta al servidor, gestiona la comunicación y actualiza la UI.
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
    
    private VentanaJuego ventana;
    private Thread hilo_receptor;
    
    private int accion_seleccionada = -1;
    private int objetivo1 = -1;
    private int objetivo2 = -1;
    private boolean esperando_accion = false;
    
    /**
     * Constructor del cliente
     */
    public ClienteUDP() throws SocketException, UnknownHostException 
    {
        socket = new DatagramSocket();
        servidor_ip = InetAddress.getLocalHost();
        
        ventana = new VentanaJuego(this);
        
        iniciarHiloReceptor();
    }
    
    /**
     * Inicia un hilo para recibir mensajes del servidor continuamente
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
     * Conecta con el servidor y envía mensaje de conexión
     */
    public void conectar() throws IOException 
    {
        Mensaje msg = new Mensaje(TipoMensaje.CONEXION);
        enviar(msg);
    }
    
    /**
     * Envía la clase seleccionada al servidor
     */
    public void enviarClase(String clase) throws IOException 
    {
        Mensaje msg = new Mensaje(TipoMensaje.SELECCION_CLASE, clase);
        enviar(msg);
    }
    
    /**
     * Envía una acción al servidor
     */
    public void enviarAccion(int accion, int obj1, int obj2) throws IOException 
    {
        String datos = accion + "|" + obj1 + "|" + obj2;
        Mensaje msg = new Mensaje(TipoMensaje.ACCION, datos);
        enviar(msg);
    }
    
    /**
     * Procesa los mensajes recibidos del servidor
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
                ventana.getPanelCombate().agregarLog("=== PARTIDA INICIADA ===");
                break;
                
            case ESTADO_PARTIDA:
                actualizarEstado(msg.getDatos());
                break;
                
            case TURNO:
                ventana.getPanelCombate().actualizarTurno("*** ES TU TURNO ***");
                ventana.getPanelCombate().habilitarAcciones(true);
                esperando_accion = true;
                break;
                
            case FIN_PARTIDA:
                ventana.getPanelCombate().agregarLog("=== " + msg.getDatos() + " ===");
                ventana.getPanelCombate().actualizarTurno(msg.getDatos());
                ventana.getPanelCombate().habilitarAcciones(false);
                break;
                
            case ERROR:
                ventana.getPanelCombate().agregarLog("ERROR: " + msg.getDatos());
                break;
        }
    }
    
    /**
     * Actualiza el estado de la partida en la interfaz
     * Formato: "vida0|vida1|vida2|vida3|turno|estado"
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
        if (turno != mi_numero) 
        {
            ventana.getPanelCombate().actualizarTurno("Turno del Jugador " + turno);
            ventana.getPanelCombate().habilitarAcciones(false);
        }
    }
    
    /**
     * Envía un mensaje al servidor
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
     * Recibe un mensaje del servidor
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
     * Cierra el cliente
     */
    public void cerrar() 
    {
        socket.close();
    }
    
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