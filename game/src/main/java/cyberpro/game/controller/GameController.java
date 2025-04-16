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
import java.util.concurrent.TimeUnit;

// This is starter for JavaFX window
import javafx.application.Platform;
import javafx.stage.Stage;
import cyberpro.game.model.*;
import cyberpro.game.service.FileResourcesImporter;
import cyberpro.game.view.GameView;
import cyberpro.game.view.TileType;
import cyberpro.game.model.Modifier;

import java.io.IOException;

public class GameController implements ControllerInterface {
	private static final String DEFAULT_LEVEL = "/cyberpro/game/model/level1.txt";
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static final int DEFAULT_TIME_TILL_EXPLOSION = 4;
	private static final TileType[] TILES_CANT_WALK_THROUGH = { TileType.CONCRETE_WALL, TileType.BRICK_WALL };
	private static final TileType[] TILES_EXPLOSIBLE = { TileType.FLOOR, TileType.EXPLOSION, TileType.BRICK_WALL };
	private static final TileType[] TILES_DESTRUCTIBLE = { TileType.BRICK_WALL };
	private static final int DEFAULT_MAX_BOMBS_ACTIVE = 1;
	private static final int GAME_OVER_TIME = 5;
	private ScheduledExecutorService schedulerForExplosion = Executors.newScheduledThreadPool(10);
	private ScheduledExecutorService schedulerForRaysOff = Executors.newScheduledThreadPool(10);
	private ScheduledExecutorService schedulerForGameOver = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService schedulerForModifiersOff = Executors.newScheduledThreadPool(10);

	// You a checkin tiles with is possible to walk abowe. But EXPLOSION, RAY

	// It is a sprite, not a tile. We will never have these tiles on board.
	private GameView gameView;
	private Game game;

	// main menu: enter number of players, enter players' names, choose players'
	// colors, choose a map, then start

	@Override
	public Board getBoard() {
		return game.getBoard();
	}

	// it is where users define their players and choose a level to play
	public void mainMenu() throws IOException {

		// initialization
		// File
		FileResourcesImporter fileResourcesImporter = new FileResourcesImporter();

		// create a game
		game = new Game("myGame", fileResourcesImporter.importLevelIntoBoard("level", DEFAULT_LEVEL));

		System.out.println("Our random modifier = " + uncoverModifier(new Coordinates(0, 0)));
		// specify players in the Start menu and create them
		game.addPlayer(new Player("Player1", new Coordinates(4, 4)));
		game.addPlayer(new Player("Player2", new Coordinates(2, 6)));

		gameProcess();

	}

	private void gameProcess() {

		// GameView initialization
		Platform.startup(() -> {
			Stage stage = new Stage();
			gameView = new GameView(stage, this);
		});

		draw();

		// running a thread for dealing with a commands queue
		new Thread(() -> {
			try {
				while (true) {
					String command = commandQueue.take();
					processCommand(command, game); // Command processing
					// gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
					/*
					 * Platform.runLater(() -> { // gameView.drawGrid(game.getPlayers(),
					 * game.getBombs(), game.getModifiers()); // We do not redraw board each time.
					 * // Now we do this only if boad itself has been changed. });
					 */

				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start(); // Retrieving a command from the queue (waits if it's empty)

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
			System.out.println("Got a plant bomb command for a queue");
		}
		default -> System.out.println("Unknown command");
		}
		return true;
	}

	// movement in the direction specified
	private boolean moveInDirection(String playerId, String direction) {
		// find a player by Id
		Player playerFound = game.findPlayerById(playerId);

		if (playerFound == null)
			return false;

		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();

		int newX = 0;
		int newY = 0;

		// REVERSE modifier implementation
		if (playerFound.findModifierByType(ModifierType.REVERSE_CONTROLS) != null) {
			switch (direction) {
			case "right" -> direction = "left";
			case "left" -> direction = "right";
			case "up" -> direction = "down";
			case "down" -> direction = "up";
			default -> System.out.println("Unknown command. Can't be reversed");
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
			System.out.println("No such direction!");
			break;
		}
		}

		Coordinates newCoordinates = new Coordinates(newX, newY);
		// check if the new X coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates)
				|| bombByCoordinates(newCoordinates) != null /* || isAnyPlayerHere(newCoordinates) */) {
			System.out.println("!isFreeToOccupy(newCoordinates)" + "=" + !isFreeToOccupy(newCoordinates));
			System.out.println("AnyPlayerHere(newCoordinates)" + "=" + isAnyPlayerHere(newCoordinates));
			System.out.println("Move from coordinates (" + currentX + ", " + currentY + ") to coordinates ("
					+ newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			System.out.println("newCoordinates = " + newCoordinates);

			return false;
		}
		Coordinates oldCoordinates = new Coordinates(currentX, currentY);

		// getting a modifier
		Modifier modifierFound = modifierByCoordinates(newCoordinates);
		if (modifierFound != null) {
			takeModifier(playerFound, modifierFound);
		}

		Platform.runLater(() -> {
			// updating the player's sprite in the view
			gameView.moveSprite(oldCoordinates, newCoordinates, playerFound);
			// System.out.println("raysBombByCoordinates =" +
			// raysBombByCoordinates(newCoordinates));
			// if player moves to the active rays he dies
		});
		if (raysBombByCoordinates(newCoordinates)) {
			killPlayer(playerFound);
		}

		// actually move the player
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
			System.out.println("No such direction!");
			break;
		}
		}

		return true;
	}

