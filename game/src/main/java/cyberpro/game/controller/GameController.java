package cyberpro.game.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// This is starter for JavaFX window
import javafx.application.Platform;
import javafx.stage.Stage;
import cyberpro.game.model.*;
import cyberpro.game.service.FileResourcesImporter;
import cyberpro.game.view.GameView;
import java.io.IOException;

public class GameController implements ControllerInterface {
	private static final String DEFAULT_LEVEL = "/cyberpro/game/model/level1.txt";
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static final int DEFAULT_TIME_TILL_EXPLOSION = 4;
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

		// specify players in the Start menu and create them
		game.addPlayer(new Player("Player1", new Coordinates(2, 2)));
		game.addPlayer(new Player("Player2", new Coordinates(7, 6)));

		gameProcess();

	}

	private void gameProcess() {
		
		// GameView initialization
		Platform.startup(() -> {
			Stage stage = new Stage();
			gameView = new GameView(stage, this);
		});
		Platform.runLater(() -> {
			gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
		});

		// running a thread for dealing with a commands queue
		new Thread(() -> {
			try {
				while (true) {
					String command = commandQueue.take();
					processCommand(command, game); // Command processing
					// gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
					Platform.runLater(() -> {
						gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
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
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int x = playerFound.getCoordinates().getX() + playerFound.getSpeed();
		if (x > game.getBoard().getSize() - 1) {
			return false;
		}
		playerFound.getCoordinates().setX(x);
		return true;
	}

	// movement left with a board size validation
	private boolean moveLeft(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int x = playerFound.getCoordinates().getX() - playerFound.getSpeed();
		if (x < 0) {
			return false;
		}
		playerFound.getCoordinates().setX(x);
		return true;
	}

	// movement down with a board size validation
	private boolean moveDown(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int y = playerFound.getCoordinates().getY() + playerFound.getSpeed();
		if (y > game.getBoard().getSize() - 1) {
			return false;
		}
		playerFound.getCoordinates().setY(y);
		return true;
	}

	// movement up with a board size validation
	private boolean moveUp(String playerId) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int y = playerFound.getCoordinates().getY() - playerFound.getSpeed();
		if (y < 0) {
			return false;
		}
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
		if (game.getPlayers().isEmpty()) {
			System.out.println("Players list is empty!");
			return;
		}
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return;
		
		Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, DEFAULT_TIME_TILL_EXPLOSION);

        Date explosionTime = calendar.getTime();
		
		game.addBomb(new Bomb(playerId, playerFound.getCoordinates(), false, explosionTime));
		System.out.println("Player " + playerId + " planted a bomb");
	}
}
