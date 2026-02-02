package com.combate.view;

import javax.swing.*;
import java.awt.*;
import com.combate.client.ClienteUDP;

/**
 * Panel de selección de clase. Permite elegir entre Luchador, Mago o Curandero.
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
        panel_botones.setLayout(new GridLayout(1, 3, 20, 0));
        panel_botones.setBackground(new Color(40, 40, 50));
        panel_botones.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        btn_luchador = crearBotonClase("LUCHADOR", "100 HP | 10 DEF | 20 ATK", 
                                       new Color(200, 50, 50), "Luchador");
        btn_mago = crearBotonClase("MAGO", "80 HP | 0 DEF | 30 ATK", 
                                   new Color(50, 100, 200), "Mago");
        btn_curandero = crearBotonClase("CURANDERO", "90 HP | 8 DEF | 15 ATK", 
                                        new Color(50, 200, 100), "Curandero");
        
        btn_luchador.setEnabled(false);
        btn_mago.setEnabled(false);
        btn_curandero.setEnabled(false);
        
        panel_botones.add(btn_luchador);
        panel_botones.add(btn_mago);
        panel_botones.add(btn_curandero);
        
        JPanel panel_info = new JPanel();
        panel_info.setBackground(new Color(40, 40, 50));
        lbl_espera = new JLabel("Conectando...");
        lbl_espera.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl_espera.setForeground(Color.LIGHT_GRAY);
        panel_info.add(lbl_espera);
        
        add(panel_titulo, BorderLayout.NORTH);
        add(panel_botones, BorderLayout.CENTER);
        add(panel_info, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonClase(String nombre, String stats, Color color, String clase) 
    {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout());
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        
        JLabel lbl_nombre = new JLabel(nombre, SwingConstants.CENTER);
        lbl_nombre.setFont(new Font("Arial", Font.BOLD, 24));
        lbl_nombre.setForeground(Color.WHITE);
        lbl_nombre.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        JLabel lbl_stats = new JLabel(stats, SwingConstants.CENTER);
        lbl_stats.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl_stats.setForeground(Color.WHITE);
        lbl_stats.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        boton.add(lbl_nombre, BorderLayout.NORTH);
        boton.add(lbl_stats, BorderLayout.CENTER);
        
        boton.addActionListener(e -> {
            try 
            {
                cliente.enviarClase(clase);
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