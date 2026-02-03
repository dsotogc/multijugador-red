package com.combate.view;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de victoria que se muestra al finalizar la partida.
 * Muestra el equipo ganador de forma vistosa.
 * 
 * @author David Soto García
 */
public class PanelVictoria extends JPanel {
	private VentanaJuego ventana;
	private JLabel lbl_resultado;
	private JLabel lbl_mensaje;

	public PanelVictoria(VentanaJuego ventana) {
		this.ventana = ventana;

		setLayout(new BorderLayout());
		setBackground(new Color(20, 20, 30));

		JPanel panel_centro = new JPanel();
		panel_centro.setLayout(new BoxLayout(panel_centro, BoxLayout.Y_AXIS));
		panel_centro.setBackground(new Color(20, 20, 30));

		lbl_resultado = new JLabel("", SwingConstants.CENTER);
		lbl_resultado.setFont(new Font("Arial", Font.BOLD, 72));
		lbl_resultado.setAlignmentX(Component.CENTER_ALIGNMENT);

		lbl_mensaje = new JLabel("", SwingConstants.CENTER);
		lbl_mensaje.setFont(new Font("Arial", Font.PLAIN, 28));
		lbl_mensaje.setForeground(Color.WHITE);
		lbl_mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel_centro.add(Box.createVerticalGlue());
		panel_centro.add(lbl_resultado);
		panel_centro.add(Box.createRigidArea(new Dimension(0, 30)));
		panel_centro.add(lbl_mensaje);
		panel_centro.add(Box.createVerticalGlue());

		add(panel_centro, BorderLayout.CENTER);
	}

	/**
	 * Muestra el resultado de la partida
	 * 
	 * @param equipo_ganador 1 o 2
	 * @param es_mi_equipo   true si ganó tu equipo
	 */
	public void mostrarResultado(int equipo_ganador, boolean es_mi_equipo) {
		SwingUtilities.invokeLater(() -> {
			if (es_mi_equipo) {
				lbl_resultado.setText("¡VICTORIA!");
				lbl_resultado.setForeground(new Color(50, 255, 50));
				lbl_mensaje.setText("Tu equipo ha ganado la batalla");
				setBackground(new Color(20, 40, 20));
			} else {
				lbl_resultado.setText("DERROTA");
				lbl_resultado.setForeground(new Color(255, 50, 50));
				lbl_mensaje.setText("Tu equipo ha sido derrotado");
				setBackground(new Color(40, 20, 20));
			}

			iniciarAnimacion();
		});
	}

	/**
	 * Animación simple de parpadeo del texto
	 */
	private void iniciarAnimacion() {
		Timer timer = new Timer(500, e -> {
			lbl_resultado.setVisible(!lbl_resultado.isVisible());
		});
		timer.start();

		Timer stop_timer = new Timer(3000, e -> {
			((Timer) e.getSource()).stop();
			lbl_resultado.setVisible(true);
		});
		stop_timer.setRepeats(false);
		stop_timer.start();
	}
}