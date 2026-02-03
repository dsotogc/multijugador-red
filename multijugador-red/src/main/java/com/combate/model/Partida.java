package com.combate.model;

/**
 * Clase Partida con sistema de turnos alternados entre equipos.
 * Orden: Jugador 0 (E1) → Jugador 2 (E2) → Jugador 1 (E1) → Jugador 3 (E2)
 * 
 * @author David Soto García
 */
public class Partida 
{
    private Equipo team1;
    private Equipo team2;
    private int current_turn;
    private String state;
    private int winner_team;
    
    private static final int[] ORDEN_TURNOS = {0, 2, 1, 3};
    private int indice_turno;
    
    public Partida() 
    {
        this.team1 = new Equipo();
        this.team2 = new Equipo();
        this.indice_turno = 0;
        this.current_turn = ORDEN_TURNOS[0];
        this.state = "waiting";
        this.winner_team = 0;
    }
    
    public boolean addPlayer(Personaje character, int team_num) 
    {
        Equipo team = (team_num == 1) ? team1 : team2;
        
        if (team.getPlayer1() == null) 
        {
            team.setPlayer(character, 0);
            return true;
        } 
        else if (team.getPlayer2() == null) 
        {
            team.setPlayer(character, 1);
            return true;
        }
        
        return false;
    }
    
    public boolean startGame() 
    {
        if (team1.isComplete() && team2.isComplete()) 
        {
            this.state = "playing";
            this.indice_turno = 0;
            this.current_turn = ORDEN_TURNOS[0];
            return true;
        }
        return false;
    }
    
    public void nextTurn() 
    {
        if (state.equals("playing")) 
        {
            indice_turno = (indice_turno + 1) % 4;
            current_turn = ORDEN_TURNOS[indice_turno];
            checkWinner();
        }
    }
    
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
    
    public Personaje getCurrentPlayer() 
    {
        if (current_turn < 2) 
        {
            return team1.getPlayer(current_turn);
        } 
        else 
        {
            return team2.getPlayer(current_turn - 2);
        }
    }
    
    public int getCurrentTeam() 
    {
        return (current_turn < 2) ? 1 : 2;
    }
    
    public boolean executeAction(int action_num, int target1_index, int target2_index) 
    {
        if (!state.equals("playing")) 
        {
            return false;
        }
        
        Personaje current_player = getCurrentPlayer();
        
        if (current_player == null || !current_player.isAlive()) 
        {
            return false;
        }
        
        Personaje target1 = getPlayerByIndex(target1_index);
        Personaje target2 = getPlayerByIndex(target2_index);
        
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
    
    public Personaje getPlayerByIndex(int index) 
    {
        if (index < 0 || index > 3) 
        {
            return null;
        }
        
        if (index < 2) 
        {
            return team1.getPlayer(index);
        } 
        else 
        {
            return team2.getPlayer(index - 2);
        }
    }
    
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