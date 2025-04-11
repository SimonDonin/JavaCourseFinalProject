package cyberpro.game.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import java.io.IOException;

public class GameController implements ControllerInterface {
	private static final String DEFAULT_LEVEL = "/cyberpro/game/model/level1.txt";
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static final int DEFAULT_TIME_TILL_EXPLOSION = 4;
	private static final TileType[] TILES_CAN_WALK_THROUGH = { TileType.FLOOR, TileType.EXPLOSION,
			TileType.RAY_HORIZONTAL, TileType.RAY_VERTICAL, TileType.RAY };
	private static final TileType[] TILES_CANT_WALK_THROUGH = { TileType.CONCRETE_WALL, TileType.BRICK_WALL };

	private static final TileType[] TILES_EXPLOSIBLE = { TileType.FLOOR, TileType.EXPLOSION, TileType.RAY_HORIZONTAL,
			TileType.RAY_VERTICAL, TileType.RAY, TileType.BRICK_WALL };
	private static final TileType[] TILES_DESTRUCTABLE = { TileType.BRICK_WALL };

	private ScheduledExecutorService schedulerForExplosion = Executors.newScheduledThreadPool(10);
	private ScheduledExecutorService schedulerForRaysOff = Executors.newScheduledThreadPool(10);

	// You a checkin tiles with is possible to walk abowe. But EXPLOSION, RAY

	// It is a sprite, not a tile. We will never have these tiles on board.
	private GameView gameView;
	Game game;

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

