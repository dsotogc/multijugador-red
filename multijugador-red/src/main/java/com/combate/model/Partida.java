package com.combate.model;

/**
 * Gestiona el estado global del juego, los equipos, turnos y lógica de combate.
 * Controla el flujo del juego.
 * 
 * @author David Soto García
 */
public class Partida {

	private Equipo team1;
	private Equipo team2;
	private int current_turn;      // 0-3 (índice del jugador que tiene el turno)
	private String state;          // "waiting", "playing", "finished"
	private int winner_team;       // 0 (ninguno), 1 (team1 gana), 2 (team2 gana)
	
	public Partida() 
	{
		this.team1 = new Equipo();
		this.team2 = new Equipo();
		this.current_turn = 0;
		this.state = "waiting";
		this.winner_team = 0;
	}

	/**
	 * Añade jugador al equipo correspondiente.
	 * Comprueba en que indice debe añadirlo (primera posicion distinta de null)
	 * 
	 * @param p personaje a añadir
	 * @param team_n número del equipo (1 o 2)
	 * @return true si se añadió y false si está completo
	 */
	public boolean addPlayer(Personaje p, int team_n)
	{
		Equipo team = (team_n == 1) ? team1 : team2;

		if (team.getPlayer1() == null)
		{
			team.setPlayer(p, 0);
			return true;
		}
		else if (team.getPlayer2() == null)
		{
			team.setPlayer(p, 1);
			return true;
		}

		return false;
	}

	/**
	 * Si ambos equipos están completos se inicia la partida.
	 * 
	 * @return true si se inició correctamente y false si falta algún jugador
	 */
	public boolean startGame()
	{
		if (team1.isComplete() && team2.isComplete())
		{
			this.state = "playing";
			this.current_turn = 0;
			return true;
		}
		return false;
	}

	/**
	 * Verifica si hay un equipo ganador. Comprueba que el otro equipo esté muerto.
	 * 
	 * @return true si hay ganador y false si el juego continua
	 */
	public boolean checkWinner()
	{
		if (team1.allDead())
		{
			this.winner_team = 2;
			this.state = "finished";
			return true;
		}

		if (team2.allDead())
		{
			this.winner_team = 1;
			this.state = "finished";
			return true;
		}

		return false;
	}

	/**
	 * Avanza al siguiente turno.
	 */
	public void nextTurn() 
	{
		if (state.equals("playing")) 
		{
			current_turn = (current_turn + 1) % 4; // Avanza 0,1,2,3,0,1,2,3...
			checkWinner();
		}
	}

	/**
	 *  Obtiene el personaje con el turno actual.
	 * 
	 * @return Personaje con turno actual
	 */
	public Personaje getCurrentPlayer() 
	{
		if (current_turn < 2) 
			return team1.getPlayer(current_turn);
		else 
			return team2.getPlayer(current_turn - 2);
	}

	/**
	 * Obtiene equipo con el turno actual
	 * 
	 * @return 1 si es el equipo1 y 2 si es el equipo2
	 */
	public int getCurrentTeam()
	{
		return (current_turn < 2) ? 1 : 2;
	}

	/**
	 * Obtiene personaje según indice global
	 * @param index va del 0-3: 0 y 1 son team1 y 1-2 son team2
	 * @return personaje correspondiente o null si indice es invalido
	 */
	public Personaje getPlayerByIndex(int index) 
	{
		if (index < 0 || index > 3) 
			return null;
		
		if (index < 2) 
			return team1.getPlayer(index);
		else 
			return team2.getPlayer(index - 2);
	}

	/**
	 * Ejecuta una accion del jugador con turno actual.
	 * Se verifica la instancia para saber el tipo de movimiento.
	 * 
	 * @param action_num Número de accion (cada personaje tiene 3 acciones posibles)
	 * @param target1_index Indice del primer objetivo o -1 si no se usa
	 * @param target2_index Indice del segundo objetivo o -1 si no se usa
	 * @return true si la acción se ejecutó correctamente.
	 */
	public boolean executeAction(int action_num, int target1_index, int target2_index) 
	{
		if (!state.equals("playing")) 
			return false;
		
		Personaje current_player = getCurrentPlayer();
		
		if (current_player == null || !current_player.isAlive()) 
			return false;
		
		Personaje target1 = getPlayerByIndex(target1_index);
		Personaje target2 = getPlayerByIndex(target2_index);
		
		// Identificar el tipo de personaje y ejecutar su acción
		if (current_player instanceof Luchador) 
		{
			Luchador luchador = (Luchador) current_player;
			switch (action_num) 
			{
				case 1:
					luchador.atacar(target1);
					break;
				case 2:
					luchador.defender();
					break;
				case 3:
					luchador.atacar_boost(target1);
					break;
				default:
					return false;
			}
		} 
		else if (current_player instanceof Mago) 
		{
			Mago mago = (Mago) current_player;
			switch (action_num) 
			{
				case 1:
					mago.atacar(target1);
					break;
				case 2:
					mago.atacar_boost(target1);
					break;
				case 3:
					mago.atacar_area(target1, target2);
					break;
				default:
					return false;
			}
		} 
		else if (current_player instanceof Curandero) 
		{
			Curandero curandero = (Curandero) current_player;
			switch (action_num) 
			{
				case 1:
					curandero.atacar(target1);
					break;
				case 2:
					curandero.curar(target1);
					break;
				case 3:
					curandero.megacurar(target1);
					break;
				default:
					return false;
			}
		}
		else 
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Obtiene el índice global de un personaje
	 * @param character personaje a buscar
	 * @return indice global (0-3) o -1 si no se encuentra
	 */
	public int getPlayerIndex(Personaje character) 
	{
		if (team1.getPlayer1() == character) return 0;
		if (team1.getPlayer2() == character) return 1;
		if (team2.getPlayer1() == character) return 2;
		if (team2.getPlayer2() == character) return 3;
		return -1;
	}

	public Equipo getTeam1() 
	{
		return team1;
	}

	public Equipo getTeam2() 
	{
		return team2;
	}

	public int getCurrentTurn() 
	{
		return current_turn;
	}

	public String getState() 
	{
		return state;
	}

	public int getWinnerTeam() 
	{
		return winner_team;
	}

	public Equipo getEnemyTeam() 
	{
		return (getCurrentTeam() == 1) ? team2 : team1;
	}

	public Equipo getAllyTeam() 
	{
		return (getCurrentTeam() == 1) ? team1 : team2;
	}
}
