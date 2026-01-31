package com.combate.net;

/**
 * Define todos los tipos de mensajes que se pueden enviar
 * entre el cliente y el servidor durante el juego.
 * 
 * Mensajes del cliente al servidor:
 * - CONEXION: Solicitud inicial de conexión
 * - SELECCION_CLASE: El jugador elige su clase (Luchador/Mago/Curandero)
 * - ACCION: El jugador ejecuta una acción en su turno
 * 
 * Mensajes del servidor al cliente:
 * - CONFIRMACION_CONEXION: Confirma la conexión y asigna equipo
 * - ESPERA: Informa cuántos jugadores faltan
 * - INICIO_PARTIDA: Notifica que la partida comienza
 * - ESTADO_PARTIDA: Envía el estado actualizado del juego
 * - TURNO: Notifica al cliente que es su turno
 * - FIN_PARTIDA: Notifica el final del juego y el ganador
 * - ERROR: Notifica un error (acción inválida, no es tu turno, etc.)
 * 
 * @author David Soto García
 */
public enum TipoMensaje 
{
    // Mensajes Cliente - Servidor
    CONEXION,
    SELECCION_CLASE,
    ACCION,
    
    // Mensajes Servidor - Cliente
    CONFIRMACION_CONEXION,
    ESPERA,
    INICIO_PARTIDA,
    ESTADO_PARTIDA,
    TURNO,
    FIN_PARTIDA,
    ERROR
}