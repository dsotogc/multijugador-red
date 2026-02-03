package com.combate.view;

import javax.swing.*;
import java.awt.*;
import com.combate.client.ClienteUDP;

/**
 * Ventana principal del juego. Gestiona la navegación entre paneles.
 * 
 * @author David Soto García
 */
public class VentanaJuego extends JFrame 
{
    private CardLayout card_layout;
    private JPanel panel_contenedor;
    
    private PanelSeleccionClase panel_seleccion;
    private PanelCombate panel_combate;
    private PanelVictoria panel_victoria;
    
    private ClienteUDP cliente;
    
    public VentanaJuego(ClienteUDP cliente) 
    {
        this.cliente = cliente;
        
        setTitle("Combate por Turnos 2v2");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        card_layout = new CardLayout();
        panel_contenedor = new JPanel(card_layout);
        
        panel_seleccion = new PanelSeleccionClase(this, cliente);
        panel_combate = new PanelCombate(this, cliente);
        panel_victoria = new PanelVictoria(this);
        
        panel_contenedor.add(panel_seleccion, "SELECCION");
        panel_contenedor.add(panel_combate, "COMBATE");
        panel_contenedor.add(panel_victoria, "VICTORIA");
        
        add(panel_contenedor);
        
        mostrarSeleccion();
        setVisible(true);
    }
    
    public void mostrarSeleccion() 
    {
        card_layout.show(panel_contenedor, "SELECCION");
    }
    
    public void mostrarCombate() 
    {
        card_layout.show(panel_contenedor, "COMBATE");
    }
    
    public void mostrarVictoria(int equipo_ganador, boolean es_mi_equipo) 
    {
        panel_victoria.mostrarResultado(equipo_ganador, es_mi_equipo);
        card_layout.show(panel_contenedor, "VICTORIA");
    }
    
    public PanelCombate getPanelCombate() 
    {
        return panel_combate;
    }
    
    public PanelSeleccionClase getPanelSeleccion() 
    {
        return panel_seleccion;
    }
}