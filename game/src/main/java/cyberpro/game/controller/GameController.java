package cyberpro.game.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// This is starter for JavaFX window
import javafx.application.Platform;
import javafx.stage.Stage;
import cyberpro.game.model.*;
import cyberpro.game.view.GameView;
import java.io.IOException;

public class GameController implements ControllerInterface {
	private static BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private GameView gameView;
	Board board;
        // Board is a part of model, we could just put getter here
	Game game = new Game("myGame", 2);

	// main menu: enter number of players, enter players' names, choose players'
	// colors, choose a map, then start
<<<<<<< Updated upstream
	public void mainMenu() throws IOException {
=======
	
	@Override
	public Board getBoard() {
		return board;
	}
	
	public void mainMenu() {
>>>>>>> Stashed changes
		/*
		 * create a game: Players list Modifiers initialization, Board, initialize
		 * Board, start the game
		 */
		// !!!TEST BLOCK!!!
		// introducing a board
                int gridWidth;
		int gridHeight;
		game.loadLevel("level1.txt");
                board = game.getBoard();
                gridWidth = gridHeight = board.getSize();
                // We dicided to have a square board, but it is possible to rectangle board

		// introducing a game
		//Game game = new Game("myGame", 2, board);

		// introducing 2 players and adding to the game
		Player player1 = new Player("Player1", new Coordinates(2, 2));
		Player player2 = new Player("Player2", new Coordinates(7, 6));
<<<<<<< Updated upstream
		// int gridWidth = 12;
		// int gridHeight = 12;
=======
>>>>>>> Stashed changes

		game.addPlayer(player1);
		game.addPlayer(player2);

		// print game before
		System.out.println(game);

		// GameView initialization
		Platform.startup(() -> {
			Stage stage = new Stage();
			gameView = new GameView(stage, this);
		});
		Platform.runLater(() -> {
		    gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
		});

		new Thread(() -> {
			try {
				while (true) {
					String command = commandQueue.take();
					processCommand(command, game); // Command processing
					//gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
					Platform.runLater(() -> {
					    gameView.drawGrid(game.getPlayers(), game.getBombs(), game.getModifiers());
					});

				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start(); // Retrieving a command from the queue (waits if it's empty)

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
		// Application.launch(GameView.class);

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
		if (x > game.getBoard().getSize() - 1) {
			return false;
		}
		playerFound.getCoordinates().setX(x);
		System.out.println(game.getBoard().getSize());
		return true;
	}

	// movement left with a board size validation
	private static boolean moveLeft(String playerId, Game game) {
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
	private static boolean moveDown(String playerId, Game game) {
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
	private static boolean moveUp(String playerId, Game game) {
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



	
	
}
