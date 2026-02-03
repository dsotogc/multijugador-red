package com.combate.view;

import javax.swing.*;
import java.awt.*;
import com.combate.client.ClienteUDP;

/**
 * Panel de selección de clase mejorado con descripción de habilidades.
 * 
 * @author David Soto García
 */
public class PanelSeleccionClase extends JPanel 
{
    private VentanaJuego ventana;
    private ClienteUDP cliente;
    
    private JButton btn_luchador;
    private JButton btn_mago;
    private JButton btn_curandero;
    private JLabel lbl_espera;
    
    public PanelSeleccionClase(VentanaJuego ventana, ClienteUDP cliente) 
    {
        this.ventana = ventana;
        this.cliente = cliente;
        
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 50));
        
        JPanel panel_titulo = new JPanel();
        panel_titulo.setBackground(new Color(40, 40, 50));
        JLabel titulo = new JLabel("ELIGE TU CLASE");
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        panel_titulo.add(titulo);
        
        JPanel panel_botones = new JPanel();
        panel_botones.setLayout(new GridLayout(1, 3, 15, 0));
        panel_botones.setBackground(new Color(40, 40, 50));
        panel_botones.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        btn_luchador = crearBotonClaseDetallado(
            "LUCHADOR",
            "100 HP | 10 DEF | 20 ATK",
            new String[]{
                "⚔ Ataque: 20 de daño",
                "🛡 Defensa: +10 DEF",
                "💥 Ataque Potenciado: 25 daño, -5 DEF"
            },
            new Color(200, 50, 50),
            "Luchador"
        );
        
        btn_mago = crearBotonClaseDetallado(
            "MAGO",
            "80 HP | 0 DEF | 30 ATK",
            new String[]{
                "⚔ Ataque: 30 de daño",
                "💀 Sacrificio: Pierde 50% HP, 60 daño",
                "💥 Ataque en Área: 15 daño a 2 enemigos"
            },
            new Color(50, 100, 200),
            "Mago"
        );
        
        btn_curandero = crearBotonClaseDetallado(
            "CURANDERO",
            "90 HP | 8 DEF | 15 ATK",
            new String[]{
                "⚔ Ataque: 15 de daño",
                "💚 Curación: +20 HP aliado",
                "✨ Megacuración: Muere, revive aliado completo"
            },
            new Color(50, 200, 100),
            "Curandero"
        );
        
        btn_luchador.setEnabled(false);
        btn_mago.setEnabled(false);
        btn_curandero.setEnabled(false);
        
        panel_botones.add(btn_luchador);
        panel_botones.add(btn_mago);
        panel_botones.add(btn_curandero);
        
        JPanel panel_info = new JPanel();
        panel_info.setBackground(new Color(40, 40, 50));
        lbl_espera = new JLabel("Conectando...");
        lbl_espera.setFont(new Font("Arial", Font.BOLD, 18));
        lbl_espera.setForeground(Color.YELLOW);
        panel_info.add(lbl_espera);
        
        add(panel_titulo, BorderLayout.NORTH);
        add(panel_botones, BorderLayout.CENTER);
        add(panel_info, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonClaseDetallado(String nombre, String stats, String[] habilidades, Color color, String clase) 
    {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout());
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(color);
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel lbl_nombre = new JLabel(nombre);
        lbl_nombre.setFont(new Font("Arial", Font.BOLD, 22));
        lbl_nombre.setForeground(Color.WHITE);
        lbl_nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lbl_stats = new JLabel(stats);
        lbl_stats.setFont(new Font("Arial", Font.BOLD, 13));
        lbl_stats.setForeground(Color.WHITE);
        lbl_stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contenido.add(lbl_nombre);
        contenido.add(Box.createRigidArea(new Dimension(0, 5)));
        contenido.add(lbl_stats);
        contenido.add(Box.createRigidArea(new Dimension(0, 15)));
        
        for (String hab : habilidades) 
        {
            JLabel lbl_hab = new JLabel(hab);
            lbl_hab.setFont(new Font("Arial", Font.PLAIN, 12));
            lbl_hab.setForeground(Color.WHITE);
            lbl_hab.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenido.add(lbl_hab);
            contenido.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        boton.add(contenido, BorderLayout.CENTER);
        
        boton.addActionListener(e -> {
            try 
            {
                cliente.enviarClase(clase);
                cliente.setMiClase(clase);
                btn_luchador.setEnabled(false);
                btn_mago.setEnabled(false);
                btn_curandero.setEnabled(false);
                lbl_espera.setText("Esperando a otros jugadores...");
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        });
        
        return boton;
    }
    
    public void habilitarSeleccion() 
    {
        SwingUtilities.invokeLater(() -> {
            btn_luchador.setEnabled(true);
            btn_mago.setEnabled(true);
            btn_curandero.setEnabled(true);
            lbl_espera.setText("¡Todos conectados! Elige tu clase");
        });
    }
    
    public void mostrarMensajeEspera(String mensaje) 
    {
        SwingUtilities.invokeLater(() -> lbl_espera.setText(mensaje));
    }
}