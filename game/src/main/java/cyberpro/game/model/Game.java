package cyberpro.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import cyberpro.game.view.TileType;

public class Game {
	private static final String DEFAULT_PLAYER1_NAME = "Player 1";
	private static final String DEFAULT_PLAYER2_NAME = "Player 2";

	private String name;
	private Board board;
	private ArrayList<Player> players;
	private CopyOnWriteArrayList<Bomb> bombs;
	private ArrayList<Bomb> bombsExploded;
	private ArrayList<Modifier> modifiers;
	private Map<String, ScheduledFuture> explosionTasksByBombId = new HashMap<>();
	private final Logger logger = Logger.getLogger(Game.class.getName()); // Create logger using core Java API

	public Game(String name, ArrayList<Player> players, Board board) {
		this.name = name;
		this.players = players;
		this.board = board;
		bombs = new CopyOnWriteArrayList<>();
		bombsExploded = new ArrayList<>();
		modifiers = new ArrayList<>();
	}

	public static String getDefaultPlayer1Name() {
		return DEFAULT_PLAYER1_NAME;
	}

	public static String getDefaultPlayer2Name() {
		return DEFAULT_PLAYER2_NAME;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public CopyOnWriteArrayList<Bomb> getBombs() {
		return bombs;
	}

	public ArrayList<Bomb> getBombsExploded() {
		return bombsExploded;
	}

	public ArrayList<Modifier> getModifiers() {
		return modifiers;
	}

	// finds a player by Id
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

	// creates a pair (bombId, scheduler object) to be able to cancel the scheduler
	// llater
	public void putExplosionTaskByBombId(String bombId, ScheduledFuture scheduledTask) {
		if (explosionTasksByBombId.containsKey(bombId)) {
			return;
		}
		explosionTasksByBombId.put(bombId, scheduledTask);
		logger.log(Level.INFO, "Scheduled an explosion task for the bombId = " + bombId);
	}

	// gets the scheduled explosion task from the pairs list and removes it from
	// there
	public ScheduledFuture extractExplosionTaskByBombId(String bombId) {
		ScheduledFuture scheduledTask = explosionTasksByBombId.get(bombId);
		logger.log(Level.INFO, "A scheduled task for the bomb with bombId = " + bombId + " was extracted successfully");
		explosionTasksByBombId.remove(bombId);
		return scheduledTask;
	}

	// finds bomb by id
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

	// finds modifier by id
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

	// adds the player to the players list
	public boolean addPlayer(Player player) {
		// search for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.add(player);
		return true;
	}

	// removes the player from the players list
	public boolean removePlayer(Player player) {
		// if there are no players
		if (players == null)
			return false;
		// searching for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.remove(player);
		return true;
	}

	// adding the bomb to the bombs list
	public boolean addBomb(Bomb bomb) {
		// search for a double and if it is found - return false
		Bomb bombFound = findBombById(bomb.getId());
		if (bombFound != null) {
			return false;
		}
		this.bombs.add(bomb);
		return true;
	}

	// removes the bomb from the bombs list
	public boolean removeBomb(Bomb bomb) {
		// if there are no bombs
		if (bombs == null)
			return false;
		// if the bomb is not found in the bombs list - return false
		Bomb bombFound = findBombById(bomb.getId());
		if (bombFound == null) {
			logger.log(Level.INFO, "No such bomb in the bombs list!");
			return false;
		}
		this.bombs.remove(bomb);
		return true;
	}

	// adds the modifier to the modifiers list
	public boolean addModifier(Modifier modifier) {
		// search for a double and if it is found - return false
		Modifier modifierFound = findModifierById(modifier.getId());
		if (modifierFound != null) {
			return false;
		}
		this.modifiers.add(modifier);
		return true;
	}

	// removes the modifier from the modifiers list
	public boolean removeModifier(Modifier modifier) {
		// if there are no players
		if (modifiers == null)
			return false;
		// if not found - nothing to remove
		Modifier modifierFound = findModifierById(modifier.getId());
		if (modifierFound == null) {
			logger.log(Level.INFO, "No such modifier in the modifiers list!");
			return false;
		}
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

	// transfers the bomb from the main bombs list to the exploded bombs list
	public boolean assignExploded(Bomb bomb) {
		if (!removeBomb(bomb))
			return false;
		if (findExplodedBombById(bomb.getId()) != null) {
			return false;
		}
		logger.log(Level.INFO, "Bomb " + bomb.getId() + " was transfered to the exploded bombs list");
		this.bombsExploded.add(bomb);
		return true;
	}

	// finds the exploded bomb by id
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

	// removes the bomb from the exploded bombs list
	public boolean fullRemove(Bomb bomb) {
		// if there are no bombs
		if (bombsExploded == null)
			return false;
		// if the bomb is not found in the bombs list - return false
		Bomb bombFound = findExplodedBombById(bomb.getId());
		if (bombFound == null) {
			logger.log(Level.INFO, "No such bomb to remove in the exploded bombs list!");
			return false;
		}
		logger.log(Level.INFO, "Bomb " + bomb.getId() + " was removed from the exploded bombs list");
		this.bombsExploded.remove(bomb);
		return true;

	}

	// prints players list
	public void printPlayers() {
		for (Player player : players) {
			System.out.println(player);
		}
	}

	// resets players variables except for their stats
	public void playersReset() {
		if (players == null) {
			return;
		}
		for (Player player : players) {
			if (!player.isAlive()) {
				player.setAlive(true);
			}
			player.resetModifiers();
		}
		placePlayersToFreePlace();
	}

	// validates if there is a free cell in the board near the coordinates provided
	public boolean isFreeSpaceNear(Coordinates coordinates) {
		if (coordinates == null) {
			return false;
		}
		// traverse along X
		for (int i = -1; i <= 1 && i != 0; i++) {
			int newX = coordinates.getX() + i;
			if (newX <= 0 || newX > board.getSize() - 1) {
				continue;
			}
			if (board.getCell(newX, coordinates.getY()) == TileType.FLOOR) {
				return true;
			}

			// traverse along Y
			for (int j = -1; j <= 1 && j != 0; j++) {
				int newY = coordinates.getY() + j;
				if (newY <= 0 || newY > board.getSize() - 1) {
					continue;
				}
				if (board.getCell(coordinates.getX(), newY) == TileType.FLOOR) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	// places players in the board in the most cornered free space
	private void placePlayersToFreePlace() {
		boolean isPlayer1Placed = false;
		boolean isPlayer2Placed = false;
		if (players == null || players.isEmpty()) {
			return;
		}
		// player 1 processing
		Player player1 = players.get(0);
		for (int i = 0; i <= board.getSize() / 2; i++) {
			if (isPlayer1Placed) {
				break;
			}
			for (int j = 0; j <= board.getSize() / 2; j++) {
				if (board.getCell(i, j) == TileType.FLOOR && isFreeSpaceNear(new Coordinates(i, j))) {
					isPlayer1Placed = true;
					player1.setCoordinates(new Coordinates(i, j));
					break;
				}
			}
		}

		// player 2 processing
		Player player2 = players.get(1);
		for (int i = 0; i <= board.getSize() / 2; i++) {
			if (isPlayer2Placed) {
				break;
			}
			for (int j = 0; j <= board.getSize() / 2; j++) {
				if (board.getCell(board.getSize() - i - 1, board.getSize() - j - 1) == TileType.FLOOR
						&& isFreeSpaceNear(new Coordinates(board.getSize() - i - 1, board.getSize() - j - 1))) {
					isPlayer2Placed = true;
					player2.setCoordinates(new Coordinates(board.getSize() - i - 1, board.getSize() - j - 1));
					break;
				}
			}
		}
	}
}