package cyberpro.game.controller;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cyberpro.game.model.*;

public class GameController {
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

	// main menu: enter number of players, enter players' names, choose players'
	// colors, choose a map, then start
	public static void mainMenu() {
		/*
		 * create a game: Players list Modifiers initialization, Board, initialize
		 * Board, start the game
		 */

		// introducing a board
		Board board = new Board("myBoard", 12);
		board.initialize();

		// introducing a game
		Game game = new Game("myGame", 2, board);
		System.out.println(game);

		// introducing 2 players and adding to the game
		Player player1 = new Player("Player1", new Coordinates(2, 2));
		Player player2 = new Player("Player2", new Coordinates(11, 11));
		game.addPlayer(player1);
		game.addPlayer(player2);

		new Thread(() -> {
			try {
				while (true) {
					// Retrieving a command from the queue (waits if it's empty)
					String command = commandQueue.take();
					processCommand(command, game); // Command processing
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start();

	}

	private static void processCommand(String command, Game game) {
		switch (command.charAt(0)) {
		case 'U' -> moveUp(command.substring(1), game);
		case 'D' -> moveDown(command.substring(1), game);
		case 'L' -> moveLeft(command.substring(1), game);
		case 'R' -> moveRight(command.substring(1), game);
		default -> System.out.println("Unknown command");
		}

	}

	private static boolean moveRight(String playerId, Game game) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int x = playerFound.getCoordinates().getX() + playerFound.getSpeed();
		if (x > game.getBoard().getSize()) {
			return false;
		}
		playerFound.getCoordinates().setX(x);
		return true;
	}

	private static boolean moveLeft(String playerId, Game game) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int x = playerFound.getCoordinates().getX() - playerFound.getSpeed();
		if (x < 1) {
			return false;
		}
		playerFound.getCoordinates().setX(x);
		return true;
	}

	private static boolean moveDown(String playerId, Game game) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int y = playerFound.getCoordinates().getY() + playerFound.getSpeed();
		if (y > game.getBoard().getSize()) {
			return false;
		}
		playerFound.getCoordinates().setY(y);
		return true;
	}

	private static boolean moveUp(String playerId, Game game) {
		Player playerFound = game.findPlayerById(playerId);
		if (playerFound == null)
			return false;
		int y = playerFound.getCoordinates().getY() - playerFound.getSpeed();
		if (y < 1) {
			return false;
		}
		playerFound.getCoordinates().setY(y);
		return true;
	}

	// game process

	// Draw the objects

	// Start receiving input commands from GUI
	public void playerMoveUp(String playerId) {
		commandQueue.add("U" + playerId);
	}

	public void playerMoveDown(String playerId) {
		commandQueue.add("D" + playerId);
	}

	public void playerMoveLeft(String playerId) {
		commandQueue.add("L" + playerId);
	}

	public void playerMoveRight(String playerId) {
		commandQueue.add("R" + playerId);
	}

	// few times a second repeat the same actions:
	// process players' keyboard commands
	// cycle over all objects and check if any actions needed with them, if yes -
	// make them
}