	// game process

	// Draw the objects
	private static void playersViewUpdate() {
		// send off Players' Data
		// actually call the View's method

	}

	// receiving input commands from VIEW
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
	public String getPlayerIdByNumber(int playerNumber) {
		if (game.getPlayers().isEmpty()) {
			System.out.println("Players list is empty!");
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
			System.out.println("Players list is empty!");
			return;
		}
		Player playerFound = game.findPlayerById(playerId);
		// validation if a player isn't found by Id
		if (playerFound == null)
			return;
		// only allowed amount of bombs can be planted by the player at the same time
		if (countBombsActive(playerId) >= countBombsAllowed(playerId)) {
			System.out.println("Player " + playerId + " can't plant more bombs: current active bombs number = "
					+ countBombsActive(playerId) + " and allowed = " + countBombsAllowed(playerId));
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

		// creating a bomb and adding to the bombs list
		Bomb newBomb = new Bomb(playerId,
				new Coordinates(playerFound.getCoordinates().getX(), playerFound.getCoordinates().getY()), false,
				explosionTime);
		game.addBomb(newBomb);

		// setting up a timer for bomb to change its look
				schedulerForExplosion.schedule(() -> {
					Platform.runLater(() -> {
						gameView.plantBomb(newBomb);
					});
					System.out.println("Requested the bomb to change its look");}, DEFAULT_TIME_TILL_EXPLOSION / 2,
						TimeUnit.SECONDS);
		
		// setting up a timer for bomb to explode
		schedulerForExplosion.schedule(() -> explodeBomb(newBomb.getId()), DEFAULT_TIME_TILL_EXPLOSION,
				TimeUnit.SECONDS);

		System.out.println("Player " + playerId + " planted a bomb");
		// ask view to draw the bomb
		Platform.runLater(() -> {
			gameView.plantBomb(newBomb);
		});
	}

	public void explodeBomb(String bombId) {
		Bomb bombFound = game.findBombById(bombId);
		// validation if a bomb isn't found by Id
		if (bombFound == null)
			return;
		bombFound.explode();
		/*
		 * System.out.println("\n\n" + bombFound);
		 * System.out.println("\n\nFound a bomb:\n" + bombFound);
		 */
		makeRaysInOneDirection(bombFound, "right");
		makeRaysInOneDirection(bombFound, "left");
		makeRaysInOneDirection(bombFound, "up");
		makeRaysInOneDirection(bombFound, "down");
		makeRaysInOneDirection(bombFound, "center");

		// rays array validation
		/*
		 * System.out.println("\nOur bomb is still " + bombFound);
		 * System.out.println("Rays array is " + bombFound.getRays());
		 */

		Platform.runLater(() -> {
			// draw explosion in the view
			gameView.blastBomb(bombFound, bombFound.getRays());
			// transfer the bomb from the bombs list to the exploded bombs list
			game.assignExploded(bombFound);
		});
		/*
		 * System.out.println("Bombs array before removal = " + game.getBombs());
		 * System.out.println("Bombs array after removal = " + game.getBombs());
		 */

		// calculating time for rays to disappear and Setting up a timer to do it
		// eliminating the bomb from the exploded bombs list
		schedulerForRaysOff.schedule(() -> {
			draw();
			game.fullRemove(bombFound);
		}, Bomb.getDefaultRaysDuration(), TimeUnit.SECONDS);

	}

	public void makeRaysInOneDirection(Bomb bombFound, String direction) {

		Coordinates iCoordinate;
		int raysRange = calculateRaysRange(bombFound.getPlayerId());
		// System.out.println("\nMoving " + direction + "\n");

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
				System.out.println("No such direction!");
				return;
			}
			}

