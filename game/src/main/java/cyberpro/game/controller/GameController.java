package cyberpro.game.controller;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.application.Application;
// This is starter for JavaFX window

import cyberpro.game.model.*;
import cyberpro.game.view.GameView;


public class GameController {
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

	// main menu: enter number of players, enter players' names, choose players'
	// colors, choose a map, then start
	public static void mainMenu() {
		/*
		 * create a game: Players list Modifiers initialization, Board, initialize
		 * Board, start the game
		 */
		// !!!TEST BLOCK!!!
		// introducing a board
		Board board = new Board("myBoard", 12);
		board.initialize();

		// introducing a game
		Game game = new Game("myGame", 2, board);

		// introducing 2 players and adding to the game
		Player player1 = new Player("Player1", new Coordinates(2, 2));
		Player player2 = new Player("Player2", new Coordinates(11, 11));
		game.addPlayer(player1);
		game.addPlayer(player2);

		// print game before
		System.out.println(game);

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

		// Emulating Players' actions
		moveLeft("P1", game);
		moveRight("P1", game);
		moveRight("P1", game);
		moveDown("P1", game);
		moveDown("P1", game);
		moveDown("P2", game);
		moveLeft("P2", game);

		// print game after some players actions
		System.out.println(game);
		
		// GameView gameView = new GameView(board.getCells());
		Application.launch(GameView.class);
	}

	// processes a command from a Player
	private static void processCommand(String command, Game game) {
		switch (command.charAt(0)) {
		case 'U' -> moveUp(command.substring(1), game);
		case 'D' -> moveDown(command.substring(1), game);
		case 'L' -> moveLeft(command.substring(1), game);
		case 'R' -> moveRight(command.substring(1), game);
		default -> System.out.println("Unknown command");
		}

	}

	// movement right with a board size validation
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

	// movement left with a board size validation
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

	// movement down with a board size validation
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

	// movement up with a board size validation
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
	private static void playersViewUpdate() {
		// send off Players' Data
		// actually call the View's method

	}

	// receiving input commands from VIEW
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

}
