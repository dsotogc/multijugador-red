package com.combate.server;

import java.io.*;
import java.net.*;
import com.combate.net.Mensaje;
import com.combate.net.TipoMensaje;
import com.combate.model.*;

/**
 * Servidor UDP para el juego de combate por turnos 2v2.
 * Gestiona las conexiones de hasta 4 clientes, asigna equipos, coordina el flujo
 * de la partida y hace broadcast de mensajes a todos los jugadores.
 * 
 * @author David Soto García
 */
public class ServidorUDP 
{
    private DatagramSocket socket;
    private int puerto = 9000;
    
    private InetAddress[] clientes_ip = new InetAddress[4];
    private int[] clientes_puerto = new int[4];
    private int num_clientes = 0;
    
    private Partida partida;
    
    /**
     * Inicializa el socket en el puerto 9000 y crea una nueva instancia de partida.
     * 
     * @throws SocketException Si no se puede crear el socket en el puerto especificado
     */
    public ServidorUDP() throws SocketException 
    {
        socket = new DatagramSocket(puerto);
        partida = new Partida();
        System.out.println("Servidor iniciado en puerto " + puerto);
    }
    
    /**
     * Inicia el servidor y gestiona todo el flujo del juego.
     * Fases: esperar clientes, recibir selección de clases, iniciar partida y gestionar turnos.
     * 
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ClassNotFoundException Si no se puede deserializar un mensaje
     */
    public void iniciar() throws IOException, ClassNotFoundException 
    {
        esperarClientes();
        recibirClases();
        
        partida.startGame();
        enviarATodos(new Mensaje(TipoMensaje.INICIO_PARTIDA, "La partida comienza!"));
        System.out.println("Partida iniciada!");
        
        jugar();
    }
    
    /**
     * Espera a que se conecten exactamente 4 clientes.
     * Los dos primeros se asignan al equipo 1, los dos últimos al equipo 2.
     * 
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ClassNotFoundException Si no se puede deserializar un mensaje
     */
    private void esperarClientes() throws IOException, ClassNotFoundException 
    {
        System.out.println("Esperando clientes...");
        
        while (num_clientes < 4) 
        {
            byte[] recibidos = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
            socket.receive(paquete);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Mensaje msg = (Mensaje) ois.readObject();
            ois.close();
            
            if (msg.getTipo() == TipoMensaje.CONEXION) 
            {
                clientes_ip[num_clientes] = paquete.getAddress();
                clientes_puerto[num_clientes] = paquete.getPort();
                
                int equipo = (num_clientes < 2) ? 1 : 2;
                
                System.out.println("Cliente " + num_clientes + " conectado. Equipo " + equipo);
                
                Mensaje respuesta = new Mensaje(TipoMensaje.CONFIRMACION_CONEXION, 
                                                num_clientes + "|" + equipo);
                enviarA(respuesta, clientes_ip[num_clientes], clientes_puerto[num_clientes]);
                
                num_clientes++;
                
                if (num_clientes < 4) 
                {
                    String msg_espera = num_clientes + "/4 jugadores conectados";
                    enviarATodos(new Mensaje(TipoMensaje.ESPERA, msg_espera));
                }
            }
        }
        
        System.out.println("4 clientes conectados!");
        enviarATodos(new Mensaje(TipoMensaje.TODOS_CONECTADOS, "Todos conectados"));
    }
    
    /**
     * Recibe la clase seleccionada por cada cliente y crea los personajes correspondientes.
     * Espera a que los 4 jugadores hayan elegido su clase antes de continuar.
     * 
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ClassNotFoundException Si no se puede deserializar un mensaje
     */
    private void recibirClases() throws IOException, ClassNotFoundException 
    {
        System.out.println("Esperando selección de clases...");
        int clases_recibidas = 0;
        
        while (clases_recibidas < 4) 
        {
            byte[] recibidos = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
            socket.receive(paquete);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Mensaje msg = (Mensaje) ois.readObject();
            ois.close();
            
            if (msg.getTipo() == TipoMensaje.SELECCION_CLASE) 
            {
                String clase = msg.getDatos();
                int num_cliente = buscarCliente(paquete.getAddress(), paquete.getPort());
                
                if (num_cliente != -1) 
                {
                    Personaje personaje = null;
                    if (clase.equals("Luchador")) 
                    {
                        personaje = new Luchador();
                    } 
                    else if (clase.equals("Mago")) 
                    {
                        personaje = new Mago();
                    } 
                    else if (clase.equals("Curandero")) 
                    {
                        personaje = new Curandero();
                    }
                    
                    int equipo = (num_cliente < 2) ? 1 : 2;
                    partida.addPlayer(personaje, equipo);
                    
                    System.out.println("Cliente " + num_cliente + " eligió " + clase);
                    clases_recibidas++;
                }
            }
        }
        
        System.out.println("Todos los jugadores eligieron clase!");
    }
    
