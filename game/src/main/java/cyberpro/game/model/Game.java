package cyberpro.game.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import cyberpro.game.view.TileType;
import java.io.FileNotFoundException;

public class Game {
	private static final String DEFAULT_PLAYER1_NAME = "Player 1";
	private static final String DEFAULT_PLAYER2_NAME = "Player 2";

	private String name;
	private Board board;
	private ArrayList<Player> players;
	private ArrayList<Bomb> bombs;
	private ArrayList<Bomb> bombsExploded;
	private ArrayList<Modifier> modifiers;
	private Map<String, ScheduledFuture> explosionTasksByBombId = new HashMap<>();

	// public Game(String name, int playersNumber) { this.name = name;
	// this.playersNumber = playersNumber; board = new Board(); players = new
	// ArrayList<>(); bombs = new ArrayList<>(); modifiers = new ArrayList<>();
	// }

	public Game(String name, ArrayList<Player> players, Board board) {
		this.name = name;
		this.players = players;
		this.board = board;
		bombs = new ArrayList<>();
		bombsExploded = new ArrayList<>();
		modifiers = new ArrayList<>();
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Bomb> getBombs() {
		return bombs;
	}

	public ArrayList<Bomb> getBombsExploded() {
		return bombsExploded;
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

	// create a pair (bombId, scheduler object) to be able to cancel the scheduler later
	public void putExplosionTaskByBombId(String bombId, ScheduledFuture scheduledTask) {
		if (explosionTasksByBombId.containsKey(bombId)) {
			return;
		}
		explosionTasksByBombId.put(bombId, scheduledTask);
		System.out.println("Scheduled an explosion task for the bombId = " + bombId);
	}
	
	public ScheduledFuture extractExplosionTaskByBombId(String bombId) {
		ScheduledFuture scheduledTask = explosionTasksByBombId.get(bombId);
		System.out.println("A scheduled task for the bomb with bombId = " + bombId + " was extracted successfully");
		explosionTasksByBombId.remove(bombId);
		return scheduledTask;
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
		System.out.println("Bomb " + bomb.getId() + " was added to the bombs list");
		return true;
	}

	public boolean removeBomb(Bomb bomb) {
		// if there are no bombs
		if (bombs == null)
			return false;
		// if the bomb is not found in the bombs list - return false
		Bomb bombFound = findBombById(bomb.getId());
		if (bombFound == null) {
			System.out.println("No such bomb in the bombs list!");
			return false;
		}
		System.out.println("Bomb " + bomb.getId() + " was removed from the bombs list");
		this.bombs.remove(bomb);
		return true;
	}

	public boolean addModifier(Modifier modifier) {
		// search for a double and if it is found - return false
		Modifier modifierFound = findModifierById(modifier.getId());
		if (modifierFound != null) {
			return false;
		}
		this.modifiers.add(modifier);
		System.out.println("Modifier " + modifier.getId() + " was added to the modifiers list");
		return true;
	}

	public boolean removeModifier(Modifier modifier) {
		// if there are no players
		if (modifiers == null)
			return false;
		// if not found - nothing to remove
		Modifier modifierFound = findModifierById(modifier.getId());
		if (modifierFound == null) {
			System.out.println("No such modifier in the modifiers list!");
			return false;
		}
		System.out.println("Modifier " + modifier.getId() + " was removed from the modifiers list");
		this.modifiers.remove(modifier);
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Game " + name + " with " + players.size() + " players on the " + board.getName() + " board" + "\nBOARD"
				+ "\n" + board.toString() + "\nPLAYERS" + "\n" + players.toString() + "\nBOMBS" + "\n"
				+ bombs.toString() + "\nMODIFIERS" + "\n" + modifiers.toString();
	}

	public boolean assignExploded(Bomb bomb) {
		if (!removeBomb(bomb))
			return false;
		if (findExplodedBombById(bomb.getId()) != null) {
			return false;
		}
		System.out.println("Bomb " + bomb.getId() + " was transfered to the exploded bombs list");
		this.bombsExploded.add(bomb);
		return true;
	}
	
	public Bomb findExplodedBombById(String Id) {
		if (bombsExploded == null)
			return null;
		for (Bomb bomb : bombsExploded) {
			if (bomb.getId().equalsIgnoreCase(Id)) {
				return bomb;
			}
		}
		return null;
	}

	public boolean fullRemove(Bomb bomb) {
		// if there are no bombs
		if (bombsExploded == null)
			return false;
		// if the bomb is not found in the bombs list - return false
		Bomb bombFound = findExplodedBombById(bomb.getId());
		if (bombFound == null) {
			System.out.println("No such bomb in the exploded bombs list!");
			return false;
		}
		System.out.println("Bomb " + bomb.getId() + " was removed from the exploded bombs list");
		this.bombsExploded.remove(bomb);
		return true;
		
	}
	
	public void printPlayers() {
		for (Player player : players) {
			System.out.println(player);
		}
	}

}
