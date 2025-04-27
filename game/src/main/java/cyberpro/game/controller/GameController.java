package cyberpro.game.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// This is starter for JavaFX window
import javafx.application.Platform;
import javafx.stage.Stage;
import cyberpro.game.model.*;
import cyberpro.game.service.DataHandler;
import cyberpro.game.service.FileResourcesImporter;
import cyberpro.game.view.GameView;
import cyberpro.game.view.TileType;
import cyberpro.game.view.MainMenu;

import java.io.FileNotFoundException;

public class GameController implements ControllerInterface {
	private static final String DEFAULT_LEVEL = "/cyberpro/game/model/level01.txt";
	private String level = DEFAULT_LEVEL;
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static final int DEFAULT_TIME_TILL_EXPLOSION = 4;
	private static final TileType[] TILES_CANT_WALK_THROUGH = { TileType.CONCRETE_WALL, TileType.BRICK_WALL };
	private static final TileType[] TILES_EXPLOSIBLE = { TileType.FLOOR, TileType.BRICK_WALL };
	private static final TileType[] TILES_DESTRUCTIBLE = { TileType.BRICK_WALL };
	private static final int DEFAULT_MAX_BOMBS_ACTIVE = 1;
	private static final int GAME_OVER_TIME = 5;
	private ScheduledExecutorService schedulerForExplosion = Executors.newScheduledThreadPool(10);
	private ScheduledExecutorService schedulerForRaysOff = Executors.newScheduledThreadPool(10);
	private ScheduledExecutorService schedulerForGameOver = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService schedulerForModifiersOff = Executors.newScheduledThreadPool(10);
	private ArrayList<Player> playersSet = new ArrayList<>();
	private ScheduledFuture<?> gameOverTask;

	private GameView gameView;
	private Game game;
	private MainMenu mainMenuGUI;
	private final Logger logger = Logger.getLogger(GameView.class.getName());; // Create logger using core Java API

	@Override
	public Board getBoard() {
		return game.getBoard();
	}

	// the entrance point into the Controller
	public void enterController() {
		try {
			initLastPlayersSet();
			mainMenu();
			logger.setLevel(Level.FINE);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Got an unexpected exception...");
		}
	}

	// imports last Players Set and restores last max Player counter value
	private void initLastPlayersSet() {
		ArrayList<Player> playersSetImported = DataHandler.getLastPlayersSet();
		if (playersSetImported == null || playersSetImported.isEmpty()) {
			return;
		}
		initPlayersCounter();
	}

	// initializes the Player counter value file
	public void initPlayersCounter() {
		// calculating max counter number and updating the Player counter to the max
		// value
		ArrayList<Player> playersSetImported = DataHandler.getLastPlayersSet();
		// logger.log(Level.INFO, ">>> playersSetImported is " + playersSetImported);
		int maxId = Player.getCounter();
		for (Player player : playersSetImported) {
			int playerCount = Integer.parseInt(player.getId().substring(1));
			// logger.log(Level.INFO, "Counter value for player " + player.getName() + " =
			// ");
			if (maxId < playerCount) {
				maxId = playerCount;
			}
		}
		if (maxId != Player.getCounter()) {
			logger.log(Level.INFO, "Setting up counter from initPlayersCounter: to the value = " + maxId);
			Player.setCounter(maxId);
			DataHandler.saveCounterIntoFile();
			// logger.log(Level.INFO, "currentCounter =" + Player.getCounter());
		}
	}

	// Main menu with Players customization and level selection
	public void mainMenu() {
		// >>> Only until the target mechanism is ready in the GUI <<<
		// playersSet initialization
		if (playersSet == null || playersSet.isEmpty()) {
			setDefaultPlayers();
		}
		// starting the MainMenu window
		Platform.startup(() -> {
			Stage stage = new Stage();
			mainMenuGUI = new MainMenu(stage, this);
		});
	}

