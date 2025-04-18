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
import javafx.application.Platform;
// Animation libs
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
// End animation libs
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.*;
import javafx.animation.AnimationTimer;

// Package import
import cyberpro.game.controller.ControllerInterface;
import cyberpro.game.controller.GameController;
import cyberpro.game.model.*;
import cyberpro.game.view.GameView;
import cyberpro.game.controller.ModifierType;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.scene.Node;

/**
 *
 * @author mikhail
 */
public class GameView {
    
    Logger logger = Logger.getLogger(GameView.class.getName());
    // Create logger using core Java API
    
    private ControllerInterface controller;
    private final int TILE_SIZE = 40;
    // This is actually size of a sprite. It will depend on actual size of game board
    private GridPane grid;
    // Create GridPane object to display all graphical objects.
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
    private final Image modPlusRange = new Image(getClass().getResourceAsStream("modPlusRange.png"));
    private final Image modReverseCotrols = new Image(getClass().getResourceAsStream("modReverseControls.png"));
    // End load sprites
    // Begin load blast sprites
    // Image[][] blastImage = new Image[5][5];
    private Image blastCenter, blastLeftTip, blastRightTip, blastBottomTip, blastTopTip;
    private Image blastLeftRay, blastRightRay, blastTopRay, blastBottomRay;
    

    // Begin declare map lists for player, modifier and bomb sprites
    private Map<String, ImageView> playerSprites = new HashMap<>();
    private Map<String, ImageView> bombSprites = new HashMap<>();
    private Map<String, ImageView> deadPlayerSprites = new HashMap<>();
    private Map<String, ImageView> modifierSprites = new HashMap<>();
    // End declare map lists for player and bomb sprites
    
    private Map<String, Boolean> playerOnMove = new HashMap<>();
    private Map<String, Integer> playerSpeed = new HashMap<>();

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private long lastUpdate = 0; // Tracks the last update time
    private static final long MOVEMENT_DELAY = 50_000_000; // 50ms in nanoseconds

    private int gridWidth;
    private int gridHeight;
    // Shall be defined at model.
    
    private int playerDefaultSpeed = 150;

    public GameView(Stage stage, ControllerInterface controller) {
        logger.setLevel(Level.FINE);
        // Set Fine logging level for beta-testing. 
        // [TODO] Rise logging level to Level.WARRINGS for a final package
        this.controller = controller; // Get Controller interface to call its methods
        this.gameBoard = controller.getBoard().getCells(); // Get a link to the gameboard cells

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
                    if ( ( pressedKeys.contains(KeyCode.O) ) && (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
                        controller.playerRemoteBombExplode(controller.getPlayerIdByNumber(1));
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
                        // GameOverWindow gameOver = new GameOverWindow("Game over");
                        // Platform.runLater(() -> new GameOverWindow("You lose!"));
                        gameOver("Game over");
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
            System.exit(0); // <-- Close the app with all threads
        });

        grid.requestFocus(); // Request focus to catch keypresses
        
        // Create a specific sprite for each player
        players = controller.getPlayers();
        int counter = 1;
        Image playerImage;
        ImageView playerView;
        ImageView deadPlayerView;
        for (Player player : players) {
            String playerID = player.getId();
            String sprite = "character" + counter + ".png";
            // System.out.println(sprite);
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
            playerSpeed.put(playerID, playerDefaultSpeed);
            // Set default speed for each player in ms
        }
        // All player's sprites are in playerSprites list
        
        // Create blast sprites
        try {
            blastCenter = loadImage("blast/blastCenter.png");
            blastTopTip = loadImage("blast/blastTopTip.png");
            blastBottomTip = loadImage("blast/blastBottomTip.png");
            blastBottomRay = loadImage("blast/blastBottomRay.png");
            blastLeftTip = loadImage("blast/blastLeftTip.png");
            blastLeftRay = loadImage("blast/blastLeftRay.png");
            blastRightTip = loadImage("blast/blastRightTip.png");
            blastRightRay = loadImage("blast/blastRightRay.png");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Can't load blast sprites. Check folder 'blast' inside a view folder");
            e.printStackTrace(); // or show an alert, log to file, etc.
        }
    }
    