		System.out.println(getBoard());
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
					Platform.runLater(() -> {
						// gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
						// We do not redraw board each time.
						// Now we do this only if boad itself has been changed.
					});

				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start(); // Retrieving a command from the queue (waits if it's empty)

	}

	// processes a command from a Player
	private void processCommand(String command, Game game) {
		switch (command.charAt(0)) {
		case 'U' -> moveUp(command.substring(1));
		case 'D' -> moveDown(command.substring(1));
		case 'L' -> moveLeft(command.substring(1));
		case 'R' -> moveRight(command.substring(1));
		case 'B' -> plantBomb(command.substring(1));
		default -> System.out.println("Unknown command");
		}

	}

	// movement right with a board size validation
	private boolean moveRight(String playerId) {
		// find a player by Id
		Player playerFound = game.findPlayerById(playerId);

		if (playerFound == null)
			return false;
		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();

		// calculating a new X coordinate
		int x = playerFound.getCoordinates().getX() + playerFound.getSpeed();
		Coordinates newCoordinates = new Coordinates(x, playerFound.getCoordinates().getY());
		// check if the new X coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates) /* || isAnyPlayerHere(newCoordinates) */) {
			System.out.println("!isFreeToOccupy(newCoordinates)" + "=" + !isFreeToOccupy(newCoordinates));
			System.out.println("AnyPlayerHere(newCoordinates)" + "=" + isAnyPlayerHere(newCoordinates));
			System.out.println("Move from coordinates (" + currentX + ", " + currentY + ") to coordinates ("
					+ newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			System.out.println("newCoordinates = " + newCoordinates);

			return false;
		}
		// Begin of new code
		Coordinates oldCoord = new Coordinates(currentX, currentY);
		Coordinates newCoord = new Coordinates(x, playerFound.getCoordinates().getY());
		Platform.runLater(() -> {
			gameView.moveSprite(oldCoord, newCoord, playerFound);
		});
		// End of new code

		playerFound.getCoordinates().setX(x);

		return true;
	}

	// movement left with a board size validation
	private boolean moveLeft(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();
		int x = playerFound.getCoordinates().getX() - playerFound.getSpeed();
		Coordinates newCoordinates = new Coordinates(x, playerFound.getCoordinates().getY());
		// check if the new X coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates) /* | isAnyPlayerHere(newCoordinates) */) {
			System.out.println("!isFreeToOccupy(newCoordinates)" + "=" + !isFreeToOccupy(newCoordinates));
			System.out.println("AnyPlayerHere(newCoordinates)" + "=" + isAnyPlayerHere(newCoordinates));
			System.out.println("Move from coordinates (" + currentX + ", " + currentY + ") to coordinates ("
					+ newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			System.out.println("newCoordinates = " + newCoordinates);
			return false;
		}
		// Begin of new code
		Coordinates oldCoord = new Coordinates(currentX, currentY);
		Coordinates newCoord = new Coordinates(x, playerFound.getCoordinates().getY());
		Platform.runLater(() -> {
			gameView.moveSprite(oldCoord, newCoord, playerFound);
		});
		// End of new code

		playerFound.getCoordinates().setX(x);
		return true;
	}

	// movement down with a board size validation
	private boolean moveDown(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();
		int y = playerFound.getCoordinates().getY() + playerFound.getSpeed();
		Coordinates newCoordinates = new Coordinates(playerFound.getCoordinates().getX(), y);
		// check if the new Y coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates) /* || isAnyPlayerHere(newCoordinates) */) {
			System.out.println("!isFreeToOccupy(newCoordinates)" + "=" + !isFreeToOccupy(newCoordinates));
			System.out.println("AnyPlayerHere(newCoordinates)" + "=" + isAnyPlayerHere(newCoordinates));
			System.out.println("Move from coordinates (" + currentX + ", " + currentY + ") to coordinates ("
					+ newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			System.out.println("newCoordinates = " + newCoordinates);
			return false;
		}
		// Begin of new code
		// Coordinates newCoord = new Coordinates(playerFound.getCoordinates().getX(),
		// y);
		// Begin of new code
		Coordinates oldCoord = new Coordinates(currentX, currentY);
		Coordinates newCoord = new Coordinates(playerFound.getCoordinates().getX(), y);
		Platform.runLater(() -> {
			gameView.moveSprite(oldCoord, newCoord, playerFound);
		});
		// End of new code
		playerFound.getCoordinates().setY(y);
		return true;
	}

	// movement up with a board size validation
	private boolean moveUp(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int currentX = playerFound.getCoordinates().getX();
		int currentY = playerFound.getCoordinates().getY();
		int y = playerFound.getCoordinates().getY() - playerFound.getSpeed();
		Coordinates newCoordinates = new Coordinates(playerFound.getCoordinates().getX(), y);
		// check if the new Y coordinate is free for occupation
		if (!isFreeToOccupy(newCoordinates) /* || isAnyPlayerHere(newCoordinates) */) {
			System.out.println("!isFreeToOccupy(newCoordinates)" + "=" + !isFreeToOccupy(newCoordinates));
			System.out.println("AnyPlayerHere(newCoordinates)" + "=" + isAnyPlayerHere(newCoordinates));
			System.out.println("Move from coordinates (" + currentX + ", " + currentY + ") to coordinates ("
					+ newCoordinates.getX() + ", " + newCoordinates.getY() + ")");
			System.out.println("newCoordinates = " + newCoordinates);
			return false;
		}

		// Begin of new code
		Coordinates oldCoord = new Coordinates(currentX, currentY);
		Coordinates newCoord = new Coordinates(playerFound.getCoordinates().getX(), y);
		Platform.runLater(() -> {
			gameView.moveSprite(oldCoord, newCoord, playerFound);
		});
		// End of new code
		playerFound.getCoordinates().setY(y);
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
		System.out.println("\n\n" + bombFound);
		Coordinates iCoordinate;

		// adding explosion rays - separate iterations to 4 sides
		int raysRange = Bomb.getDefaultRaysRange();
		for (int i = 1; i <= raysRange; i++) {
			System.out.println("i = " + i);
			// collecting coordinates for potential rays
			// direction - to the right
			iCoordinate = new Coordinates(bombFound.getCoordinates().getX() + i, bombFound.getCoordinates().getY());

			// printing for diagnostics
			System.out.println("Horizontal coords are\n" + iCoordinate);
			System.out.println(
					"areCoordinatesInsideBoard(iCoordinate) = " + areCoordinatesInsideBoard(iCoordinate));
			System.out.println("isFreeToOccupy(iCoordinate) = " + isFreeToOccupy(iCoordinate));
			System.out.println("isExplosible(ihorizontalCoordinate) = " + isExplosible(iCoordinate));
			System.out.println("isAnyPlayerHere(ihorizontalCoordinate) = " + isAnyPlayerHere(iCoordinate));

			// killing players
			if (isAnyPlayerHere(iCoordinate)) {
				Player playerFound = playerByCoordinates(iCoordinate);
				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
				if (playerFound != null) {
					playerFound.kill();
				}
			}
			// if the coordinate is out of the board, quit the cycle
			if (!areCoordinatesInsideBoard(iCoordinate)) {
				break;
			}

			// find out if this coordinate is busy with a solid tile
			if (!isFreeToOccupy(iCoordinate)) {
				// if this solid tile is destructible, demolish it
				if (isDestructible(iCoordinate)) {
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					System.out.println("Demolishing BRICK WALL " + iCoordinate);
					draw();
					System.out.println("After demolishing the tile is " + game.getBoard().getCell(iCoordinate.getX(), iCoordinate.getY()));
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);
		}

		for (int i = 1; i <= raysRange; i++) {
			System.out.println("i = " + i);
			// collecting coordinates for potential rays
			// direction - to the left
			iCoordinate = new Coordinates(bombFound.getCoordinates().getX() - i, bombFound.getCoordinates().getY());

			// printing for diagnostics
			System.out.println("Horizontal coords are\n" + iCoordinate);
			System.out.println(
					"areCoordinatesInsideBoard(horizontalCoordinate) = " + areCoordinatesInsideBoard(iCoordinate));
			System.out.println("isFreeToOccupy(horizontalCoordinate) = " + isFreeToOccupy(iCoordinate));
			System.out.println("isExplosible(ihorizontalCoordinate) = " + isExplosible(iCoordinate));
			System.out.println("isAnyPlayerHere(ihorizontalCoordinate) = " + isAnyPlayerHere(iCoordinate));

			// killing players
			if (isAnyPlayerHere(iCoordinate)) {
				Player playerFound = playerByCoordinates(iCoordinate);
				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
				if (playerFound != null) {
					playerFound.kill();
				}
			}
			// if the coordinate is out of the board, quit the cycle
			if (!areCoordinatesInsideBoard(iCoordinate)) {
				break;
			}

			// find out if this coordinate is busy with a solid tile
			if (!isFreeToOccupy(iCoordinate)) {
				// if this solid tile is destructible, demolish it
				if (isDestructible(iCoordinate)) {
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					System.out.println("Demolishing BRICK WALL " + iCoordinate);
					draw();
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);
		}

		for (int i = 1; i <= raysRange; i++) {
			System.out.println("i = " + i);
			// collecting coordinates for potential rays
			// direction - up
			iCoordinate = new Coordinates(bombFound.getCoordinates().getX(), bombFound.getCoordinates().getY() - i);

			// printing for diagnostics
			System.out.println("Horizontal coords are\n" + iCoordinate);
			System.out.println(
					"areCoordinatesInsideBoard(horizontalCoordinate) = " + areCoordinatesInsideBoard(iCoordinate));
			System.out.println("isFreeToOccupy(horizontalCoordinate) = " + isFreeToOccupy(iCoordinate));
			System.out.println("isExplosible(ihorizontalCoordinate) = " + isExplosible(iCoordinate));
			System.out.println("isAnyPlayerHere(ihorizontalCoordinate) = " + isAnyPlayerHere(iCoordinate));

			// killing players
			if (isAnyPlayerHere(iCoordinate)) {
				Player playerFound = playerByCoordinates(iCoordinate);
				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
				if (playerFound != null) {
					playerFound.kill();
				}
			}
			// if the coordinate is out of the board, quit the cycle
			if (!areCoordinatesInsideBoard(iCoordinate)) {
				break;
			}

			// find out if this coordinate is busy with a solid tile
			if (!isFreeToOccupy(iCoordinate)) {
				// if this solid tile is destructible, demolish it
				if (isDestructible(iCoordinate)) {
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					System.out.println("Demolishing BRICK WALL " + iCoordinate);
					draw();
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);
		}

		for (int i = 1; i <= raysRange; i++) {
			System.out.println("i = " + i);
			// collecting coordinates for potential rays
			// direction - down
			iCoordinate = new Coordinates(bombFound.getCoordinates().getX(), bombFound.getCoordinates().getY() + i);

			// printing for diagnostics
			System.out.println("Horizontal coords are\n" + iCoordinate);
			System.out.println(
					"areCoordinatesInsideBoard(horizontalCoordinate) = " + areCoordinatesInsideBoard(iCoordinate));
			System.out.println("isFreeToOccupy(horizontalCoordinate) = " + isFreeToOccupy(iCoordinate));
			System.out.println("isExplosible(ihorizontalCoordinate) = " + isExplosible(iCoordinate));
			System.out.println("isAnyPlayerHere(ihorizontalCoordinate) = " + isAnyPlayerHere(iCoordinate));

			// killing players
			if (isAnyPlayerHere(iCoordinate)) {
				Player playerFound = playerByCoordinates(iCoordinate);
				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
				if (playerFound != null) {
					playerFound.kill();
				}
			}
			// if the coordinate is out of the board, quit the cycle
			if (!areCoordinatesInsideBoard(iCoordinate)) {
				break;
			}

			// find out if this coordinate is busy with a solid tile
			if (!isFreeToOccupy(iCoordinate)) {
				// if this solid tile is destructible, demolish it
				if (isDestructible(iCoordinate)) {
					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
					System.out.println("Demolishing BRICK WALL " + iCoordinate);
					draw();
				}
				// rays can't get through this obstacle
				break;
			}
			// adding ray at the icoordinate cell
			bombFound.addToRays(iCoordinate);
		}

	
//			iCoordinate = new Coordinates(bombFound.getCoordinates().getX(),
//					bombFound.getCoordinates().getY() + i);
//			System.out.println("Vertical coords are\n" + iCoordinate);
//			System.out.println("areCoordinatesInsideBoard(verticalCoordinate) = "
//					+ areCoordinatesInsideBoard(iCoordinate));
//			System.out.println("isFreeToOccupy(verticalCoordinate) = " + isFreeToOccupy(iCoordinate));
//			System.out.println("isExplosible(iverticalCoordinate) = " + isExplosible(iCoordinate));
//			System.out.println("isFreeToOccupy(iverticalCoordinate) = " + isFreeToOccupy(iCoordinate));
//			System.out.println("isAnyPlayerHere(iverticalCoordinate) = " + isAnyPlayerHere(iCoordinate));
//			// killing players
//			if (isAnyPlayerHere(iCoordinate)) {
//				Player playerFound = playerByCoordinates(iCoordinate);
//				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
//				if (playerFound != null) {
//					playerFound.kill();
//				}
//			} else if (isAnyPlayerHere(iCoordinate)) {
//				System.out.println("Player is here ! It's " + playerByCoordinates(iCoordinate));
//				Player playerFound = playerByCoordinates(iCoordinate);
//				if (playerFound != null) {
//					playerFound.kill();
//				}
//			}
//			
//			if (areCoordinatesInsideBoard(iCoordinate) && isExplosible(iCoordinate) && i != 0) {
//				bombFound.addToRaysHorizontal(iCoordinate);
//				if (game.getBoard().getCell(iCoordinate.getY(),
//						iCoordinate.getX()) == TileType.BRICK_WALL) {
//					game.getBoard().setCell(iCoordinate.getX(), iCoordinate.getY(), TileType.FLOOR);
//					System.out.println("Demolishing BRICK WALL " + iCoordinate);
//					draw();
//				}
//				System.out.println("Horizontal rays coords are\n" + bombFound.getRaysHorizontal());
//				System.out.println("Vertical rays coords are\n" + bombFound.getRaysVertical());
//			}
//			
//			// adding vertical explosion rays
//			if (areCoordinatesInsideBoard(iCoordinate) && isExplosible(iCoordinate) && i != 0) {
//				bombFound.addToRaysVertical(iCoordinate);
//				if (game.getBoard().getCell(iCoordinate.getY(),
//						iCoordinate.getX()) == TileType.BRICK_WALL) {
//					game.getBoard().setCell(iCoordinate.getY(), iCoordinate.getX(), TileType.FLOOR);
//					System.out.println("Demolishing BRICK WALL " + iCoordinate);
//					draw();
//				}
//				System.out.println("Horizontal rays coords are\n" + bombFound.getRaysHorizontal());
//				System.out.println("Vertical rays coords are\n" + bombFound.getRaysVertical());
//			}
//		}
//		System.out.println("Bomb coords are\n" + bombFound.getCoordinates());
//		System.out.println("Horizontal rays coords are\n" + bombFound.getRaysHorizontal());
//		System.out.println("Vertical rays coords are\n" + bombFound.getRaysVertical());

		Platform.runLater(() -> {
			gameView.blastBomb(bombFound, bombFound.getRays());
		});

		// KILLING PLAYERS

		// !!!EXCLUDE RAYS THROUGH OBSTACLES!!! AND DESTROY WALLS!!!

		// calculating time for rays to disappear and Setting up a timer to do it

		schedulerForRaysOff.schedule(() -> draw(), Bomb.getDefaultRaysDuration(), TimeUnit.SECONDS);

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
			System.out.println("Obstacle = " + tileToValidate);
			return false;
		}
		return true;
	}

	// identify if this cell is free for rays
	public boolean isExplosible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_EXPLOSIBLE).contains(tileToValidate)) {
			System.out.println("Not explosible = " + tileToValidate);
			return false;
		}
		return true;
	}

	public boolean isDestructible(Coordinates coordinates) {
		TileType tileToValidate = game.getBoard().getCells()[coordinates.getX()][coordinates.getY()];
		if (!Arrays.asList(TILES_DESTRUCTABLE).contains(tileToValidate)) {
			System.out.println("Not destructable = " + tileToValidate);
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

	@Override
	public ArrayList<Player> getPlayers() {
		return game.getPlayers();
	}

	/*
	 * public ArrayList<Coordinates> getRaysCoordinates (Coordinates
	 * explosionCoordinates) { ArrayList<Coordinates> toReturn = new
	 * ArrayList<Coordinates>(); if
	 * (areCoordinatesInsideBoard(explosionCoordinates)) { return null; } for ()
	 * 
	 * }
	 */
}
