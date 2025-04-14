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
import javafx.stage.Stage;
// Animation libs
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
// End animation libs
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.animation.AnimationTimer;

// Package import
import cyberpro.game.controller.ControllerInterface;
import cyberpro.game.controller.GameController;
import cyberpro.game.model.*;
import cyberpro.game.view.GameView;
import cyberpro.game.controller.ModifierType;

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
    
    // Begin load sprites
    private final Image playerOneImage = new Image(getClass().getResourceAsStream("Character1.png"));
    private final Image brickWallImage = new Image(getClass().getResourceAsStream("BrickWall.png"));
    private final Image concreteWallImage = new Image(getClass().getResourceAsStream("ConcreteWall.png"));
    private final Image floorImage = new Image(getClass().getResourceAsStream("Floor.png"));
    private final Image bombImage = new Image(getClass().getResourceAsStream("Bomb.png"));
    private final Image enemyImage = new Image(getClass().getResourceAsStream("Enemy.png"));
    // Modifier sprites
    private final Image modRemoteExplosion = new Image(getClass().getResourceAsStream("modRemoteExplosion.png"));
    private final Image modPlusBomb = new Image(getClass().getResourceAsStream("modPlusBomb.png"));
    private final Image modSpeedUp = new Image(getClass().getResourceAsStream("modSpeedUp.png"));
    // End load sprites
    // Begin load blast sprites
    Image[][] blastImage = new Image[5][5];
    

    // Begin declare map lists for player and bomb sprites
    private Map<String, ImageView> playerSprites = new HashMap<>();
    private Map<String, ImageView> bombSprites = new HashMap<>();
    private Map<String, ImageView> deadPlayerSprites = new HashMap<>();
    // End declare map lists for player and bomb sprites
    
    private Map<String, Boolean> playerOnMove = new HashMap<>();

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
        ArrayList<Player> players;

        int sceneSize = TILE_SIZE * gridWidth; // или gridHeight, если нужна квадратная область
        Scene scene = new Scene(grid, sceneSize, sceneSize);
        grid.setOnKeyPressed(this::handleKeyPress);
        grid.setOnKeyReleased(this::handleKeyRelease);
        // grid.setOnKeyPressed(this::handleKeyPress); // Обработка нажатия клавиш

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= MOVEMENT_DELAY) {
                    if ( (pressedKeys.contains(KeyCode.UP) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
                        controller.playerMoveUp(controller.getPlayerIdByNumber(1));
                    }
                    if ( (pressedKeys.contains(KeyCode.DOWN) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE)))  {
                        controller.playerMoveDown(controller.getPlayerIdByNumber(1));
                    }
                    if ( (pressedKeys.contains(KeyCode.LEFT) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
                        controller.playerMoveLeft(controller.getPlayerIdByNumber(1));
                    }
                    if ( ( pressedKeys.contains(KeyCode.RIGHT) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
                        controller.playerMoveRight(controller.getPlayerIdByNumber(1));
                    }
                    if ( ( pressedKeys.contains(KeyCode.SPACE) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
                        controller.playerPlantBomb(controller.getPlayerIdByNumber(1));
                    }
                    if ( (pressedKeys.contains(KeyCode.W) ) &&  (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
                        controller.playerMoveUp(controller.getPlayerIdByNumber(2));
                    }
                    if ( (pressedKeys.contains(KeyCode.S) ) &&  (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
                        controller.playerMoveDown(controller.getPlayerIdByNumber(2));
                    }
                    if ( (pressedKeys.contains(KeyCode.A) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
                        controller.playerMoveLeft(controller.getPlayerIdByNumber(2));
                    }
                    if ( (pressedKeys.contains(KeyCode.D) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
                        controller.playerMoveRight(controller.getPlayerIdByNumber(2));
                    }
                    if (( pressedKeys.contains(KeyCode.G) ) &&  (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
                        controller.playerPlantBomb(controller.getPlayerIdByNumber(2));
                    }
                    if (pressedKeys.contains(KeyCode.Q)) {
                        // this.killPlayer(controller.getPlayers().getFirst());
                        Player player = controller.getPlayers().getFirst();
                        killPlayer(player);
                    }
                    if (pressedKeys.contains(KeyCode.M)) {
                        Coordinates modCoord = new Coordinates(10, 10);
                        Modifier mod = new Modifier(modCoord, ModifierType.SPEED_UP, 10);
                        plantMod(mod);
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
        
        // Create a specific sprite for each player
        players = controller.getPlayers();
        int counter = 1;
        Image playerImage;
        ImageView playerView;
        ImageView deadPlayerView;
        for (Player player : players) {
            String playerID = player.getId();
            String sprite = "character" + counter + ".png";
            System.out.println(sprite);
            playerImage = new Image(getClass().getResourceAsStream(sprite));
            playerView = new ImageView(playerImage);
            playerView.setFitWidth(TILE_SIZE);
            playerView.setFitHeight(TILE_SIZE);
            playerSprites.put(playerID, playerView);
            sprite = "dead" + counter + ".png";
            playerImage = new Image(getClass().getResourceAsStream(sprite));
            deadPlayerView = new ImageView(playerImage);
            deadPlayerView.setFitWidth(TILE_SIZE);
            deadPlayerView.setFitHeight(TILE_SIZE);
            deadPlayerSprites.put(playerID, deadPlayerView);
            playerOnMove.put(controller.getPlayerIdByNumber(counter), Boolean.FALSE);
            counter++;
        }
        // All player's sprites are in playerSprites list
        
        // Create blast sprites
        blastImage[0][2] = new Image(getClass().getResourceAsStream("blast/blast0,2.png"));
        blastImage[1][2] = new Image(getClass().getResourceAsStream("blast/blast1,2.png"));
        blastImage[2][2] = new Image(getClass().getResourceAsStream("blast/blast2,2.png"));
        blastImage[3][2] = new Image(getClass().getResourceAsStream("blast/blast3,2.png"));
        blastImage[4][2] = new Image(getClass().getResourceAsStream("blast/blast4,2.png"));
        blastImage[2][0] = new Image(getClass().getResourceAsStream("blast/blast2,0.png"));
        blastImage[2][1] = new Image(getClass().getResourceAsStream("blast/blast2,1.png"));
        blastImage[2][3] = new Image(getClass().getResourceAsStream("blast/blast2,3.png"));
        blastImage[2][4] = new Image(getClass().getResourceAsStream("blast/blast2,4.png"));
        
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
                    case FLOOR ->
                        tileView.setImage(floorImage);
                    case BRICK_WALL ->
                        tileView.setImage(brickWallImage);
                    case CONCRETE_WALL ->
                        tileView.setImage(concreteWallImage);
                }
                grid.add(tileView, row, col);
            }
        }
        
        // Begin put players on the grid
        for (Player player : players) {
            String playerID = player.getId();
            ImageView playerView = playerSprites.get(playerID);
            grid.add(playerView, player.getCoordinates().getX(), player.getCoordinates().getY());
        }
        // End. All players are on map

        // Begin put bombs on the grid
        for (Bomb bomb : bombs) {
            ImageView bombView = new ImageView(bombImage);
            bombView.setFitWidth(TILE_SIZE);
            bombView.setFitHeight(TILE_SIZE);
            grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY()); // it
        }
        // All bombs are on map
    }


    public void moveSprite(Coordinates oldCoord, Coordinates newCoord, Player player) {
        ImageView playerView = playerSprites.get(player.getId());
        if (playerView == null) {
            return;
        }

        int oldX = oldCoord.getX();
        int oldY = oldCoord.getY();

        int newX = newCoord.getX();
        int newY = newCoord.getY();

        // Check, if it is valid movement
        if (Math.abs(newX - oldX) > 1 || Math.abs(newY - oldY) > 1) {
            System.err.printf("Unexpected move: from (%d, %d) to (%d, %d)%n", oldX, oldY, newX, newY);
            return;
        }
        playerOnMove.replace(player.getId(), Boolean.TRUE);
        // Movement log for debug
        // System.out.printf("Moving player %s from (%d, %d) to (%d, %d)%n", player.getId(), oldX, oldY, newX, newY);

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
            playerOnMove.replace(player.getId(), Boolean.FALSE);
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
        bombSprites.put(bomb.getId(), bombView);
        grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
    }

    public void blastBomb(Bomb bomb, ArrayList<Coordinates> blastWave) {
        if (bomb == null) {
            System.out.println("No bomb");
            return;
        }
        ImageView bombView = bombSprites.get(bomb.getId());
        grid.getChildren().remove(bombView);
        bombSprites.remove(bomb.getId());
        // Find a specific bomb, remove it from the screen and from the list
        ImageView blastView = new ImageView(blastImage[2][2]);
        blastView.setFitWidth(TILE_SIZE);
        blastView.setFitHeight(TILE_SIZE);
        grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == bomb.getCoordinates().getX() &&
                GridPane.getRowIndex(node) == bomb.getCoordinates().getY()
            );
        grid.add(blastView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
        for (Coordinates blast : blastWave) {
            grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == blast.getX() &&
                GridPane.getRowIndex(node) == blast.getY()
            );
            int blastX = blast.getX()-bomb.getCoordinates().getX()+2;
            int blastY = blast.getY()-bomb.getCoordinates().getY()+2;
            //System.out.println(blastX +", " + blastY);
            blastView = new ImageView(blastImage[blastX][blastY]);
            blastView.setFitWidth(TILE_SIZE);
            blastView.setFitHeight(TILE_SIZE);
            grid.add(blastView, blast.getX(), blast.getY());
        }

    }
    
    public void plantMod(Modifier mod) {
        Coordinates modCoord = mod.getCoordinates();
        grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == modCoord.getX() &&
                GridPane.getRowIndex(node) == modCoord.getY()
            );
        // Remove previous mode under modifer
        ModifierType type = mod.getType();
        ImageView tileView = new ImageView();
        tileView.setFitWidth(TILE_SIZE);
        tileView.setFitHeight(TILE_SIZE);
        switch (type) {
            case ModifierType.REMOTE_EXPLOSION ->
                tileView.setImage(modRemoteExplosion);
            case ModifierType.PLUS_BOMB ->
                tileView.setImage(modPlusBomb);
            case ModifierType.SPEED_UP ->
                tileView.setImage(modSpeedUp);
            }
        grid.add(tileView, modCoord.getY(), modCoord.getX());
    }

    private void handleKeyPress(KeyEvent event) {
        pressedKeys.add(event.getCode());
    }

    private void handleKeyRelease(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }
    
    public void killPlayer(Player player) {
        System.out.println("Player with id " + player.getId() + " is killed!");
        ImageView playerView = playerSprites.get(player.getId());
        grid.getChildren().remove(playerView);
        // Remove player sprite from grid
        ImageView deadPlayerView = deadPlayerSprites.get(player.getId());
        grid.add(deadPlayerView, player.getCoordinates().getX(), player.getCoordinates().getY());
    }
}
