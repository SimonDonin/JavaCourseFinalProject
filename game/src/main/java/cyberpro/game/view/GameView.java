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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.animation.AnimationTimer;

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
        
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    
    private long lastUpdate = 0; // Tracks the last update time
    private static final long MOVEMENT_DELAY = 100_000_000; // 200ms in nanoseconds

 
    private int gridWidth;
    private int gridHeight;
    // Shall be defined at model.


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
        grid.setOnKeyPressed(this::handleKeyPress);
        grid.setOnKeyReleased(this::handleKeyRelease);
        // grid.setOnKeyPressed(this::handleKeyPress); // Обработка нажатия клавиш
        
        AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (now - lastUpdate >= MOVEMENT_DELAY) {
                if (pressedKeys.contains(KeyCode.UP)) {
                    controller.playerMoveUp(controller.getPlayerIdByNumber(1));
                }
                if (pressedKeys.contains(KeyCode.DOWN)) {
                    controller.playerMoveDown(controller.getPlayerIdByNumber(1));
                }
                if (pressedKeys.contains(KeyCode.LEFT)) {
                    controller.playerMoveLeft(controller.getPlayerIdByNumber(1));
                }
                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    controller.playerMoveRight(controller.getPlayerIdByNumber(1));
                }
                if (pressedKeys.contains(KeyCode.W)) {
                    controller.playerMoveUp(controller.getPlayerIdByNumber(2));
                }
                if (pressedKeys.contains(KeyCode.S)) {
                    controller.playerMoveDown(controller.getPlayerIdByNumber(2));
                }
                if (pressedKeys.contains(KeyCode.A)) {
                    controller.playerMoveLeft(controller.getPlayerIdByNumber(2));
                }
                if (pressedKeys.contains(KeyCode.D)) {
                    controller.playerMoveRight(controller.getPlayerIdByNumber(2));
                }
                // Update the last update time
                lastUpdate = now;
            }
        }
        };
        timer.start();
        
	stage.setScene(scene);
	stage.setTitle("Bombermen");
	stage.show();
        
        stage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing...");
            System.exit(0); // <-- принудительно завершает процесс
        });
    

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

    }
        
        /*
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
		

		}

		if (playerNumber != 0) {
			System.out.println("Key pressed " + "by the Player " + controller.getPlayerIdByNumber(playerNumber) + " "
					+ event.getCode());
		}
	} */

    public void moveSprite(Coordinates oldCoord, Coordinates newCoord, Player player) {
        ImageView playerView = playerSprites.get(player.getId());
        if (playerView == null) return;
 
        int oldX = oldCoord.getX();
        int oldY = oldCoord.getY();
 
        int newX = newCoord.getX();
        int newY = newCoord.getY();
        
        // Проверка валидности перехода
        if (Math.abs(newX - oldX) > 1 || Math.abs(newY - oldY) > 1) {
            System.err.printf("Unexpected move: from (%d, %d) to (%d, %d)%n", oldX, oldY, newX, newY);
        return;
        }
        
        // Movement log
        System.out.printf("Moving player %s from (%d, %d) to (%d, %d)%n", player.getId(), oldX, oldY, newX, newY);

        
        int deltaX = (newX - oldX) * TILE_SIZE;
        int deltaY = (newY - oldY) * TILE_SIZE;
 
        TranslateTransition transition = new TranslateTransition(Duration.millis(150), playerView);
        transition.setByX(deltaX);
        transition.setByY(deltaY);
        transition.setOnFinished(e -> {
            playerView.setTranslateX(0);
            playerView.setTranslateY(0);
            // Now player position is perfectly in the cell
            grid.getChildren().remove(playerView); // More safe than just add new
            grid.add(playerView, newX, newY);
            // Actually set player at new position
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
    
    private void handleKeyPress(KeyEvent event) {
        pressedKeys.add(event.getCode());
    }

    private void handleKeyRelease(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }
    
    
}
}
>>>>>>> Stashed changes