    /**
     * Bucle principal del juego.
     * Gestiona los turnos, recibe acciones de los jugadores, actualiza el estado
     * y procesa mensajes de chat. El bucle continúa hasta que haya un ganador.
     * 
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ClassNotFoundException Si no se puede deserializar un mensaje
     */
    private void jugar() throws IOException, ClassNotFoundException 
    {
        while (!partida.getState().equals("finished")) 
        {
            while (!partida.getCurrentPlayer().isAlive()) 
            {
                partida.nextTurn();
            }
            
            int turno = partida.getCurrentTurn();
            System.out.println("\n--- Turno del jugador " + turno + " ---");
            
            enviarEstado();
            
            Mensaje msg_turno = new Mensaje(TipoMensaje.TURNO, "Tu turno");
            enviarA(msg_turno, clientes_ip[turno], clientes_puerto[turno]);
            
            boolean accion_valida = false;
            while (!accion_valida) 
            {
                byte[] recibidos = new byte[2048];
                DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
                socket.receive(paquete);
                
                ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Mensaje msg = (Mensaje) ois.readObject();
                ois.close();
                
                if (msg.getTipo() == TipoMensaje.ACCION) 
                {
                    String[] partes = msg.getDatos().split("\\|");
                    int accion = Integer.parseInt(partes[0]);
                    int obj1 = Integer.parseInt(partes[1]);
                    int obj2 = Integer.parseInt(partes[2]);
                    
                    accion_valida = partida.executeAction(accion, obj1, obj2);
                    
                    if (!accion_valida) 
                    {
                        Mensaje error = new Mensaje(TipoMensaje.ERROR, "Accion invalida");
                        enviarA(error, clientes_ip[turno], clientes_puerto[turno]);
                    }
                } 
                else if (msg.getTipo() == TipoMensaje.MENSAJE_CHAT) 
                {
                    enviarATodos(new Mensaje(TipoMensaje.CHAT_GLOBAL, msg.getDatos()));
                }
            }
            
            if (partida.checkWinner()) 
            {
                int ganador = partida.getWinnerTeam();
                Mensaje fin = new Mensaje(TipoMensaje.FIN_PARTIDA, "Gana equipo " + ganador);
                enviarATodos(fin);
                System.out.println("FIN - Ganó equipo " + ganador);
                break;
            }
            
            partida.nextTurn();
        }
        
        socket.close();
    }
    
    /**
     * Envía el estado actualizado de la partida a todos los clientes.
     * Formato: "vida0|vida1|vida2|vida3|turno|estado"
     * 
     * @throws IOException Si ocurre un error al enviar el mensaje
     */
    private void enviarEstado() throws IOException 
    {
        String estado = "";
        for (int i = 0; i < 4; i++) 
        {
            Personaje p = partida.getPlayerByIndex(i);
            estado += p.getCurrentHealth() + "|";
        }
        estado += partida.getCurrentTurn() + "|" + partida.getState();
        
        Mensaje msg = new Mensaje(TipoMensaje.ESTADO_PARTIDA, estado);
        enviarATodos(msg);
    }
    
    /**
     * Serializa y envía un mensaje a un cliente específico.
     * 
     * @param msg Mensaje a enviar
     * @param ip Dirección IP del cliente
     * @param puerto Puerto del cliente
     * @throws IOException Si ocurre un error al enviar el paquete
     */
    private void enviarA(Mensaje msg, InetAddress ip, int puerto) throws IOException 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        oos.close();
        
        byte[] datos = baos.toByteArray();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
        socket.send(paquete);
    }
    
    /**
     * Envía un mensaje en broadcast a todos los clientes conectados.
     * 
     * @param msg Mensaje a enviar
     * @throws IOException Si ocurre un error al enviar algún paquete
     */
    private void enviarATodos(Mensaje msg) throws IOException 
    {
        for (int i = 0; i < num_clientes; i++) 
        {
            enviarA(msg, clientes_ip[i], clientes_puerto[i]);
        }
    }
    
    /**
     * Busca el índice de un cliente por su dirección IP y puerto.
     * 
     * @param ip Dirección IP del cliente
     * @param puerto Puerto del cliente
     * @return Índice del cliente (0-3) o -1 si no se encuentra
     */
    private int buscarCliente(InetAddress ip, int puerto) 
    {
        for (int i = 0; i < num_clientes; i++) 
        {
            if (clientes_ip[i].equals(ip) && clientes_puerto[i] == puerto) 
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Punto de entrada de la aplicación servidor.
     * Crea una instancia del servidor e inicia el proceso de espera de clientes.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) 
    {
        try 
        {
            ServidorUDP servidor = new ServidorUDP();
            servidor.iniciar();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}