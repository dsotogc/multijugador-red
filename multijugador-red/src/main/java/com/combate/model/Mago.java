package com.combate.model;

/**
 * Clase mago.
 * Sus características base son: 80 puntos de vida, 0 de defensa y
 * 30 de ataque.
 * Acciones:
 * ->Ataque: Inflinge 30 pt de daño a un objetivo.
 * ->Ataque potenciado: Su vida se reduce a la mitad, pero hace 60 de
 * daño.
 * ->Ataque en area: Si los dos enemigos están vivos hace 15 de daño
 * a cada uno.
 * 
 * @author David Soto García
 */
public class Mago extends Personaje {

	/**
	 * Características base.
	 */
	public Mago() 
	{
		super(80, 0, 30);
	}

	/**
	 * Ataque básico mágico. Inflige daño a objetivo seleccionado.
	 * 
	 * @param obj Personaje que recibirá el daño
	 */
	public void atacar(Personaje obj) 
	{
		int basedmg = 30;
		if (this.isAlive() && obj != null && obj.isAlive())
			obj.getDamage(basedmg);
	}

	/**
	 * Ataque potenciado. Su vida se reduce a la mitad pero inflige 
	 * 60 de daño.
	 * 
	 * @param obj Personaje que recibirá el daño
	 */
	public void atacar_boost(Personaje obj) 
	{
		int dmg = 60;
		if (this.isAlive() && obj != null && obj.isAlive()) {
			this.current_health = this.current_health / 2;
			obj.getDamage(dmg);
		}
	}

	/**
	 * Ataque en área. Si los dos enemigos están vivos, hace 15 de 
	 * daño a cada uno.
	 * 
	 * @param obj1 Primer enemigo
	 * @param obj2 Segundo enemigo
	 */
	public void atacar_area(Personaje obj1, Personaje obj2) 
	{
		int dmg = 15;
		if (this.isAlive() && obj1 != null && obj2 != null) 
		{
			if (obj1.isAlive() && obj2.isAlive()) 
			{
				obj1.getDamage(dmg);
				obj2.getDamage(dmg);
			}
		}
	}
}
