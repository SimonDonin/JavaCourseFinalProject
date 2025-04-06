package cyberpro.game.view;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
// Do not shure if it possible to use here. Or juct controller will use it.
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyCode;
import cyberpro.game.controller.ControllerInterface;
import cyberpro.game.controller.GameController;
import cyberpro.game.model.*;
import cyberpro.game.view.GameView;
import javafx.stage.Stage;

/**
 *
 * @author mikhail
 */
public class GameView {
	private ControllerInterface controller;
	private final int TILE_SIZE = 40;
	// This is actually size of a sprite. It will depend on actual size of game
	// board
	private GridPane grid;
	// Create GridPane object to display all graphical objects.
	// It is also possible to use TilePane, but we use GridPane because of better
	// control methods
	private TileType[][] gameBoard;
	// Game board array. We have to recive it from a Controller
	private final Image playerOneImage = new Image(getClass().getResourceAsStream("Character1.png"));
	private final Image brickWallImage = new Image(getClass().getResourceAsStream("BrickWall.png"));
	private final Image concreteWallImage = new Image(getClass().getResourceAsStream("ConcreteWall.png"));
	private final Image floorImage = new Image(getClass().getResourceAsStream("Floor.png"));
	private final Image bombImage = new Image(getClass().getResourceAsStream("Bomb.png"));
	private final Image enemyImage = new Image(getClass().getResourceAsStream("Enemy.png"));
	private final Image blastImage = new Image(getClass().getResourceAsStream("Blast.png"));

	private final int gridWidth = 12;
	private final int gridHeight = 12;
	// Shall be defined at model.

	/*
	 * @Override public void start(Stage primaryStage) {
	 * 
	 * grid = new GridPane(); grid.setFocusTraversable(true); // This is required to
	 * obtain a focus for keyboard input
	 * 
	 * 
	 * Scene scene = new Scene(grid, 600, 500); // Is it possible to create stages
	 * inside the toot Scene from class instances?
	 * 
	 * grid.setOnKeyPressed(this::handleKeyPress); // Allow key handling
	 * primaryStage.setScene(scene); primaryStage.setTitle("Bombermen");
	 * primaryStage.show();
	 * 
	 * 
	 * grid.requestFocus(); // Without this handler work is not granted. }
	 */

	public GameView(Stage stage, ControllerInterface controller, Board board) {
		this.controller = controller; // Сохраняем контроллер для дальнейшей работы
		this.gameBoard = board.getCells(); // Получаем массив плиток из Board

		grid = new GridPane();
		grid.setFocusTraversable(true); // Устанавливаем фокус для ввода

		 // Убираем отступы и промежутки
	    grid.setPadding(new Insets(0)); // Без отступов
	    grid.setHgap(0); // Без промежутков между колонками
	    grid.setVgap(0); // Без промежутков между строками
		
		int sceneSize = TILE_SIZE * gridWidth; // или gridHeight, если нужна квадратная область
		Scene scene = new Scene(grid, sceneSize, sceneSize);
		//Scene scene = new Scene(grid, 520, 520);
		grid.setOnKeyPressed(this::handleKeyPress); // Обработка нажатия клавиш
		stage.setScene(scene);
		stage.setTitle("Bombermen");
		stage.show();

		grid.requestFocus(); // Запрашиваем фокус
	}

	public GameView() {
		// gameBoard = board;
		return;
	}

	public void getBoard(TileType[][] board) {
		gameBoard = board;
	}