			// calculating variables for making decisions
			boolean insideBoard = areCoordinatesInsideBoard(iCoordinate);
			boolean freeToOccupy = isFreeToOccupy(iCoordinate);
			boolean explosible = isExplosible(iCoordinate);
			boolean playerHere = isAnyPlayerHere(iCoordinate);
			boolean destructible = isDestructible(iCoordinate);

			// printing for diagnostics
			/*
			 * System.out.println("i = " + i); System.out.println("Coordinate " +
			 * iCoordinate + " is being considered");
			 * System.out.println("Inside the board = " + insideBoard);
			 * System.out.println("FreeToOccupy = " + freeToOccupy);
			 * System.out.println("Explosible = " + explosible);
			 * System.out.println("AnyPlayerHere = " + playerHere);
			 * System.out.println("Destructible = " + destructible);
			 */

			// if the coordinate is out of the board, quit the cycle
			if (!insideBoard) {
				break;
			}

			// killing players
			if (playerHere) {
				Player playerFound = playerByCoordinates(iCoordinate);
				// System.out.println("Player is here ! It's " + playerFound.getName());
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

					// printing tile before the change
					/*
					 * System.out.println("Our board is still " + game.getBoard());
					 * System.out.println("Before demolishing the tile is " +
					 * game.getBoard().getCell(iCoordinate.getX(), iCoordinate.getY()));
					 * System.out.println("Demolishing BRICK WALL " + iCoordinate); // tile change
					 * to the FLOOR
					 */
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					// printing tile after the change
					/*
					 * System.out.println("After demolishing the tile is " +
					 * game.getBoard().getCell(iCoordinate.getX(), iCoordinate.getY()));
					 */
					// adding rays at the cell being destroyed
					bombFound.addToRays(iCoordinate);

					// uncovering a modifier under the destroyed brick wall
					Modifier newModifier = new Modifier(iCoordinate, uncoverModifier(iCoordinate));
					game.addModifier(newModifier);
					System.out.println("The modifiers list now is " + game.getModifiers());
					Platform.runLater(() -> gameView.plantMod(newModifier));
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);

