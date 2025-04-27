package cyberpro.game.view;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
// Do not shure if it possible to use here. Or juct controller will use it.
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
// Animation libs
import javafx.animation.TranslateTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
// End animation libs
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.*;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;

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

	final Logger logger; // Create logger using core Java API
	// Create logger using core Java API

	private ControllerInterface controller;
	private final int TILE_SIZE = 40;
	// This is actually size of a sprite. It will depend on actual size of game
	// board
	private GridPane grid;
	// Create GridPane object to display all graphical objects.
        private Pane blastPane = new Pane();
        // blastPane is only for a blast
	private TileType[][] gameBoard;
	// Game board array. We have to recive it from a Controller
	private boolean isPaused;
        

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
	private Image blastCenter, blastLeftTip, blastRightTip, blastBottomTip, blastTopTip;
	private Image blastLeftRay, blastRightRay, blastTopRay, blastBottomRay;

	// Begin load sound files
	private static AudioClip BLAST_BOMB_SOUND;
	private static AudioClip PLANT_BOMB_SOUND;

	// Begin declare map lists for player, modifier and bomb sprites
	private Map<String, ImageView> playerSprites = new HashMap<>();
	private Map<String, ImageView> bombSprites = new HashMap<>();
	private Map<String, ImageView> deadPlayerSprites = new HashMap<>();
	private Map<String, ImageView> modifierSprites = new HashMap<>();
        private Map<String, CopyOnWriteArrayList<ImageView>> blastSprites = new ConcurrentHashMap<>();
	// End declare map lists for player and bomb sprites

	// Declare a mediaplayer for background music
	private MediaPlayer mediaPlayer;

	private Map<String, Boolean> playerOnMove = new HashMap<>();

	private final Set<KeyCode> pressedKeys = new HashSet<>();

	// Tracks the last update time for keyboard request of each player
	private long lastUpdateP1 = 0;
	private long lastUpdateP2 = 0;
	private final long MOVEMENT_DELAY = 50_000_000; // 50ms in nanoseconds

	// Shall be defined at model
	private int gridWidth;
	private int gridHeight;

	private int playerDefaultSpeed = 150;

	private Stage stage; // Make accessible externally

	public GameView(Stage stage, ControllerInterface controller) {
		this.logger = Logger.getLogger(GameView.class.getName());
		this.stage = stage;
		ArrayList<Player> players;

		logger.setLevel(Level.ALL);
		// Set Fine logging level for beta-testing.
		// [TODO] Rise logging level to Level.WARRINGS for a final package
		this.controller = controller; // Get Controller interface to call its methods
		this.gameBoard = controller.getBoard().getCells(); // Get a link to the gameboard cells

		grid = new GridPane();
                StackPane root = new StackPane();
                root.getChildren().addAll(grid, blastPane);
		grid.setFocusTraversable(true); // Get keyboard input focus into a grid control

		// Removing spacing and padding
		grid.setPadding(new Insets(0));
		grid.setHgap(0);
		grid.setVgap(0);

		gridWidth = gridHeight = controller.getBoard().getSize();
		logger.log(Level.INFO, "Board size is {0}", gridWidth);

		int sceneSize = TILE_SIZE * gridWidth; // или gridHeight, если нужна квадратная область
		Scene scene = new Scene(root, sceneSize, sceneSize);
		// Alternative keyboard input interface
		grid.setOnKeyPressed(this::handleKeyPress);
		grid.setOnKeyReleased(this::handleKeyRelease);

		AnimationTimer timerP1 = new AnimationTimer() {
			@Override
			public void handle(long now) {

				int playerSpeed = controller.getPlayers().getFirst().getSpeed();
				long dynamicDelay = switch (playerSpeed) {
				case 100 -> MOVEMENT_DELAY;
				case 101 -> 25_000_000;
				default -> MOVEMENT_DELAY;
				};

				if (now - lastUpdateP1 >= dynamicDelay) {
					if ((pressedKeys.contains(KeyCode.UP))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerMoveUp(controller.getPlayerIdByNumber(1));
					}
					if ((pressedKeys.contains(KeyCode.DOWN))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerMoveDown(controller.getPlayerIdByNumber(1));
					}
					if ((pressedKeys.contains(KeyCode.LEFT))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerMoveLeft(controller.getPlayerIdByNumber(1));
					}
					if ((pressedKeys.contains(KeyCode.RIGHT))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerMoveRight(controller.getPlayerIdByNumber(1));
					}
					if ((pressedKeys.contains(KeyCode.NUMPAD2))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerPlantBomb(controller.getPlayerIdByNumber(1));
					}
					if ((pressedKeys.contains(KeyCode.NUMPAD3))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerRemoteBombExplode(controller.getPlayerIdByNumber(1));
					}
					if (pressedKeys.contains(KeyCode.P)) {
						if (isPaused = false) {
							controller.pauseOn();
							isPaused = true;
						} else {
							controller.pauseOff();
							isPaused = false;
						}
						// Make a 100 ms pause to opress false detection of keypresses
						PauseTransition pause = new PauseTransition(Duration.millis(100));

						pause.setOnFinished(event -> {
							logger.log(Level.INFO, "Game paused/unpaused");
						});

						pause.play(); // Start pause timer
					}
					// Update the last update time
					lastUpdateP1 = now;
				}
			}
		};
		timerP1.start();

		AnimationTimer timerP2 = new AnimationTimer() {
			@Override
			public void handle(long now) {
				int playerSpeed = controller.getPlayers().getLast().getSpeed();

				long dynamicDelay = switch (playerSpeed) {
				case 100 -> MOVEMENT_DELAY;
				case 101 -> 25_000_000;
				default -> MOVEMENT_DELAY;
				};

				if (now - lastUpdateP2 >= dynamicDelay) {
					if ((pressedKeys.contains(KeyCode.W))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
						controller.playerMoveUp(controller.getPlayerIdByNumber(2));
					}
					if ((pressedKeys.contains(KeyCode.S))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
						controller.playerMoveDown(controller.getPlayerIdByNumber(2));
					}
					if ((pressedKeys.contains(KeyCode.A))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
						controller.playerMoveLeft(controller.getPlayerIdByNumber(2));
					}
					if ((pressedKeys.contains(KeyCode.D))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
						controller.playerMoveRight(controller.getPlayerIdByNumber(2));
					}
					if ((pressedKeys.contains(KeyCode.G))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(2)), Boolean.FALSE))) {
						controller.playerPlantBomb(controller.getPlayerIdByNumber(2));
					}
					if ((pressedKeys.contains(KeyCode.H))
							&& (Objects.equals(playerOnMove.get(controller.getPlayerIdByNumber(1)), Boolean.FALSE))) {
						controller.playerRemoteBombExplode(controller.getPlayerIdByNumber(2));
					}
                                        
					// Update the last update time
					lastUpdateP2 = now;
				}
			}
		};
		timerP2.start();

		stage.setScene(scene);
		stage.setTitle("Bombermen");
		stage.show();

		stage.setOnCloseRequest(event -> {
			logger.log(Level.INFO, "Stage is closing...");
			System.exit(0); // Close the app with all threads
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
			logger.log(Level.SEVERE,
					"Can't load blast sprites. Check folder 'blast' inside a view folder for all files listed above. Game is still playable, but it's not a fun to play without nice sprites.");
		}

		try {
			BLAST_BOMB_SOUND = new AudioClip(GameView.class.getResource("music/sndBlastBomb.wav").toExternalForm());
			PLANT_BOMB_SOUND = new AudioClip(GameView.class.getResource("music/sndPlantBomb.mp3").toExternalForm());
		} catch (NullPointerException e) {
			logger.log(Level.SEVERE,
					"Can't load sounds. Check folder 'music' inside a view folder. Game is still playable, but without sounds.");
		}

		playBackgroundMusic();
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
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

	public void drawGrid(ArrayList<Player> players, CopyOnWriteArrayList<Bomb> bombs, ArrayList<Modifier> modifiers, CopyOnWriteArrayList<Bomb> blast) {
		grid.getChildren().clear();
		// We need to clear all grid. GridPane do not allow duplicated nodes
		for (int row = 0; row < gridWidth; row++) {
			for (int col = 0; col < gridHeight; col++) {
				ImageView tileView = new ImageView();
				tileView.setFitWidth(TILE_SIZE);
				tileView.setFitHeight(TILE_SIZE);
				try {
					switch (gameBoard[row][col]) {
					case FLOOR -> tileView.setImage(floorImage);
					case BRICK_WALL -> tileView.setImage(brickWallImage);
					case CONCRETE_WALL -> tileView.setImage(concreteWallImage);
					}
				} catch (IndexOutOfBoundsException e) {
					logger.log(Level.SEVERE, "Invalid index at row {0}, col {1}", new Object[] { row, col });
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
		int movementSpeed; // Speed of movement im ms

		if (playerView == null) {
			return;
		}

		int oldX = oldCoord.getX();
		int oldY = oldCoord.getY();

		int newX = newCoord.getX();
		int newY = newCoord.getY();

		// Check, if it is valid movement
		if (Math.abs(newX - oldX) > 1 || Math.abs(newY - oldY) > 1) {
			logger.log(Level.WARNING, "Unexpected move: from ({0}, {1}) to ({2}, {3})",
					new Object[] { oldX, oldY, newX, newY });
			return;
		}
		playerOnMove.replace(player.getId(), Boolean.TRUE);

		int deltaX = (newX - oldX) * TILE_SIZE;
		int deltaY = (newY - oldY) * TILE_SIZE;

		logger.log(Level.FINE, "Player spped {0}", player.getSpeed());
		movementSpeed = switch (player.getSpeed()) {
		case 100 -> 150;
		case 101 -> 100;
		default -> 150;
		};
		// Greater speed --> smaller transition delay
		// logger.log(Level.INFO, "Movement delay is {0} ms", movementSpeed);

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
        // Play a sound before plant a bomb on the board
        PLANT_BOMB_SOUND.setVolume(0.6);
        PLANT_BOMB_SOUND.play();
        ImageView bombView = new ImageView(bombImage);
        bombView.setFitWidth(TILE_SIZE);
        bombView.setFitHeight(TILE_SIZE);
        bombSprites.put(bomb.getId(), bombView);
        grid.add(bombView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
    }

    public void blastBomb(Bomb bomb, CopyOnWriteArrayList<Coordinates> blastWave) {
        CopyOnWriteArrayList<ImageView> blastCloud = new CopyOnWriteArrayList<>();

        if (bomb == null) {
            logger.log(Level.WARNING, "No bomb");
            return;
        }

        // Play blast sound before update the view
        BLAST_BOMB_SOUND.setVolume(0.8);
        BLAST_BOMB_SOUND.play();

        int centerX = bomb.getCoordinates().getX();
        int centerY = bomb.getCoordinates().getY();
        int minX = centerX;
        int maxX = centerX;
        int minY = centerY;
        int maxY = centerY;

        for (Coordinates c : blastWave) {
            if (c.getX() < minX) {
                minX = c.getX();
            }
            if (c.getX() > maxX) {
                maxX = c.getX();
            }
            if (c.getY() < minY) {
                minY = c.getY();
            }
            if (c.getY() > maxY) {
                maxY = c.getY();
            }
        }
        // Now we have min/max coordinates for blast wave

        // Now we have a ray length for every direction
        drawBlast(blastCenter, centerX, centerY, blastCloud);
        if (minX < centerX) {
            drawBlast(blastLeftTip, minX, centerY, blastCloud);
        }
        if (maxX > centerX) {
            drawBlast(blastRightTip, maxX, centerY, blastCloud);
        }
        if (minY < centerY) {
            drawBlast(blastTopTip, centerX, minY, blastCloud);
        }
        if (maxY > centerY) {
            drawBlast(blastBottomTip, centerX, maxY, blastCloud);
        }
        // Do not draw ray if it is close to a wall

        if ((centerX - minX) > 1) {
            for (int x = (centerX - 1); x > minX; x--) {
                drawBlast(blastLeftRay, x, centerY, blastCloud);
            }
        }
        if ((maxX - centerX) > 1) {
            for (int x = (centerX + 1); x < maxX; x++) {
                drawBlast(blastRightRay, x, centerY, blastCloud);
            }
        }
        if ((centerY - minY) > 1) {
            for (int y = (centerY - 1); y > minY; y--) {
                drawBlast(blastBottomRay, centerX, y, blastCloud);
            }
        }
        if ((maxY - centerY) > 1) {
            for (int y = (centerY + 1); y < maxY; y++) {
                drawBlast(blastBottomRay, centerX, y, blastCloud);
            }
        }
        // Do not draw rays if ray length is < 2
        // (so ray have only a tip or no ray)
        blastSprites.put(bomb.getId(), blastCloud);
        logger.log(Level.INFO, "Blast cloud with ID {0} is saved for future removal.", bomb.getId());
        // All tiles of blast are keept at blastSprites to facilitate future removal
        // blastCloud is local. It will be re-inicialized during next run

    }
    
    public void removeBlast(Bomb bomb) {
        CopyOnWriteArrayList<ImageView> blastCloud = blastSprites.get(bomb.getId());
        if (blastCloud == null) {
            logger.log(Level.WARNING, "No blast found for bomb ID: " + bomb.getId());
            return;
        }
        blastPane.getChildren().removeAll(blastCloud);
        System.out.println("Blast colud with bomb ID " + bomb.getId() + " are restored for removal.");
        logger.log(Level.INFO, "Removing {0} blast sprites", blastCloud.size());
        for (ImageView blastElement : blastCloud) {
            // grid.getChildren().remove(blastElement);
            blastPane.getChildren().remove(blastElement);
            if (grid.getChildren().contains(blastElement)) {
                logger.log(Level.FINE, "blastElement IS inside grid before removal");
            } else {
                logger.log(Level.FINE, "blastElement NOT FOUND inside grid before removal");
            }
        }
        blastPane.getChildren().removeIf(node -> blastCloud.contains(node));
        logger.log(Level.INFO, "Bomb is removed from screen sprites");
        blastPane.requestLayout();
        blastSprites.remove(bomb.getId());
        // Remove all blast information from a set after it was removed from screen
    }

    private void drawBlast(Image img, int x, int y, CopyOnWriteArrayList<ImageView> blastCloud) {
        ImageView blastView;
        blastView = new ImageView(img);
        blastView.setFitWidth(TILE_SIZE);
        blastView.setFitHeight(TILE_SIZE);
        // blastCloud.add(blastView);
        // Add new element of blast to an array of all blast tiles
        blastView.setLayoutX(x * TILE_SIZE);
        blastView.setLayoutY(y * TILE_SIZE);
        // Put coordinates in pixels
        blastPane.getChildren().add(blastView);
        blastCloud.add(blastView);
        // We add each blast sprite to a ArrayList to use it after to remove these elements from the board
    }

    public void plantMod(Modifier mod) {
        Coordinates modCoord = mod.getCoordinates();
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
        modifierSprites.remove(mod.getId());
        // Remove unused mod from a Set (garbage collector do not remove unused objects from inside collections)
        grid.getChildren().remove(modView);
        // We just remove a mod sprite from a game board.
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
		controller.gameOverComplete();
	}

	public void playBackgroundMusic() {
		try {
			URL resource = getClass().getResource("music/gamemusic1.mp3"); // Path inside resources
			Media media = new Media(resource.toExternalForm());
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
			mediaPlayer.setVolume(0.5); // Set volume 0.0 - 1.0
			mediaPlayer.play();
			logger.log(Level.INFO, "Background music is playing.");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not play music: {0}", e.getMessage());
		}
	}

	public void stopBackgroundMusic() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			logger.log(Level.INFO, "Background music is stopped.");
		}
	}

	public void pauseBackgroundMusic() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			logger.log(Level.INFO, "Background music is paused.");
		}
	}

	public Stage getStage() {
		return stage;
	}

}
