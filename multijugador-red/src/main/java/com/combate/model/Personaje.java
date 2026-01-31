package com.combate.model;

/**
 * Plantilla genérica de personaje. Contiene los atributos y métodos comunes de las 3 clases (Luchador, Mago y Curandero).
 * Los atributos comunes son los de vida máxima, vida actual, defensa, ataque base y estado (vivo o muerto).
 * Los métodos que usaremos en todas las clases serán los de realizar un ataque (solo a objetivos vivos), recibir daño, 
 * comprobar si está vivo.
 * 
 * @author David Soto García
 */
public abstract class Personaje {
	
	protected int max_health;
	protected int current_health;
	protected int defense;
	protected int attack;
	protected boolean alive;

	/**
	 * Constructor común para todos los personajes.
	 * @param mhealth Vida máxima
	 * @param def Defensa base
	 * @param attack Ataque base
	 */
	protected Personaje(int mhealth, int def, int attack)
	{
		this.max_health = mhealth;
		this.current_health = mhealth;
		this.defense = def;
		this.attack = attack;
		this.alive = true;
	}

	/**
	 * Comprueba si el personaje está vivo
	 * @return true si su vida actual es mayor que 0, false en caso contrario
	 */
	public boolean isAlive()
	{
		return this.current_health > 0;
	}

	/**
	 * Resta puntos de vida a la vida actual del personaje. Al daño se le resta el porcentaje de defensa.
	 * Si la vida llega a 0 o menos, muere.
	 * @param dmg Cantidad de daño a recibir
	 */
	protected void getDamage(int dmg)
	{
		this.current_health -= (dmg - (this.defense  * dmg)/ 100);
		if (this.current_health <= 0)
		{
			this.current_health = 0;
			this.alive = false;
		}
	}

	/**
	 * Cura al personaje la cantidad especificada sin superar vida máxima.
	 * @param ph Puntos de vida a recuperar
	 */
	protected void heal(int ph)
	{
		if (this.alive)
		{
			this.current_health += ph;
			if (this.current_health > this.max_health)
				this.current_health = this.max_health;
		}
	}

	/**
	 * Si el personaje está vivo, su vida volverá a ser su vida máxima. Si ya murió, revivirá con la mitad de su vida 
	 * máxima.
	 */
	protected void revive()
	{
		if (this.alive)
			this.current_health = this.max_health;
		else
		{
			this.alive = true;
			this.current_health = this.max_health / 2;
		}
	}

	public int getMaxHealth()
	{
		return this.max_health;
	}

	public int getCurrentHealth()
	{
		return this.current_health;
	}

	public int getDefense()
	{
		return this.defense;
	}

	public int getAttack()
	{
		return this.attack;
	}

	public boolean getAlive()
	{
		return this.alive;
	}

}
