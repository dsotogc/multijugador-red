package com.combate.server;

import java.io.*;
import java.net.*;
import com.combate.net.Mensaje;
import com.combate.net.TipoMensaje;
import com.combate.model.*;

/**
 * Servidor UDP que gestiona la partida de combate.
 * Espera a 4 clientes, asigna equipos y gestiona el juego.
 * 
 * @author David Soto García
 */
public class ServidorUDP {
	private DatagramSocket socket;
	private int puerto = 9000;

	// Lista de clientes conectados (IP + puerto)
	private InetAddress[] clientes_ip = new InetAddress[4];
	private int[] clientes_puerto = new int[4];
	private int num_clientes = 0;

	// La partida
	private Partida partida;

	public ServidorUDP() throws SocketException {
		socket = new DatagramSocket(puerto);
		partida = new Partida();
		System.out.println("Servidor iniciado en puerto " + puerto);
	}

	/**
	 * Inicia el servidor y espera clientes
	 */
	public void iniciar() throws IOException, ClassNotFoundException {
		// FASE 1: Esperar a 4 clientes
		esperarClientes();

		// FASE 2: Recibir selección de clase de cada cliente
		recibirClases();

		// FASE 3: Iniciar partida
		partida.startGame();
		enviarATodos(new Mensaje(TipoMensaje.INICIO_PARTIDA, "La partida comienza!"));
		System.out.println("Partida iniciada!");

		// FASE 4: Jugar (bucle de turnos)
		jugar();
	}

	/**
	 * Espera a que se conecten 4 clientes
	 */
	private void esperarClientes() throws IOException, ClassNotFoundException {
		System.out.println("Esperando clientes...");

		while (num_clientes < 4) {
			byte[] recibidos = new byte[1024];
			DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
			socket.receive(paquete);

			ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Mensaje msg = (Mensaje) ois.readObject();
			ois.close();

			if (msg.getTipo() == TipoMensaje.CONEXION) {
				clientes_ip[num_clientes] = paquete.getAddress();
				clientes_puerto[num_clientes] = paquete.getPort();

				int equipo = (num_clientes < 2) ? 1 : 2;

				System.out.println("Cliente " + num_clientes + " conectado. Equipo " + equipo);

				Mensaje respuesta = new Mensaje(TipoMensaje.CONFIRMACION_CONEXION,
						num_clientes + "|" + equipo);
				enviarA(respuesta, clientes_ip[num_clientes], clientes_puerto[num_clientes]);

				num_clientes++;

				// Informar a todos cuántos faltan
				if (num_clientes < 4) {
					String msg_espera = num_clientes + "/4 jugadores conectados";
					enviarATodos(new Mensaje(TipoMensaje.ESPERA, msg_espera));
				}
			}
		}

		System.out.println("4 clientes conectados!");

		enviarATodos(new Mensaje(TipoMensaje.TODOS_CONECTADOS, "Todos conectados"));
	}