			// exploding a bomb which is on the rays way
			Bomb bombToExplode = bombByCoordinates(iCoordinate);
			if (bombToExplode != null && bombToExplode.getRaysOffDate() == null) {
				// running a thread for exploding another bomb
				System.err.println("Bomb " + bombFound.getId() + " detonated bomb " + bombToExplode.getId());
				new Thread(() -> {
					explodeBomb(bombToExplode.getId());
				}).start();
			}

		}
	}

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

	public void draw() {
		Platform.runLater(() -> gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers()));
	}

	// coordinates are inside the board
	public boolean areCoordinatesInsideBoard(Coordinates coordinates) {
		if (coordinates.getX() > game.getBoard().getSize() - 1 || coordinates.getX() < 0
				|| coordinates.getY() > game.getBoard().getSize() - 1 || coordinates.getY() < 0) {
			return false;
		}
		return true;
	}

	// collision detection method: if player can occupy this cell
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

	// identify if this cell is free for rays
	public boolean isExplosible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_EXPLOSIBLE).contains(tileToValidate)) {
			return false;
		}
		return true;
	}

	public boolean isDestructible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_DESTRUCTIBLE).contains(tileToValidate)) {
			return false;
		}
		return true;
	}

	public boolean isAnyPlayerHere(Coordinates coordinates) {
		for (Player player : game.getPlayers()) {
			if (player.getCoordinates().getX() == coordinates.getX()
					&& player.getCoordinates().getY() == coordinates.getY()) {
				return true;
			}
		}
		return false;
	}

	public boolean isAnyBombHere(Coordinates coordinates) {
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getCoordinates().getX() == coordinates.getX()
					&& bomb.getCoordinates().getY() == coordinates.getY()) {
				return true;
			}
		}
		return false;
	}

	public Player playerByCoordinates(Coordinates coordinates) {
		for (Player player : game.getPlayers()) {
			if (player.getCoordinates().getX() == coordinates.getX()
					&& player.getCoordinates().getY() == coordinates.getY()) {
				return player;
			}
		}
		return null;
	}

	public Bomb bombByCoordinates(Coordinates coordinates) {
		for (Bomb bomb : game.getBombs()) {
			if (bomb.getCoordinates().getX() == coordinates.getX()
					&& bomb.getCoordinates().getY() == coordinates.getY()) {
				return bomb;
			}
		}
		return null;
	}

	public boolean killPlayer(Player player) {
		// can't kill twice
		if (!player.isAlive()) {
			return false;
		}
		// find a player by Id
		Player playerFound = game.findPlayerById(player.getId());
		if (playerFound == null)
			return false;
		playerFound.kill();
		// killing the player in the view
		Platform.runLater(() -> {
			System.err.println("Sending requiest to gameView to kill the player");
			gameView.killPlayer(playerFound);
		});
		// setting up a timer for game over
		schedulerForGameOver.schedule(() -> gameOver(), GAME_OVER_TIME, TimeUnit.SECONDS);
		return true;
	}

	// game over implementation
	private void gameOver() {
		if (!game.getPlayers().get(0).isAlive() && !game.getPlayers().get(1).isAlive()) {
			gameView.gameOver("DRAW"); // add a DRAW message
			System.out.println("Sending a request for the DRAW message");
			return;
		}
		if (!game.getPlayers().get(0).isAlive() && game.getPlayers().get(1).isAlive()) {
			gameView.gameOver("Player 2 wins"); // add a Player 2 wins message message
			System.out.println("Sending a request for the Player 1 WINS message");
			return;
		}
		if (game.getPlayers().get(0).isAlive() && !game.getPlayers().get(1).isAlive()) {
			gameView.gameOver("Player 1 wins"); // add a Player 1 wins message
			System.out.println("Sending a request for the Player 2 WINS message");
			return;
		}
		System.out.println("Noone wins");
		return;

	}

	// returns the bomb that has its rays at the coordinates specified
	public boolean raysBombByCoordinates(Coordinates coordinates) {
		for (Bomb bomb : game.getBombsExploded()) {
			System.out.println(bomb.getRays());
			for (Coordinates raysCoords : bomb.getRays()) {
				System.out.println(raysCoords);
				System.out.println(coordinates);
				if (raysCoords.getX() == coordinates.getX() && raysCoords.getY() == coordinates.getY()) {
					return true;
				}
			}
		}
		return false;
	}

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

	private ModifierType uncoverModifier(Coordinates coordinates) {
		int randomChoice = (int) (Math.random() * 5);
		List<ModifierType> arr = Arrays.asList(ModifierType.values());
		return arr.get(randomChoice);
	}

	public boolean takeModifier(Player player, Modifier modifier) {
		Modifier modifierFound = player.findModifierById(modifier.getId());
		System.out.println(modifierFound);
		if (modifierFound != null) {
			return false;
		}
		player.addModifier(modifier);
		// setting up the scheduler for removing the modifier after its duration ending
		if (modifier.getDuration() != 0) {
			schedulerForGameOver.schedule(() -> player.removeModifier(modifier), modifier.getDuration(),
					TimeUnit.SECONDS);
		}

		System.out.println("Player " + player.getName() + " got the modifier " + modifier);

		// if it is SPEED_UP
		if (modifier.getType() == ModifierType.SPEED_UP) {
			System.err.println("Speed up the player " + player.getId());
			player.calculatePlayerSpeed();
		}

		game.removeModifier(modifier);
		// removing the modifier in the view
				Platform.runLater(() -> {
					System.err.println("Sending requiest to gameView to remove the modifier");
					gameView.removeMod(modifier);
				});
		return true;
	}

	private void printRays() {
		for (Bomb bomb : game.getBombs()) {
			for (Coordinates raysCoords : bomb.getRays()) {
				System.out.println(raysCoords);

			}
		}
	}
}
