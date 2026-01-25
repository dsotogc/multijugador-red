package com.combate.model;

/**
 * Clase Luchador.
 * Cuenta con 100 puntos de vida inicial, 10 de defensa y 20 de ataque base.
 * Acciones:
 * 	->Ataque: Inflinge 20 pt de daño a un objetivo.
 * 	->Defensa: Aumenta su defensa en 10.
 * 	->Ataque potenciado: Hace 25 de daño, pero disminuye defensa en 5.
 * 
 * @author David Soto García
 */
public class Luchador extends Personaje{
	
	/**
	 * Estadísticas predefinidas para el luchador.
	 */
	public Luchador()
	{
		super(100, 10,20);
	}

	/**
	 * Ataque básico. Inflige daño a objetivo seleccionado.
	 * @param obj Personaje que recibirá el daño
	 */
	public void atacar(Personaje obj)
	{
		int basedmg = 20;

		if (this.isAlive() && obj != null && obj.isAlive())
			obj.getDamage(basedmg);
	}

	/**
	 * Aumenta su defensa en 10 puntos.
	 */
	public void defender()
	{
		int inc_defense = 10;

		if (this.isAlive())
			this.defense += inc_defense;
	}

	/**
	 * Lanza ataque potenciado, inflingiendo 25 de daño pero disminuyendo defensa en 5.
	 * 
	 * @param obj Personaje que recibirá el daño
	 */
	public void atacar_boost(Personaje obj)
	{
		int dmg = 25;
		int def = 5;

		if (this.isAlive() && obj != null && obj.isAlive())
		{
			obj.getDamage(dmg);
			this.defense -= def;
		}
	}

}