    private Image loadImage(String path) throws FileNotFoundException {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException("Image file not found: " + path);
        }
        return new Image(stream);
    }

    public void getBoard(TileType[][] board) {
        gameBoard = board;
    }

    public void drawGrid(ArrayList<Player> players, ArrayList<Bomb> bombs, ArrayList<Modifier> modifiers) {
        grid.getChildren().clear();
        // We need to clear all grid. GridPane do not allow duplicated nodes
        for (int row = 0; row < gridWidth; row++) {
            for (int col = 0; col < gridHeight; col++) {
                ImageView tileView = new ImageView();
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                try {
                    switch (gameBoard[row][col]) {
                        case FLOOR ->
                            tileView.setImage(floorImage);
                        case BRICK_WALL ->
                            tileView.setImage(brickWallImage);
                        case CONCRETE_WALL ->
                            tileView.setImage(concreteWallImage);
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.log(Level.SEVERE, "Invalid index at row {0}, col {1}", new Object[]{row, col});
                    logger.log(Level.SEVERE, "Check gridWidth, gridHeight parameters");
                    e.printStackTrace(); // Optional: log the error
                }
                
                grid.add(tileView, row, col);
            }
        }
        
        // Begin put players on the grid
        for (Player player : players) {
            String playerID = player.getId();
            ImageView playerView;
            playerView = player.isAlive() ? playerSprites.get(playerID) : deadPlayerSprites.get(playerID);
            grid.add(playerView, player.getCoordinates().getX(), player.getCoordinates().getY());
        }
        // End. All players are on map

        // Begin put bombs on the grid
        for (Bomb bomb : bombs) {
            ImageView bombView = new ImageView(bombImage);
            bombView.setFitWidth(TILE_SIZE);
            bombView.setFitHeight(TILE_SIZE);
            grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
        }
        // All bombs are on map
        
        // Begin put modifiers on the grid
        for (Modifier mod : modifiers) {
            plantMod(mod);
        }
        // All modifiers are on grid
    }


    public void moveSprite(Coordinates oldCoord, Coordinates newCoord, Player player) {
        ImageView playerView = playerSprites.get(player.getId());
        int movementSpeed = 150; // Speed of movement im ms
        
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
        
        int deltaX = (newX - oldX) * TILE_SIZE;
        int deltaY = (newY - oldY) * TILE_SIZE;
        
        movementSpeed = movementSpeed*100 / player.getSpeed();
        // Greater speed --> smaller transition delay 
        logger.log(Level.INFO, "Movement delay is {1} ms", movementSpeed);
        
        TranslateTransition transition = new TranslateTransition(Duration.millis(movementSpeed), playerView);
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
            logger.log(Level.WARNING, "No bomb");
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
            logger.log(Level.WARNING, "No bomb");
            return;
        }

        int centerX = bomb.getCoordinates().getX();
        int centerY = bomb.getCoordinates().getY();
        int minX = centerX;
        int maxX = centerX;
        int minY = centerY;
        int maxY = centerY;
        
        int leftRay, rightRay, topRay, bottomRay;
        
        for (Coordinates c : blastWave) {
            if (c.getX() < minX) minX = c.getX();
            if (c.getX() > maxX) maxX = c.getX();
            if (c.getY() < minY) minY = c.getY();
            if (c.getY() > maxY) maxY = c.getY();
        }
        // Now we have min/max coordinates for blast wave
        leftRay = centerX - minX;
        rightRay = maxX - centerX;
        topRay = maxY - centerY;
        bottomRay = centerY - minY;
        // Now we have a ray length for every direction
        for (Coordinates blast : blastWave) {
            grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == blast.getX() &&
                GridPane.getRowIndex(node) == blast.getY()
            );
        }
        // Clean all tiles under the blast wave to avoid nested node error
        drawBlast(blastCenter, centerX, centerY);
        if (minX < centerX) { drawBlast(blastLeftTip, minX, centerY); }
        if (maxX > centerX) { drawBlast(blastRightTip, maxX, centerY); }
        if (minY < centerY) { drawBlast(blastTopTip, centerX, minY); }
        if (maxY > centerY) { drawBlast(blastBottomTip, centerX, maxY); }
        // Do not draw ray if it is close to a wall

        if ((centerX - minX) > 1 ) {
        for (int x = (centerX-1); x > minX; x--) {
            drawBlast(blastLeftRay, x, centerY);
        } }
        if ((maxX - centerX) > 1) {
        for (int x = (centerX+1); x < maxX; x++) {
            drawBlast(blastRightRay, x, centerY);
        } }
        if ((centerY - minY) > 1) {
        for (int y = (centerY-1); y > minY; y--) {
            drawBlast(blastBottomRay, centerX, y);
        } }
        if ((maxY - centerY) > 1) {
        for (int y = (centerY+1); y < maxY; y++) {
            drawBlast(blastBottomRay, centerX, y);
        } }
        // Do not draw rays if ray length is < 2 
        // (so ray have only a tip or no ray)
    }
        
    private void drawBlast(Image img, int x, int y) {
        ImageView blastView;
        blastView = new ImageView(img);
        blastView.setFitWidth(TILE_SIZE);
        blastView.setFitHeight(TILE_SIZE);
        grid.add(blastView, x, y);
    }
    
    public void plantMod(Modifier mod) {
        Coordinates modCoord = mod.getCoordinates();
        grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == modCoord.getX() &&
                GridPane.getRowIndex(node) == modCoord.getY()
            );
        // Remove previous tile under modifer
        ModifierType type = mod.getType();
        ImageView modView = new ImageView();
        modView.setFitWidth(TILE_SIZE);
        modView.setFitHeight(TILE_SIZE);
        switch (type) {
            case ModifierType.REMOTE_EXPLOSION ->
                modView.setImage(modRemoteExplosion);
            case ModifierType.PLUS_BOMB ->
                modView.setImage(modPlusBomb);
            case ModifierType.SPEED_UP ->
                modView.setImage(modSpeedUp);
            case ModifierType.PLUS_RANGE ->
                modView.setImage(modPlusRange);
            case ModifierType.REVERSE_CONTROLS ->
                modView.setImage(modReverseCotrols);
            }
        modifierSprites.put(mod.getId(), modView);
        // We add new modifier to a list, so we know what to remove after
        grid.add(modView, modCoord.getX(), modCoord.getY());
    }
    
    public void removeMod(Modifier mod) {
        // Place a code to remove modifier if it was taken
        ImageView modView = modifierSprites.get(mod.getId());
        grid.getChildren().remove(modView);
        // We just remove a sprite from a game board. No additional actions is required
        ImageView floorView = new ImageView(floorImage);
        floorView.setFitHeight(TILE_SIZE);
        floorView.setFitWidth(TILE_SIZE);
        grid.add(floorView, mod.getCoordinates().getX(), mod.getCoordinates().getY());
    }

    private void handleKeyPress(KeyEvent event) {
        pressedKeys.add(event.getCode());
    }

    private void handleKeyRelease(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }
    
    public void killPlayer(Player player) {
        logger.log(Level.INFO, "Player with id {0} is killed!", player.getId());
        ImageView playerView = playerSprites.get(player.getId());
        grid.getChildren().remove(playerView);
        // Remove player sprite from grid
        ImageView deadPlayerView = deadPlayerSprites.get(player.getId());
        grid.add(deadPlayerView, player.getCoordinates().getX(), player.getCoordinates().getY());
    }
    
    public void gameOver(String msg) {
        Platform.runLater(() -> new GameOverWindow(msg));
    }

}
