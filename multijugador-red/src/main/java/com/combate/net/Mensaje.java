package com.combate.net;

import java.io.Serializable;

/**
 * Enviar información entre cliente y servidor.
 * 
 * @author David Soto García
 */
public class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L;

	private TipoMensaje tipo;
	private String datos;

	public Mensaje(TipoMensaje tipo, String datos) {
		this.tipo = tipo;
		this.datos = datos;
	}

	public Mensaje(TipoMensaje tipo) {
		this.tipo = tipo;
		this.datos = "";
	}

	public TipoMensaje getTipo() {
		return tipo;
	}

	public String getDatos() {
		return datos;
	}

	public void setDatos(String datos) {
		this.datos = datos;
	}
}