	public void drawGrid(ArrayList<Player> players, ArrayList<Bomb> bombs, ArrayList<Modifier> modifiers) {
		grid.getChildren().clear();
		for (int row = 0; row < gridWidth; row++) {
			for (int col = 0; col < gridHeight; col++) {
				ImageView tileView = new ImageView();
				tileView.setFitWidth(TILE_SIZE);
				tileView.setFitHeight(TILE_SIZE);
				switch (gameBoard[col][row]) {
				case FLOOR -> tileView.setImage(floorImage);
				/*
				 * case BRICK_WALL -> tileView.setImage(brickWallImage); case CONCRETE_WALL ->
				 * tileView.setImage(concreteWallImage);
				 */
				}
				grid.add(tileView, row, col);
			}
		}
		for (Player player : players) {
			ImageView tileView = new ImageView();
			tileView.setFitWidth(TILE_SIZE);
			tileView.setFitHeight(TILE_SIZE);
			// How to get player color??
			tileView.setImage(playerOneImage);
			
			if (player.getCoordinates().getX() < gridWidth && player.getCoordinates().getY() < gridHeight) {
			    grid.add(tileView, player.getCoordinates().getX(), player.getCoordinates().getY());
			} else {
			    System.out.println("Player is out of bounds: " + player.getCoordinates());
			}

			
			//grid.add(tileView, player.getCoordinates().getX(), player.getCoordinates().getY());
			// We just put player tile above the grid at the end of draw board cycle.
			// So it does not "shade" tiles below the player.
		}
		// All players are on map
		/*
		 * for (Bomb bomb : bombs) { ImageView tileView = new ImageView();
		 * tileView.setFitWidth(TILE_SIZE); tileView.setFitHeight(TILE_SIZE); // Do we
		 * need to know, who put the bomb?? tileView.setImage(bombImage);
		 * grid.add(tileView, bomb.getCoordinates().getX(),
		 * bomb.getCoordinates().getY()); // !! Bomb is not implemented yet. Wait for it
		 * } // All bombs are on map
		 */
		// Bomb class is not finished yet
	}

	private void handleKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.UP)
			controller.playerMoveUp(controller.getPlayerIdByNumber(1));
		if (event.getCode() == KeyCode.DOWN)
			controller.playerMoveDown(controller.getPlayerIdByNumber(1));
		if (event.getCode() == KeyCode.LEFT)
			controller.playerMoveLeft(controller.getPlayerIdByNumber(1));
		if (event.getCode() == KeyCode.RIGHT)
			controller.playerMoveRight(controller.getPlayerIdByNumber(1));

		if (event.getCode() == KeyCode.W)
			controller.playerMoveUp(controller.getPlayerIdByNumber(2));
		if (event.getCode() == KeyCode.S)
			controller.playerMoveDown(controller.getPlayerIdByNumber(2));
		if (event.getCode() == KeyCode.A)
			controller.playerMoveLeft(controller.getPlayerIdByNumber(2));
		if (event.getCode() == KeyCode.D)
			controller.playerMoveRight(controller.getPlayerIdByNumber(2));

		int playerNumber = 0;
		switch (event.getCode()) {
		case KeyCode.UP -> {
			controller.playerMoveUp(controller.getPlayerIdByNumber(1));
			playerNumber = 1;
		}
		case KeyCode.DOWN -> {
			controller.playerMoveDown(controller.getPlayerIdByNumber(1));
			playerNumber = 1;
		}
		case KeyCode.LEFT -> {
			controller.playerMoveLeft(controller.getPlayerIdByNumber(1));
			playerNumber = 1;
		}
		case KeyCode.RIGHT -> {
			controller.playerMoveRight(controller.getPlayerIdByNumber(1));
			playerNumber = 1;
		}
		case KeyCode.W -> {
			controller.playerMoveUp(controller.getPlayerIdByNumber(2));
			playerNumber = 2;
		}
		case KeyCode.S -> {
			controller.playerMoveDown(controller.getPlayerIdByNumber(2));
			playerNumber = 2;
		}
		case KeyCode.A -> {
			controller.playerMoveLeft(controller.getPlayerIdByNumber(2));
			playerNumber = 2;
		}
		case KeyCode.D -> {
			controller.playerMoveRight(controller.getPlayerIdByNumber(2));
			playerNumber = 2;
		}
		default -> {
			System.out.println("Not a valid key!");
		}
		}

		if (playerNumber != 0) {
			System.out.println("Key pressed " + "by the Player " + controller.getPlayerIdByNumber(playerNumber) + " "
					+ event.getCode());
		}
	}

	public void moveSprite(Coordinates coordNew, String playerID) {
		// [TODO]
	}

}
