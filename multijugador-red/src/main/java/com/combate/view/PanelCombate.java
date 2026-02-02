package com.combate.view;

import javax.swing.*;
import java.awt.*;
import com.combate.client.ClienteUDP;

/**
 * Panel principal del combate. Muestra personajes, barras de vida y botones de acción.
 * 
 * @author David Soto García
 */
public class PanelCombate extends JPanel 
{
    private VentanaJuego ventana;
    private ClienteUDP cliente;
    
    private JProgressBar[] barras_vida = new JProgressBar[4];
    private JLabel[] labels_jugadores = new JLabel[4];
    private JButton[] botones_jugadores = new JButton[4];
    
    private JButton btn_accion1;
    private JButton btn_accion2;
    private JButton btn_accion3;
    
    private JLabel lbl_turno;
    private JTextArea area_log;
    
    private int accion_seleccionada = -1;
    
    public PanelCombate(VentanaJuego ventana, ClienteUDP cliente) 
    {
        this.ventana = ventana;
        this.cliente = cliente;
        
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));
        
        JPanel panel_equipo1 = crearPanelEquipo("EQUIPO 1", 0, 1, new Color(200, 50, 50));
        JPanel panel_equipo2 = crearPanelEquipo("EQUIPO 2", 2, 3, new Color(50, 100, 200));
        
        JPanel panel_centro = new JPanel(new BorderLayout());
        panel_centro.setBackground(new Color(30, 30, 40));
        panel_centro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lbl_turno = new JLabel("Esperando inicio...", SwingConstants.CENTER);
        lbl_turno.setFont(new Font("Arial", Font.BOLD, 20));
        lbl_turno.setForeground(Color.YELLOW);
        lbl_turno.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        area_log = new JTextArea(8, 40);
        area_log.setEditable(false);
        area_log.setBackground(new Color(20, 20, 30));
        area_log.setForeground(Color.WHITE);
        area_log.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll_log = new JScrollPane(area_log);
        
        JPanel panel_acciones = crearPanelAcciones();
        
        panel_centro.add(lbl_turno, BorderLayout.NORTH);
        panel_centro.add(scroll_log, BorderLayout.CENTER);
        panel_centro.add(panel_acciones, BorderLayout.SOUTH);
        
        add(panel_equipo1, BorderLayout.NORTH);
        add(panel_centro, BorderLayout.CENTER);
        add(panel_equipo2, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelEquipo(String nombre, int idx1, int idx2, Color color) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color.darker());
        panel.setBorder(BorderFactory.createLineBorder(color, 3));
        
        JLabel titulo = new JLabel(nombre, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JPanel panel_jugadores = new JPanel(new GridLayout(1, 2, 20, 0));
        panel_jugadores.setBackground(color.darker());
        panel_jugadores.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        panel_jugadores.add(crearPanelJugador(idx1, color));
        panel_jugadores.add(crearPanelJugador(idx2, color));
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panel_jugadores, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelJugador(int indice, Color color) 
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(color.darker().darker());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        labels_jugadores[indice] = new JLabel("Jugador " + indice, SwingConstants.CENTER);
        labels_jugadores[indice].setFont(new Font("Arial", Font.BOLD, 16));
        labels_jugadores[indice].setForeground(Color.WHITE);
        
        barras_vida[indice] = new JProgressBar(0, 100);
        barras_vida[indice].setValue(100);
        barras_vida[indice].setStringPainted(true);
        barras_vida[indice].setString("100 HP");
        barras_vida[indice].setForeground(new Color(50, 200, 50));
        barras_vida[indice].setBackground(new Color(100, 100, 100));
        barras_vida[indice].setFont(new Font("Arial", Font.BOLD, 14));
        
        botones_jugadores[indice] = new JButton("Objetivo " + indice);
        botones_jugadores[indice].setEnabled(false);
        botones_jugadores[indice].setFocusPainted(false);
        
        int idx = indice;
        botones_jugadores[indice].addActionListener(e -> seleccionarObjetivo(idx));
        
        panel.add(labels_jugadores[indice], BorderLayout.NORTH);
        panel.add(barras_vida[indice], BorderLayout.CENTER);
        panel.add(botones_jugadores[indice], BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelAcciones() 
    {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(new Color(30, 30, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        
        btn_accion1 = new JButton("Acción 1");
        btn_accion2 = new JButton("Acción 2");
        btn_accion3 = new JButton("Acción 3");
        
        JButton[] botones = {btn_accion1, btn_accion2, btn_accion3};
        for (JButton btn : botones) 
        {
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setEnabled(false);
        }
        
        btn_accion1.addActionListener(e -> prepararAccion(1));
        btn_accion2.addActionListener(e -> prepararAccion(2));
        btn_accion3.addActionListener(e -> prepararAccion(3));
        
        panel.add(btn_accion1);
        panel.add(btn_accion2);
        panel.add(btn_accion3);
        
        return panel;
    }
    
    private void prepararAccion(int accion) 
    {
        accion_seleccionada = accion;
        agregarLog("Acción " + accion + " seleccionada. Elige objetivo(s).");
        
        btn_accion1.setEnabled(false);
        btn_accion2.setEnabled(false);
        btn_accion3.setEnabled(false);
        
        for (int i = 0; i < 4; i++) 
        {
            botones_jugadores[i].setEnabled(true);
        }
    }
    
    private void seleccionarObjetivo(int objetivo) 
    {
        try 
        {
            cliente.enviarAccion(accion_seleccionada, objetivo, -1);
            agregarLog("Acción enviada contra Jugador " + objetivo);
            
            for (int i = 0; i < 4; i++) 
            {
                botones_jugadores[i].setEnabled(false);
            }
            
            accion_seleccionada = -1;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void actualizarVida(int indice, int vida) 
    {
        SwingUtilities.invokeLater(() -> {
            barras_vida[indice].setValue(vida);
            barras_vida[indice].setString(vida + " HP");
            
            if (vida > 50) 
            {
                barras_vida[indice].setForeground(new Color(50, 200, 50));
            } 
            else if (vida > 25) 
            {
                barras_vida[indice].setForeground(new Color(200, 200, 50));
            } 
            else if (vida > 0) 
            {
                barras_vida[indice].setForeground(new Color(200, 50, 50));
            } 
            else 
            {
                barras_vida[indice].setForeground(Color.GRAY);
                labels_jugadores[indice].setText("Jugador " + indice + " (MUERTO)");
            }
        });
    }
    
    public void habilitarAcciones(boolean habilitado) 
    {
        SwingUtilities.invokeLater(() -> {
            btn_accion1.setEnabled(habilitado);
            btn_accion2.setEnabled(habilitado);
            btn_accion3.setEnabled(habilitado);
        });
    }
    
    public void actualizarTurno(String mensaje) 
    {
        SwingUtilities.invokeLater(() -> lbl_turno.setText(mensaje));
    }
    
    public void agregarLog(String mensaje) 
    {
        SwingUtilities.invokeLater(() -> {
            area_log.append(mensaje + "\n");
            area_log.setCaretPosition(area_log.getDocument().getLength());
        });
    }
}