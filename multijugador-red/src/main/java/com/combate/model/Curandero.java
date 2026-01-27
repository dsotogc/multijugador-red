package com.combate.model;

/**
 * Clase Curandero.
 * Cuenta con 90 puntos de vida inicial, 5 de defensa y 15 de ataque 
 * base.
 * Acciones:
 * ->Ataque: Inflinge 15pt de daño a un objetivo.
 * ->Curación: Cura a él mismo o a su compañero 20 de vida.
 * ->Megacuración: Pierde el 100% de su vida (muere) pero su compañero
 * restaura toda la vida o revive con la mitad de su vida máxima si 
 * estaba muerto.
 * 
 * @author David Soto García
 */
public class Curandero extends Personaje {

	/**
	 * Características base.
	 */
	public Curandero()
	{
		super(90, 5, 15);
	}

	/**
	 * Daña a objetivo seleccionado.
	 * @param obj Personaje que recibirá el daño
	 */
	public void atacar(Personaje obj)
	{
		int basedmg = 15;

		if (this.isAlive() && obj != null && obj.isAlive())
			obj.getDamage(basedmg);
	}

	/**
	 * Restaura 20pt de vida a si mismo o a su aliado.
	 * @param obj Personaje que recibirá la curación, puede ser él
	 * mismo o su aliado.
	 */
	public void curar(Personaje obj)
	{
		int healpt = 20;

		if (this.isAlive() && obj != null && obj.isAlive())
			obj.heal(healpt);
	}

	/**
	 * El curandero se sacrifica. Su aliado restaura el 100% de su 
	 * vida en caso de estar vivo o la mitad en caso de estar muerto.
	 * @param obj Personaje que va a revivir o restaurar su vida por 
	 * completo
	 */
	public void megacurar(Personaje obj)
	{
		if (this.isAlive() && obj != null)
		{
			this.current_health = 0;
			this.alive = false;

			if (obj.isAlive())
				obj.heal(obj.getMaxHealth());
			else
				obj.revive();
		}
	}
	
}