	/**
	 * Recibe la clase elegida por cada cliente
	 */
	private void recibirClases() throws IOException, ClassNotFoundException {
		System.out.println("Esperando selección de clases...");
		int clases_recibidas = 0;

		while (clases_recibidas < 4) {
			byte[] recibidos = new byte[1024];
			DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
			socket.receive(paquete);

			ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Mensaje msg = (Mensaje) ois.readObject();
			ois.close();

			if (msg.getTipo() == TipoMensaje.SELECCION_CLASE) {
				String clase = msg.getDatos(); // "Luchador", "Mago" o "Curandero"

				// Identificar qué cliente envió esto
				int num_cliente = buscarCliente(paquete.getAddress(), paquete.getPort());

				if (num_cliente != -1) {
					// Crear personaje según la clase
					Personaje personaje = null;
					if (clase.equals("Luchador")) {
						personaje = new Luchador();
					} else if (clase.equals("Mago")) {
						personaje = new Mago();
					} else if (clase.equals("Curandero")) {
						personaje = new Curandero();
					}

					// Añadir a la partida
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
	 * Bucle principal del juego
	 */
	private void jugar() throws IOException, ClassNotFoundException {
		while (!partida.getState().equals("finished")) {
			// Saltar turnos de jugadores muertos
			while (!partida.getCurrentPlayer().isAlive()) {
				partida.nextTurn();
			}

			int turno = partida.getCurrentTurn();
			System.out.println("\n--- Turno del jugador " + turno + " ---");

			// Enviar estado actual a todos
			enviarEstado();

			// Notificar a quién le toca
			Mensaje msg_turno = new Mensaje(TipoMensaje.TURNO, "Tu turno");
			enviarA(msg_turno, clientes_ip[turno], clientes_puerto[turno]);

			// Esperar acción del jugador
			boolean accion_valida = false;
			while (!accion_valida) {
				byte[] recibidos = new byte[1024];
				DatagramPacket paquete = new DatagramPacket(recibidos, recibidos.length);
				socket.receive(paquete);

				ByteArrayInputStream bais = new ByteArrayInputStream(recibidos);
				ObjectInputStream ois = new ObjectInputStream(bais);
				Mensaje msg = (Mensaje) ois.readObject();
				ois.close();

				if (msg.getTipo() == TipoMensaje.ACCION) {
					// Parsear datos: "accion|objetivo1|objetivo2"
					String[] partes = msg.getDatos().split("\\|");
					int accion = Integer.parseInt(partes[0]);
					int obj1 = Integer.parseInt(partes[1]);
					int obj2 = Integer.parseInt(partes[2]);

					// Ejecutar acción
					accion_valida = partida.executeAction(accion, obj1, obj2);

					if (!accion_valida) {
						Mensaje error = new Mensaje(TipoMensaje.ERROR, "Accion invalida");
						enviarA(error, clientes_ip[turno], clientes_puerto[turno]);
					}
				}
			}

			// Verificar ganador
			if (partida.checkWinner()) {
				int ganador = partida.getWinnerTeam();
				Mensaje fin = new Mensaje(TipoMensaje.FIN_PARTIDA, "Gana equipo " + ganador);
				enviarATodos(fin);
				System.out.println("FIN - Ganó equipo " + ganador);
				break;
			}

			// Siguiente turno
			partida.nextTurn();
		}

		socket.close();
	}

	/**
	 * Envía el estado actual de la partida a todos los clientes
	 */
	private void enviarEstado() throws IOException {
		// Formato: "vida0|vida1|vida2|vida3|turno|estado"
		String estado = "";
		for (int i = 0; i < 4; i++) {
			Personaje p = partida.getPlayerByIndex(i);
			estado += p.getCurrentHealth() + "|";
		}
		estado += partida.getCurrentTurn() + "|" + partida.getState();

		Mensaje msg = new Mensaje(TipoMensaje.ESTADO_PARTIDA, estado);
		enviarATodos(msg);
	}

	/**
	 * Envía un mensaje a un cliente específico
	 */
	private void enviarA(Mensaje msg, InetAddress ip, int puerto) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(msg);
		oos.close();

		byte[] datos = baos.toByteArray();
		DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
		socket.send(paquete);
	}

	/**
	 * Envía un mensaje a todos los clientes conectados
	 */
	private void enviarATodos(Mensaje msg) throws IOException {
		for (int i = 0; i < num_clientes; i++) {
			enviarA(msg, clientes_ip[i], clientes_puerto[i]);
		}
	}

	/**
	 * Busca el índice de un cliente por su IP y puerto
	 */
	private int buscarCliente(InetAddress ip, int puerto) {
		for (int i = 0; i < num_clientes; i++) {
			if (clientes_ip[i].equals(ip) && clientes_puerto[i] == puerto) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		try {
			ServidorUDP servidor = new ServidorUDP();
			servidor.iniciar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}