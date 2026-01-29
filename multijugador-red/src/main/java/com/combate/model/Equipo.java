package com.combate.model;

/**
 * Clase Equipo. Representa un equipo de 2 personajes en el combate.
 * Proporciona métodos para gestionar y consultar el estado del equipo.
 * 
 * @author David Soto García
 */
public class Equipo 
{
    private Personaje player1;
    private Personaje player2;
    
    /**
     * Constructor del equipo
     * @param p1 Primer jugador del equipo
     * @param p2 Segundo jugador del equipo
     */
    public Equipo(Personaje p1, Personaje p2) 
    {
        this.player1 = p1;
        this.player2 = p2;
    }
    
    /**
     * Constructor vacío para crear equipo sin jugadores inicialmente
     */
    public Equipo() 
    {
        this.player1 = null;
        this.player2 = null;
    }
    
    /**
     * Verifica si ambos personajes del equipo están muertos
     * @return true si ambos están muertos, false en caso contrario
     */
    public boolean allDead() 
    {
        if (player1 == null || player2 == null) 
        {
            return false;
        }
        return !player1.isAlive() && !player2.isAlive();
    }
    
    /**
     * Cuenta cuántos personajes del equipo están vivos
     * @return Número de personajes vivos (0, 1 o 2)
     */
    public int countAlive() 
    {
        int count = 0;
        
        if (player1 != null && player1.isAlive()) 
        {
            count++;
        }
        
        if (player2 != null && player2.isAlive()) 
        {
            count++;
        }
        
        return count;
    }
    
    /**
     * Obtiene un personaje por su índice en el equipo
     * @param index Índice del personaje (0 = player1, 1 = player2)
     * @return El personaje en esa posición o null si el índice es inválido
     */
    public Personaje getPlayer(int index) 
    {
        if (index == 0) 
        {
            return player1;
        } 
        else if (index == 1) 
        {
            return player2;
        }
        return null;
    }
    
    /**
     * Establece un jugador en una posición específica
     * @param character Personaje a establecer
     * @param index Posición (0 o 1)
     */
    public void setPlayer(Personaje character, int index) 
    {
        if (index == 0) 
        {
            this.player1 = character;
        } 
        else if (index == 1) 
        {
            this.player2 = character;
        }
    }
    
    /**
     * Verifica si el equipo está completo (tiene 2 jugadores)
     * @return true si tiene 2 jugadores, false en caso contrario
     */
    public boolean isComplete() 
    {
        return player1 != null && player2 != null;
    }
    
    // Getters
    public Personaje getPlayer1() 
    {
        return player1;
    }
    
    public Personaje getPlayer2() 
    {
        return player2;
    }
}