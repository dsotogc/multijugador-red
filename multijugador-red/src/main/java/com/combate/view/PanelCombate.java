package com.combate.view;

import javax.swing.*;
import java.awt.*;
import com.combate.client.ClienteUDP;

/**
 * Panel de combate mejorado con acciones específicas por clase y validación de objetivos.
 * 
 * @author David Soto García
 */
public class PanelCombate extends JPanel 
{
    private VentanaJuego ventana;
    private ClienteUDP cliente;
    
    private int mi_numero;
    private int mi_equipo;
    private String mi_clase;
    
    private JProgressBar[] barras_vida = new JProgressBar[4];
    private JLabel[] labels_jugadores = new JLabel[4];
    private JButton[] botones_objetivo = new JButton[4];
    
    private JButton btn_accion1;
    private JButton btn_accion2;
    private JButton btn_accion3;
    
    private JLabel lbl_mi_clase;
    private JLabel lbl_turno;
    private JTextArea area_log;
    private JPanel panel_habilidades;
    
    private int accion_seleccionada = -1;
    
    public PanelCombate(VentanaJuego ventana, ClienteUDP cliente) 
    {
        this.ventana = ventana;
        this.cliente = cliente;
        
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));
        
        JPanel panel_superior = new JPanel(new BorderLayout());
        panel_superior.setBackground(new Color(30, 30, 40));
        
        lbl_mi_clase = new JLabel("", SwingConstants.CENTER);
        lbl_mi_clase.setFont(new Font("Arial", Font.BOLD, 24));
        lbl_mi_clase.setForeground(Color.YELLOW);
        lbl_mi_clase.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel_superior.add(lbl_mi_clase, BorderLayout.NORTH);
        
        JPanel panel_equipos = new JPanel(new GridLayout(2, 1, 0, 10));
        panel_equipos.setBackground(new Color(30, 30, 40));
        panel_equipos.add(crearPanelEquipo("EQUIPO 1", 0, 1, new Color(200, 50, 50)));
        panel_equipos.add(crearPanelEquipo("EQUIPO 2", 2, 3, new Color(50, 100, 200)));
        panel_superior.add(panel_equipos, BorderLayout.CENTER);
        
        JPanel panel_inferior = new JPanel(new BorderLayout());
        panel_inferior.setBackground(new Color(30, 30, 40));
        
        lbl_turno = new JLabel("Esperando inicio...", SwingConstants.CENTER);
        lbl_turno.setFont(new Font("Arial", Font.BOLD, 18));
        lbl_turno.setForeground(Color.YELLOW);
        lbl_turno.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        area_log = new JTextArea(6, 40);
        area_log.setEditable(false);
        area_log.setBackground(new Color(20, 20, 30));
        area_log.setForeground(Color.WHITE);
        area_log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll_log = new JScrollPane(area_log);
        
        panel_habilidades = new JPanel();
        panel_habilidades.setBackground(new Color(30, 30, 40));
        
        panel_inferior.add(lbl_turno, BorderLayout.NORTH);
        panel_inferior.add(scroll_log, BorderLayout.CENTER);
        panel_inferior.add(panel_habilidades, BorderLayout.SOUTH);
        
        add(panel_superior, BorderLayout.CENTER);
        add(panel_inferior, BorderLayout.SOUTH);
    }
    
    public void configurarJuego(int mi_numero, int mi_equipo, String mi_clase) 
    {
        this.mi_numero = mi_numero;
        this.mi_equipo = mi_equipo;
        this.mi_clase = mi_clase;
        
        SwingUtilities.invokeLater(() -> {
            lbl_mi_clase.setText("Eres " + mi_clase + " - Jugador " + mi_numero + " (Equipo " + mi_equipo + ")");
            crearBotonesHabilidades();
        });
    }
    
    private void crearBotonesHabilidades() 
    {
        panel_habilidades.removeAll();
        panel_habilidades.setLayout(new GridLayout(1, 3, 10, 0));
        panel_habilidades.setBorder(BorderFactory.createEmptyBorder(10, 50, 15, 50));
        
        if (mi_clase.equals("Luchador")) 
        {
            btn_accion1 = crearBotonHabilidad("⚔ ATAQUE", "20 de daño a enemigo", new Color(180, 60, 60));
            btn_accion2 = crearBotonHabilidad("🛡 DEFENSA", "+10 a tu defensa", new Color(100, 100, 180));
            btn_accion3 = crearBotonHabilidad("💥 ATAQUE POTENCIADO", "25 daño, -5 DEF", new Color(200, 100, 0));
        } 
        else if (mi_clase.equals("Mago")) 
        {
            btn_accion1 = crearBotonHabilidad("⚔ ATAQUE", "30 de daño a enemigo", new Color(100, 120, 200));
            btn_accion2 = crearBotonHabilidad("💀 SACRIFICIO", "Pierdes 50% HP, 60 daño", new Color(150, 50, 150));
            btn_accion3 = crearBotonHabilidad("💥 ATAQUE EN ÁREA", "15 daño a 2 enemigos", new Color(200, 100, 0));
        } 
        else if (mi_clase.equals("Curandero")) 
        {
            btn_accion1 = crearBotonHabilidad("⚔ ATAQUE", "15 de daño a enemigo", new Color(100, 180, 120));
            btn_accion2 = crearBotonHabilidad("💚 CURACIÓN", "+20 HP a aliado o ti", new Color(50, 200, 100));
            btn_accion3 = crearBotonHabilidad("✨ MEGACURACIÓN", "Mueres, revives aliado", new Color(100, 250, 150));
        }
        
        btn_accion1.addActionListener(e -> prepararAccion(1));
        btn_accion2.addActionListener(e -> prepararAccion(2));
        btn_accion3.addActionListener(e -> prepararAccion(3));
        
        panel_habilidades.add(btn_accion1);
        panel_habilidades.add(btn_accion2);
        panel_habilidades.add(btn_accion3);
        
        btn_accion1.setEnabled(false);
        btn_accion2.setEnabled(false);
        btn_accion3.setEnabled(false);
        
        panel_habilidades.revalidate();
        panel_habilidades.repaint();
    }
    
    private JButton crearBotonHabilidad(String nombre, String descripcion, Color color) 
    {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout());
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lbl_nombre = new JLabel(nombre, SwingConstants.CENTER);
        lbl_nombre.setFont(new Font("Arial", Font.BOLD, 14));
        lbl_nombre.setForeground(Color.WHITE);
        
        JLabel lbl_desc = new JLabel("<html><center>" + descripcion + "</center></html>", SwingConstants.CENTER);
        lbl_desc.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl_desc.setForeground(Color.WHITE);
        
        boton.add(lbl_nombre, BorderLayout.NORTH);
        boton.add(lbl_desc, BorderLayout.CENTER);
        
        return boton;
    }
    
    private JPanel crearPanelEquipo(String nombre, int idx1, int idx2, Color color) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color.darker());
        panel.setBorder(BorderFactory.createLineBorder(color, 3));
        
        JLabel titulo = new JLabel(nombre, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        
        JPanel panel_jugadores = new JPanel(new GridLayout(1, 2, 15, 0));
        panel_jugadores.setBackground(color.darker());
        panel_jugadores.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        panel_jugadores.add(crearPanelJugador(idx1, color));
        panel_jugadores.add(crearPanelJugador(idx2, color));
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panel_jugadores, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelJugador(int indice, Color color) 
    {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setBackground(color.darker().darker());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        labels_jugadores[indice] = new JLabel("Jugador " + indice, SwingConstants.CENTER);
        labels_jugadores[indice].setFont(new Font("Arial", Font.BOLD, 14));
        labels_jugadores[indice].setForeground(Color.WHITE);
        
        barras_vida[indice] = new JProgressBar(0, 100);
        barras_vida[indice].setValue(100);
        barras_vida[indice].setStringPainted(true);
        barras_vida[indice].setString("100 HP");
        barras_vida[indice].setForeground(new Color(50, 200, 50));
        barras_vida[indice].setFont(new Font("Arial", Font.BOLD, 12));
        
        botones_objetivo[indice] = new JButton("Seleccionar");
        botones_objetivo[indice].setEnabled(false);
        botones_objetivo[indice].setFocusPainted(false);
        botones_objetivo[indice].setFont(new Font("Arial", Font.BOLD, 11));
        
        int idx = indice;
        botones_objetivo[indice].addActionListener(e -> seleccionarObjetivo(idx));
        
        panel.add(labels_jugadores[indice], BorderLayout.NORTH);
        panel.add(barras_vida[indice], BorderLayout.CENTER);
        panel.add(botones_objetivo[indice], BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void prepararAccion(int accion) 
    {
        accion_seleccionada = accion;
        
        btn_accion1.setEnabled(false);
        btn_accion2.setEnabled(false);
        btn_accion3.setEnabled(false);
        
        int[] objetivos_validos = obtenerObjetivosValidos(accion);
        
        for (int i = 0; i < 4; i++) 
        {
            botones_objetivo[i].setEnabled(false);
        }
        
        for (int idx : objetivos_validos) 
        {
            if (idx >= 0 && idx < 4) 
            {
                botones_objetivo[idx].setEnabled(true);
            }
        }
        
        agregarLog("Selecciona objetivo para tu habilidad");
    }
    
    private int[] obtenerObjetivosValidos(int accion) 
    {
        int compañero = (mi_numero % 2 == 0) ? mi_numero + 1 : mi_numero - 1;
        int enemigo1 = (mi_equipo == 1) ? 2 : 0;
        int enemigo2 = (mi_equipo == 1) ? 3 : 1;
        
        if (mi_clase.equals("Luchador")) 
        {
            if (accion == 1 || accion == 3) 
            {
                return new int[]{enemigo1, enemigo2};
            } 
            else if (accion == 2) 
            {
                return new int[]{mi_numero};
            }
        } 
        else if (mi_clase.equals("Mago")) 
        {
            if (accion == 1 || accion == 2) 
            {
                return new int[]{enemigo1, enemigo2};
            } 
            else if (accion == 3) 
            {
                return new int[]{enemigo1, enemigo2};
            }
        } 
        else if (mi_clase.equals("Curandero")) 
        {
            if (accion == 1) 
            {
                return new int[]{enemigo1, enemigo2};
            } 
            else if (accion == 2) 
            {
                return new int[]{mi_numero, compañero};
            } 
            else if (accion == 3) 
            {
                return new int[]{compañero};
            }
        }
        
        return new int[]{};
    }
    
    private void seleccionarObjetivo(int objetivo) 
    {
        try 
        {
            cliente.enviarAccion(accion_seleccionada, objetivo, -1);
            agregarLog("Acción ejecutada sobre Jugador " + objetivo);
            
            for (int i = 0; i < 4; i++) 
            {
                botones_objetivo[i].setEnabled(false);
            }
            
            accion_seleccionada = -1;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void activarTurno() 
    {
        SwingUtilities.invokeLater(() -> {
            lbl_turno.setText("*** ES TU TURNO ***");
            btn_accion1.setEnabled(true);
            btn_accion2.setEnabled(true);
            btn_accion3.setEnabled(true);
        });
    }
    
    public void actualizarTurno(int turno_actual) 
    {
        SwingUtilities.invokeLater(() -> {
            if (turno_actual != mi_numero) 
            {
                lbl_turno.setText("Turno del Jugador " + turno_actual);
                btn_accion1.setEnabled(false);
                btn_accion2.setEnabled(false);
                btn_accion3.setEnabled(false);
            }
        });
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
    
    public void finalizarPartida(String mensaje) 
    {
        SwingUtilities.invokeLater(() -> {
            lbl_turno.setText(mensaje);
            btn_accion1.setEnabled(false);
            btn_accion2.setEnabled(false);
            btn_accion3.setEnabled(false);
        });
    }
    
    public void agregarLog(String mensaje) 
    {
        SwingUtilities.invokeLater(() -> {
            area_log.append(mensaje + "\n");
            area_log.setCaretPosition(area_log.getDocument().getLength());
        });
    }
}