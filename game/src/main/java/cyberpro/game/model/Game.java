package cyberpro.game.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import cyberpro.game.view.TileType;
import java.io.FileNotFoundException;

public class Game {
	private static final int DEFAULT_PLAYERS_NUMBER = 2;
	private static final String DEFAULT_PLAYER1_NAME = "Player 1";
	private static final String DEFAULT_PLAYER2_NAME = "Player 2";

	private String name;
	private int playersNumber;
	private Board board;
	private ArrayList<Player> players;
	private ArrayList<Bomb> bombs;
	private ArrayList<Modifier> modifiers;

	
	  //public Game(String name, int playersNumber) { this.name = name;
	  //this.playersNumber = playersNumber; board = new Board(); players = new
	  //ArrayList<>(); bombs = new ArrayList<>(); modifiers = new ArrayList<>();
	  //}

	public Game(String name, Board board) {
		this.name = name;
		this.playersNumber = DEFAULT_PLAYERS_NUMBER;
		this.board = board;
		bombs = new ArrayList<>();
		modifiers = new ArrayList<>();
		players = new ArrayList<>();

	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Bomb> getBombs() {
		return bombs;
	}

	public ArrayList<Modifier> getModifiers() {
		return modifiers;
	}

	public Player findPlayerById(String Id) {
		if (players == null)
			return null;
		for (Player player : players) {
			if (player.getId().equalsIgnoreCase(Id)) {
				return player;
			}
		}
		return null;
	}

	public Board getBoard() {
		return board;
	}

	public Bomb findBombById(String Id) {
		if (bombs == null)
			return null;
		for (Bomb bomb : bombs) {
			if (bomb.getId().equalsIgnoreCase(Id)) {
				return bomb;
			}
		}
		return null;
	}

	public Modifier findModifierById(String Id) {
		if (modifiers == null)
			return null;
		for (Modifier modifier : modifiers) {
			if (modifier.getId().equalsIgnoreCase(Id)) {
				return modifier;
			}
		}
		return null;
	}

	public boolean addPlayer(Player player) {
		// search for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.add(player);
		return true;
	}

	public boolean removePlayer(Player player) {
		// if there are no players
		if (players == null)
			return false;
		// search for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.remove(player);
		return true;
	}

	public boolean addBomb(Bomb bomb) {
		// search for a double and if it is found - return false
		Bomb bombFound = findBombById(bomb.getId());
		if (bombFound != null) {
			return false;
		}
		this.bombs.add(bomb);
		return true;
	}

	public boolean removeBomb(Bomb bomb) {
		// if there are no players
		if (bombs == null)
			return false;
		// search for a double and if it is found - return false
		Bomb bombFound = findBombById(bomb.getId());
		if (bombFound != null) {
			return false;
		}
		this.bombs.remove(bomb);
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Game " + name + " with " + playersNumber + " players on the " + board.getName() + " board" + "\nBOARD"
				+ "\n" + board.toString() + "\nPLAYERS" + "\n" + players.toString() + "\nBOMBS" + "\n"
				+ bombs.toString() + "\nMODIFIERS" + "\n" + modifiers.toString();
	}

	/*
	 * plantBomb findBombById findModifierById
	 */

}
