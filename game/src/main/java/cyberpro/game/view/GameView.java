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
// Animation libs
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
// End animation libs
import java.util.HashMap;
import java.util.Map;

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

        private final Map<String, ImageView> playerSprites = new HashMap<>();
 
	private int gridWidth;
	private int gridHeight;
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

	public GameView(Stage stage, ControllerInterface controller) {
		this.controller = controller; // Сохраняем контроллер для дальнейшей работы
		this.gameBoard = controller.getBoard().getCells(); // Получаем массив плиток из Board

		grid = new GridPane();
		grid.setFocusTraversable(true); // Устанавливаем фокус для ввода

		// Убираем отступы и промежутки
		grid.setPadding(new Insets(0)); // Без отступов
		grid.setHgap(0); // Без промежутков между колонками
		grid.setVgap(0); // Без промежутков между строками
                
                gridWidth = gridHeight = controller.getBoard().getSize();

		int sceneSize = TILE_SIZE * gridWidth; // или gridHeight, если нужна квадратная область
		Scene scene = new Scene(grid, sceneSize, sceneSize);
		// Scene scene = new Scene(grid, 520, 520);
		grid.setOnKeyPressed(this::handleKeyPress); // Обработка нажатия клавиш
		stage.setScene(scene);
		stage.setTitle("Bombermen");
		stage.show();

		grid.requestFocus(); // Запрашиваем фокус
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
                            switch (gameBoard[row][col]) {
				case FLOOR -> tileView.setImage(floorImage);
				case BRICK_WALL -> tileView.setImage(brickWallImage);
				case CONCRETE_WALL -> tileView.setImage(concreteWallImage);
                            }
                            grid.add(tileView, row, col);
			}
		}
		for (Player player : players) {
                    String playerID = player.getId();
                    ImageView playerView = new ImageView(playerOneImage);
                    playerView.setFitWidth(TILE_SIZE);
                    playerView.setFitHeight(TILE_SIZE);
                    playerSprites.put(playerID, playerView);
                    grid.add(playerView, player.getCoordinates().getX(), player.getCoordinates().getY());
                }
		// All players are on map
                
                // Place bombs
		for (Bomb bomb : bombs) {
                    ImageView bombView = new ImageView(bombImage);
                    bombView.setFitWidth(TILE_SIZE);
                    bombView.setFitHeight(TILE_SIZE);
                    grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());																			// it
		} 
                // All bombs are on map

		// Bomb class is not finished yet
	}

	private void handleKeyPress(KeyEvent event) {
		/*
		 * if (event.getCode() == KeyCode.UP)
		 * controller.playerMoveUp(controller.getPlayerIdByNumber(1)); if
		 * (event.getCode() == KeyCode.DOWN)
		 * controller.playerMoveDown(controller.getPlayerIdByNumber(1)); if
		 * (event.getCode() == KeyCode.LEFT)
		 * controller.playerMoveLeft(controller.getPlayerIdByNumber(1)); if
		 * (event.getCode() == KeyCode.RIGHT)
		 * controller.playerMoveRight(controller.getPlayerIdByNumber(1));
		 * 
		 * if (event.getCode() == KeyCode.W)
		 * controller.playerMoveUp(controller.getPlayerIdByNumber(2)); if
		 * (event.getCode() == KeyCode.S)
		 * controller.playerMoveDown(controller.getPlayerIdByNumber(2)); if
		 * (event.getCode() == KeyCode.A)
		 * controller.playerMoveLeft(controller.getPlayerIdByNumber(2)); if
		 * (event.getCode() == KeyCode.D)
		 * controller.playerMoveRight(controller.getPlayerIdByNumber(2));
		 */

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
		case KeyCode.G -> {
			controller.playerPlantBomb(controller.getPlayerIdByNumber(2));
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

	public void moveSprite(Coordinates newCoord, String playerID) {
            ImageView playerView = playerSprites.get(playerID);
            if (playerView == null) return;
 
            Integer currentCol = GridPane.getColumnIndex(playerView);
            Integer currentRow = GridPane.getRowIndex(playerView);
 
            if (currentCol == null || currentRow == null) return;
 
            int newCol = newCoord.getX();
            int newRow = newCoord.getY();
 
            double dx = (newCol - currentCol) * TILE_SIZE;
            double dy = (newRow - currentRow) * TILE_SIZE;
 
            TranslateTransition transition = new TranslateTransition(Duration.millis(150), playerView);
            transition.setByX(dx);
            transition.setByY(dy);
            transition.setOnFinished(e -> {
                playerView.setTranslateX(0);
 		playerView.setTranslateY(0);
 		GridPane.setColumnIndex(playerView, newCol);
 		GridPane.setRowIndex(playerView, newRow);
            });
            transition.play();
 	}
        
        public void plantBomb(Bomb bomb) {
            if (bomb == null) {
                System.out.println("No bomb");
                return;
            } 
            ImageView bombView = new ImageView(bombImage);
            bombView.setFitWidth(TILE_SIZE);
            bombView.setFitHeight(TILE_SIZE);
            grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
        }
        
        public void blastBomb(Bomb bomb, ArrayList<Coordinates> blastWave) {
            if (bomb == null) {
                System.out.println("No bomb");
                return;
            }
            ImageView blastView = new ImageView(blastImage);
            blastView.setFitWidth(TILE_SIZE);
            blastView.setFitHeight(TILE_SIZE);
            grid.add(blastView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
            for (Coordinates blast : blastWave) {
                grid.add(blastView, blast.getX(), blast.getY());
            }
        }

}