	@Override
	// starts an actual game for one level
	public void startGame() {
		// getting a board
		FileResourcesImporter fileResourcesImporter = new FileResourcesImporter();
		Board board;
		try {
			board = fileResourcesImporter.importLevelIntoBoard("level", (level.isBlank() ? DEFAULT_LEVEL : level));
			// creating a game
			game = new Game("Level", playersSet, board);
			// reseting game variables
			gameReset();
			// runs the game
			gameProcess();
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Got a FileNotFoundException exception...");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Got an unexpected exception...");
		}
	}

	// resets game variables
	private void gameReset() {
		game.playersReset();
		resetLists();
		gameOverTask = null;
		// logger.log(Level.INFO, "Game variables were reset ");
	}

	// runs a game GUI window and starts processes to provide actual game
	// implementation
	private void gameProcess() {
		// GameView initialization
		Platform.runLater(() -> {
			Stage stage = new Stage();
			gameView = new GameView(stage, this);
		});
		// draws an initial grid
		draw();
		// running a thread for dealing with a commands queue
		new Thread(() -> {
			try {
				while (true) {
					String command = commandQueue.take();
					processCommand(command, game);
				}
			} catch (InterruptedException e) {
				logger.log(Level.INFO, "A thread InterruptedException encountered");
				Thread.currentThread().interrupt();
			}
		}).start();
	}

	// processes a command from a Player
	private boolean processCommand(String command, Game game) {
		Player playerFound = game.findPlayerById(command.substring(1));
		if (playerFound == null)
			return false;
		if (!playerFound.isAlive())
			return false;
		switch (command.charAt(0)) {
		case 'U' -> moveInDirection(command.substring(1), "up");
		case 'D' -> moveInDirection(command.substring(1), "down");
		case 'L' -> moveInDirection(command.substring(1), "left");
		case 'R' -> moveInDirection(command.substring(1), "right");
		case 'B' -> {
			plantBomb(command.substring(1));
			// logger.log(Level.INFO, "Got a plant bomb command for a queue");
		}
		case 'E' -> {
			RemoteBombExplode(command.substring(1));
			// logger.log(Level.INFO, "Got a remote bombs detonation command for a queue");
		}

		default -> logger.log(Level.INFO, "Unknown command");
		}
		return true;
	}

	// movement in the direction specified: up, down, right or left
	private boolean moveInDirection(String playerId, String direction) {
		// finds a player by Id
		Player playerFound = game.findPlayerById(playerId);
		// if not
		if (playerFound == null)
			return false;
		// current coords
		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();
		// new coords parts declaration
		int newX = 0;
		int newY = 0;
		// REVERSE modifier implementation
		if (playerFound.findModifierByType(ModifierType.REVERSE_CONTROLS) != null) {
			switch (direction) {
			case "right" -> direction = "left";
			case "left" -> direction = "right";
			case "up" -> direction = "down";
			case "down" -> direction = "up";
			default -> logger.log(Level.INFO, "Unknown command. Can't be reversed");
			}
		}
		// calculating a new coordinate
		switch (direction) {
		case "right" -> {
			newX = playerFound.getCoordinates().getX() + 1;
			newY = currentY;
		}
		case "left" -> {
			newX = playerFound.getCoordinates().getX() - 1;
			newY = currentY;
		}
		case "up" -> {
			newY = playerFound.getCoordinates().getY() - 1;
			newX = currentX;
		}
		case "down" -> {
			newY = playerFound.getCoordinates().getY() + 1;
			newX = currentX;
		}
		default -> {
			logger.log(Level.INFO, "No such direction!");
			break;
		}
		}
		Coordinates newCoordinates = new Coordinates(newX, newY);
		// check if the new X coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates) || bombByCoordinates(newCoordinates) != null) {
			// logger.log(Level.INFO, "!isFreeToOccupy(newCoordinates)" + "=" +
			// !isFreeToOccupy(newCoordinates));
			// logger.log(Level.INFO, "Move from coordinates (" + currentX + ", " + currentY
			// + ") to coordinates ("
			// + newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			// logger.log(Level.INFO, "AnyPlayerHere(newCoordinates)" + "=" +
			// isAnyPlayerHere(newCoordinates));
			// logger.log(Level.INFO, "newCoordinates = " + newCoordinates);
			return false;
		}
		Coordinates oldCoordinates = new Coordinates(currentX, currentY);
		// getting a modifier
		Modifier modifierFound = modifierByCoordinates(newCoordinates);
		if (modifierFound != null) {
			takeModifier(playerFound, modifierFound);
		}
		// moves sprites
		Platform.runLater(() -> {
			// updating the player's sprite in the view
			gameView.moveSprite(oldCoordinates, newCoordinates, playerFound);
			// if player moves to the active rays, he dies
		});
		// if players touches an explosion rays, he dies
		if (raysBombByCoordinates(newCoordinates)) {
			killPlayer(playerFound);
		}
		// actually moving the player
		switch (direction) {
		case "right" -> {
			playerFound.getCoordinates().setX(newX);
		}
		case "left" -> {
			playerFound.getCoordinates().setX(newX);
		}
		case "up" -> {
			playerFound.getCoordinates().setY(newY);
		}
		case "down" -> {
			playerFound.getCoordinates().setY(newY);
		}
		default -> {
			logger.log(Level.INFO, "No such direction!");
			break;
		}
		}
		return true;
	}

	// a receiving-input-commands-from-VIEW BLOCK

	@Override
	public void playerMoveUp(String playerId) {
		commandQueue.add("U" + playerId);
	}

	@Override
	public void playerMoveDown(String playerId) {
		commandQueue.add("D" + playerId);
	}

	@Override
	public void playerMoveLeft(String playerId) {
		commandQueue.add("L" + playerId);
	}

	@Override
	public void playerMoveRight(String playerId) {
		commandQueue.add("R" + playerId);
	}

	@Override
	public void playerPlantBomb(String playerId) {
		commandQueue.add("B" + playerId);
	}

	@Override
	public void playerRemoteBombExplode(String playerId) {
		commandQueue.add("E" + playerId);
	}

	// finds a Player by Id
	@Override
	public String getPlayerIdByNumber(int playerNumber) {
		if (game.getPlayers().isEmpty()) {
			logger.log(Level.INFO, "Players list is empty!");
			return null;
		}
		int playersQuantity = game.getPlayers().size();
		if (playerNumber > playersQuantity) {
			return null;
		}
		return game.getPlayers().get(playerNumber - 1).getId();
	}

	// returns current number of bombs planted but not exploded for a player
	public int countBombsActive(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return -1;
		int count = 0;
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getPlayerId().equalsIgnoreCase(playerId)) {
				count++;
			}
		}
		return count;
	}

	// returns number of bombs allowed to have at the same time for a player
	public int countBombsAllowed(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return -1;
		int countPlusBombs = playerFound.countModifiersByType(ModifierType.PLUS_BOMB);
		if (countPlusBombs == -1) {
			return -1;
		}
		return DEFAULT_MAX_BOMBS_ACTIVE + countPlusBombs;
	}

	public void plantBomb(String playerId) {
		// validation if players list is empty
		if (game.getPlayers().isEmpty()) {
			logger.log(Level.INFO, "Players list is empty!");
			return;
		}
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return;
		// only allowed amount of bombs can be planted by the player at the same time
		if (countBombsActive(playerId) >= countBombsAllowed(playerId)) {
			// logger.log(Level.INFO, "Player " + playerId + " can't plant more bombs:
			// current active bombs number = "
			// + countBombsActive(playerId) + " and allowed = " +
			// countBombsAllowed(playerId));
			return;
		}
		// unable to plant the second bomb at the same place
		Bomb bombFound = bombByCoordinates(playerFound.getCoordinates());
		if (bombFound != null)
			return;
		// calculation of a blast time
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, DEFAULT_TIME_TILL_EXPLOSION);
		Date explosionTime = calendar.getTime();

		boolean remoteExplosion = false;
		// checking if the player has active REMOTE_EXPLOSION modifier
		if (playerFound.findModifierByType(ModifierType.REMOTE_EXPLOSION) != null) {
			remoteExplosion = true;
		}
		// creating a bomb and adding to the bombs list
		Bomb newBomb = new Bomb(playerId,
				new Coordinates(playerFound.getCoordinates().getX(), playerFound.getCoordinates().getY()),
				remoteExplosion, explosionTime);
		game.addBomb(newBomb);
		// setting up a timer for bomb to explode
		ScheduledFuture<?> taskExplode = schedulerForExplosion.schedule(() -> explodeBomb(newBomb.getId()),
				DEFAULT_TIME_TILL_EXPLOSION, TimeUnit.SECONDS);
		game.putExplosionTaskByBombId(newBomb.getId(), taskExplode);
		// logger.log(Level.INFO, "Player " + playerId + " planted a bomb");
		// ask view to draw the bomb
		Platform.runLater(() -> {
			gameView.plantBomb(newBomb);
		});
	}

	// explodes a player's bombs remotely by a command
	private void RemoteBombExplode(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return;
		// searching for all the player's active bombs able to detonate remotely and
		// detonate them immediately
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getPlayerId().equalsIgnoreCase(playerId) && bomb.isDistantExplosion() && game.findExplodedBombById(bomb.getId()) == null) {
				// canceling the scheduled tasks for future explosion
				ScheduledFuture<?> task = game.extractExplosionTaskByBombId(bomb.getId());
				if (task == null)
					continue;
				task.cancel(true);
				// requesting to immediately explode
				explodeBomb(bomb.getId());
			}
		}
	}

	// explodes the bomb
	public synchronized void explodeBomb(String bombId) {
		Bomb bombFound = game.findBombById(bombId);
		Bomb bombExploded = game.findExplodedBombById(bombId);
		// validation if a bomb isn't found  by Id
		if (bombFound == null || bombExploded != null || bombFound.getRaysOffDate() != null)
			return;
		bombFound.explode();
		makeRaysInOneDirection(bombFound, "right");
		makeRaysInOneDirection(bombFound, "left");
		makeRaysInOneDirection(bombFound, "up");
		makeRaysInOneDirection(bombFound, "down");
		makeRaysInOneDirection(bombFound, "center");
		Platform.runLater(() -> {
			// draw explosion in the view
			gameView.blastBomb(bombFound, bombFound.getRays());
			// transfer the bomb from the bombs list to the exploded bombs list
		});
		game.assignExploded(bombFound);
		// calculating time for rays to disappear and Setting up a timer to do it
		// eliminating the bomb from the exploded bombs list
		schedulerForRaysOff.schedule(() -> {
			// !!! I put this code to rest bomb removal without redraw board
			draw();
			Platform.runLater(() -> {
				gameView.removeBlast(bombFound);
			});
			//
			game.fullRemove(bombFound);
		}, Bomb.getDefaultRaysDuration(), TimeUnit.SECONDS);

	}

	// creates explosion rays in one of the directions and saves it in a collection
	// for transfering to GUI
	public void makeRaysInOneDirection(Bomb bombFound, String direction) {

		Coordinates iCoordinate;
		int raysRange = calculateRaysRange(bombFound.getPlayerId());
		for (int i = 1; i <= raysRange; i++) {
			// collecting coordinates for potential rays
			// identifying a coordinates depending on direction
			switch (direction) {
			case "right" ->
				iCoordinate = new Coordinates(bombFound.getCoordinates().getX() + i, bombFound.getCoordinates().getY());
			case "left" ->
				iCoordinate = new Coordinates(bombFound.getCoordinates().getX() - i, bombFound.getCoordinates().getY());
			case "up" ->
				iCoordinate = new Coordinates(bombFound.getCoordinates().getX(), bombFound.getCoordinates().getY() - i);
			case "down" ->
				iCoordinate = new Coordinates(bombFound.getCoordinates().getX(), bombFound.getCoordinates().getY() + i);
			case "center" ->
				iCoordinate = new Coordinates(bombFound.getCoordinates().getX(), bombFound.getCoordinates().getY());
			default -> {
				logger.log(Level.INFO, "No such direction!");
				return;
			}
			}

			// calculating variables for making decisions
			boolean insideBoard = areCoordinatesInsideBoard(iCoordinate);
			boolean freeToOccupy = isFreeToOccupy(iCoordinate);
			boolean explosible = isExplosible(iCoordinate);
			boolean playerHere = isAnyPlayerHere(iCoordinate);
			boolean destructible = isDestructible(iCoordinate);
			// if the coordinate is out of the board, quit the cycle
			if (!insideBoard) {
				break;
			}
			// killing players
			if (playerHere) {
				Player playerFound = playerByCoordinates(iCoordinate);
				// logger.log(Level.INFO, "Player is found by the rays ! It's " +
				// playerFound.getName());
				if (playerFound != null) {
					killPlayer(playerFound);
				}
			}
			// quit cycle in case of central direction
			if (direction.equalsIgnoreCase("center"))
				return;
			// find out if this coordinate is busy with a solid tile
			if (!freeToOccupy) {
				// if this solid tile is destructible, demolish it
				if (destructible) {
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					// adding rays at the cell being destroyed
					bombFound.addToRays(iCoordinate);
					// uncovering a modifier under the destroyed brick wall
					Modifier newModifier = new Modifier(iCoordinate, uncoverModifier(iCoordinate));
					game.addModifier(newModifier);
					Platform.runLater(() -> gameView.plantMod(newModifier));
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);
			// exploding a bomb which is on the rays way
			Bomb bombToExplode = bombByCoordinates(iCoordinate);
			if (bombToExplode == null) {
				continue;
			}
			Bomb bombExploded = game.findExplodedBombById(bombToExplode.getId());
			Bomb bombToCheck = game.findBombById(bombToExplode.getId());

			if (bombToCheck== null || bombExploded != null) {
				continue;
			}

			if (bombToExplode.getRaysOffDate() == null || game.findExplosionTaskByBombId(bombToExplode.getId()) == null) {
				// exploding another bomb
				logger.log(Level.INFO, "Bomb " + bombFound.getId() + " detonated bomb " + bombToExplode.getId());
					explodeBomb(bombToExplode.getId());
			}
		}

	}

	// calculates explosion rate for the player's bombs
	private int calculateRaysRange(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return -1;
		int countRange = Bomb.getDefaultRaysRange();
		int countRangeUp = playerFound.countModifiersByType(ModifierType.PLUS_RANGE);
		countRange += countRangeUp;
		return countRange;
	}

	// draws the GUI grid of the game level
	public void draw() {
		Platform.runLater(() -> gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers(),
				game.getBombsExploded()));
	}

	// validates if coordinates are inside the board
	public boolean areCoordinatesInsideBoard(Coordinates coordinates) {
		if (coordinates.getX() > game.getBoard().getSize() - 1 || coordinates.getX() < 0
				|| coordinates.getY() > game.getBoard().getSize() - 1 || coordinates.getY() < 0) {
			return false;
		}
		return true;
	}

	// collision detection method: if a player can occupy this cell
	public boolean isFreeToOccupy(Coordinates coordinates) {
		if (!areCoordinatesInsideBoard(coordinates)) {
			return false;
		}
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (Arrays.asList(TILES_CANT_WALK_THROUGH).contains(tileToValidate)) {
			return false;
		}
		return true;
	}

	// identifies if this cell is free for rays
	public boolean isExplosible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_EXPLOSIBLE).contains(tileToValidate)) {
			return false;
		}
		return true;
	}

	// identifies if the tile is destructible
	public boolean isDestructible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_DESTRUCTIBLE).contains(tileToValidate)) {
			return false;
		}
		return true;
	}

	// validates if any player is here
	public boolean isAnyPlayerHere(Coordinates coordinates) {
		for (Player player : game.getPlayers()) {
			if (player.getCoordinates().getX() == coordinates.getX()
					&& player.getCoordinates().getY() == coordinates.getY()) {
				return true;
			}
		}
		return false;
	}

	// validates if any bomb is here
	public boolean isAnyBombHere(Coordinates coordinates) {
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getCoordinates().getX() == coordinates.getX()
					&& bomb.getCoordinates().getY() == coordinates.getY()) {
				return true;
			}
		}
		return false;
	}

	// identifies the player by coordinates
	public Player playerByCoordinates(Coordinates coordinates) {
		for (Player player : game.getPlayers()) {
			if (player.getCoordinates().getX() == coordinates.getX()
					&& player.getCoordinates().getY() == coordinates.getY()) {
				return player;
			}
		}
		return null;
	}

	// identifies the bomb by coordinates
	public Bomb bombByCoordinates(Coordinates coordinates) {
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getCoordinates().getX() == coordinates.getX()
					&& bomb.getCoordinates().getY() == coordinates.getY()) {
				return bomb;
			}
		}
		return null;
	}

	// kills the player
	public boolean killPlayer(Player player) {
		// can't kill twice
		if (!player.isAlive()) {
			return false;
		}
		// searching for a player by Id
		Player playerFound = game.findPlayerById(player.getId());
		if (playerFound == null)
			return false;
		playerFound.kill();
		// killing the player in the view
		Platform.runLater(() -> {
			gameView.killPlayer(playerFound);
		});
		// setting up a timer for game over
		// gameover-can-be-only-once validation
		if (gameOverTask == null) {
			gameOverTask = schedulerForGameOver.schedule(() -> gameOver(), GAME_OVER_TIME, TimeUnit.SECONDS);
		}
		return true;
	}

	// game over implementation
	private void gameOver() {
		if (!game.getPlayers().get(0).isAlive() && !game.getPlayers().get(1).isAlive()) {
			// add a DRAW message
			gameView.gameOver("DRAW");
			game.getPlayers().get(0).draw();
			game.getPlayers().get(1).draw();
			game.printPlayers();
			return;
		}
		if (!game.getPlayers().get(0).isAlive() && game.getPlayers().get(1).isAlive()) {
			// add a Player 2 wins message
			gameView.gameOver("Player 2 wins");
			game.getPlayers().get(0).win();
			game.getPlayers().get(1).loose();
			game.printPlayers();
			return;
		}
		if (game.getPlayers().get(0).isAlive() && !game.getPlayers().get(1).isAlive()) {
			// add a Player 1 wins message
			gameView.gameOver("Player 1 wins");
			game.getPlayers().get(1).win();
			game.getPlayers().get(0).loose();
			game.printPlayers();
			return;
		}
		// logger.log(Level.INFO, "Nobody wins");
		return;
	}

	@Override
	public void specifyPlayersSetAndLevel(ArrayList<Player> playersSet, String level) {
		this.playersSet = playersSet;
		this.level = level;
	}

	// returns the bomb that has its rays at the coordinates specified
	public boolean raysBombByCoordinates(Coordinates coordinates) {
		for (Bomb bomb : game.getBombsExploded()) {
			for (Coordinates raysCoords : bomb.getRays()) {
				if (raysCoords.getX() == coordinates.getX() && raysCoords.getY() == coordinates.getY()) {
					// logger.log(Level.INFO, "Player met explosion rays at the point " +
					// coordinates);
					return true;
				}
			}
		}
		return false;
	}

	// finds the modifier by coords
	public Modifier modifierByCoordinates(Coordinates coordinates) {
		for (Modifier modifier : game.getModifiers()) {
			if (modifier.getCoordinates().getX() == coordinates.getX()
					&& modifier.getCoordinates().getY() == coordinates.getY()) {
				return modifier;
			}
		}
		return null;
	}

	@Override
	public ArrayList<Player> getPlayers() {
		return game.getPlayers();
	}

	// gets a random modifier
	private ModifierType uncoverModifier(Coordinates coordinates) {
		int randomChoice = (int) (Math.random() * 5);
		List<ModifierType> arr = Arrays.asList(ModifierType.values());
		return arr.get(randomChoice);
	}

	// the player takes the modifier
	public boolean takeModifier(Player player, Modifier modifier) {
		Modifier modifierFound = player.findModifierById(modifier.getId());
		if (modifierFound != null) {
			return false;
		}
		player.addModifier(modifier);
		// setting up the scheduler for removing the modifier after its duration ending
		if (modifier.getDuration() != 0) {
			schedulerForModifiersOff.schedule(() -> player.removeModifier(modifier), modifier.getDuration(),
					TimeUnit.SECONDS);
		}
		logger.log(Level.INFO, "Player " + player.getName() + " got the modifier " + modifier);
		// SPEED_UP modifier processing
		if (modifier.getType() == ModifierType.SPEED_UP) {
			// logger.log(Level.INFO, "Speeding up the player " + player.getId());
			player.calculatePlayerSpeed();
		}
		// removing the modifier from the modifiers list
		game.removeModifier(modifier);
		Platform.runLater(() -> {
			gameView.removeMod(modifier);
		});
		return true;
	}

	// prints explosion rays. Used just for testing purpose
	private void printRays() {
		for (Bomb bomb : game.getBombs()) {
			for (Coordinates raysCoords : bomb.getRays()) {
			}
		}
	}

	// sets up a playersSet to play next
	@Override
	public void setPlayers(ArrayList<Player> players) {
		if (players == null || players.isEmpty()) {
			return;
		}
		// if the method isn't called from the application start
		if (!playersSet.isEmpty() || DataHandler.deserializePlayersSets() == null || DataHandler.deserializePlayersSets().isEmpty()) {
		
			// saving playerSet as the lastPlayerSet into the File
			DataHandler.saveLastPlayersSet(players);
			// saving playerSet as a regular playerSet into the File
			DataHandler.serializePlayersSet(players);
		}
		// setting up the playerSet value
		playersSet = players;
		logger.log(Level.INFO, "Saving counter from setPlayers, setting up the value =  " + Player.getCounter());
		DataHandler.saveCounterIntoFile();
	}

	// processing the confirmation of the gameover from GUI
	@Override
	public void gameOverComplete() {
		// closing the gameView window
		// logger.log(Level.INFO, "Closing the gameView window...");
		Platform.runLater(() -> {
			gameView.getStage().close();
			// show GUI main menu window
			mainMenuGUI.showMainMenu();
			// stops background music
			gameView.getMediaPlayer().stop();
		});

	}

	// for Pause feature
	@Override
	public void pauseOn() {
		// TBD
	}

	// for Pause feature
	@Override
	public void pauseOff() {
		// TBD
	}

	// sets up a level to play next
	@Override
	public void setLevel(String levelFromGUI) {
		level = levelFromGUI;
		// logger.log(Level.INFO, levelFromGUI + " was set from GUI");
	}

	// returns all previously saved playersSets from local files in the ArrayList
	@Override
	public ArrayList<ArrayList<Player>> getPlayersSets() {
		ArrayList<ArrayList<Player>> playersSets = new ArrayList<ArrayList<Player>>();
		playersSets = DataHandler.deserializePlayersSets();
		return playersSets;
	}

	// returns Id for the playersSet;
	public static String getPlayersSetId(ArrayList<Player> playersSet) {
		StringBuilder str = new StringBuilder();
		for (Player player : playersSet) {
			str.append(player.getId());
		}
		return str + "";
	}

	// resets the game's collections
	private void resetLists() {
		commandQueue = new LinkedBlockingQueue<>();
		schedulerForExplosion = Executors.newScheduledThreadPool(10);
		schedulerForRaysOff = Executors.newScheduledThreadPool(10);
		schedulerForGameOver = Executors.newScheduledThreadPool(1);
		schedulerForModifiersOff = Executors.newScheduledThreadPool(10);
	}

	// sets up the default players as the current game set
	public void setDefaultPlayers() {
		Player player1 = new Player(Game.getDefaultPlayer1Name(), new Coordinates(2, 6));
		Player player2 = new Player(Game.getDefaultPlayer2Name(), new Coordinates(4, 4));
		ArrayList<Player> defaultPlayersList = new ArrayList<>();
		defaultPlayersList.add(player1);
		defaultPlayersList.add(player2);
		setPlayers(defaultPlayersList);
	}

	// exits the app
	@Override
	public void exitApp() {
		// logger.log(Level.INFO, "Exiting the App...");
		System.exit(0);
	}

	@Override
	public ArrayList<Player> getPlayersSet() {
		return playersSet;
	}
}
