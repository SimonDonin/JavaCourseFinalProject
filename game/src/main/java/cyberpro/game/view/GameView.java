package cyberpro.game.view;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
// Do not shure if it possible to use here. Or juct controller will use it.
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;

import cyberpro.game.model.*;

/**
 *
 * @author mikhail
 */
public class GameView {
    private final int TILE_SIZE = 40;
    // This is actually size of a sprite. It will depend on actual size of game board
    private GridPane grid = new GridPane();
    // Create GridPane object to display all graphical objects.
    // It is also possible to use TilePane, but we use GridPane because of better control methods
    private TileType[][] gameBoard;
    // Game board array. We have to recive it from a Controller
    private final Image playerOneImage = new Image(getClass().getResourceAsStream("Character1.png"));
    private final Image brickWallImage = new Image(getClass().getResourceAsStream("BrickWall.png"));
    private final Image concreteWallImage = new Image(getClass().getResourceAsStream("ConcreteWall.png"));
    private final Image floorImage = new Image(getClass().getResourceAsStream("Floor.png"));
    private final Image bombImage = new Image(getClass().getResourceAsStream("Bomb.png"));
    private final Image enemyImage = new Image(getClass().getResourceAsStream("Enemy.png"));
    private final Image blastImage = new Image(getClass().getResourceAsStream("Blast.png"));
    
    private final int gridWidth = 10;
    private final int gridHeight = 10;
    // Shall be defined at model.

    public GameView(TileType[][] board) {
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
                    case BRICK_WALL -> tileView.setImage(brickWallImage);
                    case CONCRETE_WALL -> tileView.setImage(concreteWallImage);
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
            grid.add(tileView, player.getCoordinates().getX(), player.getCoordinates().getY());
            // We just put player tile above the grid at the end of draw board cycle. 
            // So it does not "shade" tiles below the player.
        }
        // All players are on map
        /*
        for (Bomb bomb : bombs) {
            ImageView tileView = new ImageView();
            tileView.setFitWidth(TILE_SIZE);
            tileView.setFitHeight(TILE_SIZE);
            // Do we need to know, who put the bomb??
            tileView.setImage(bombImage);
            grid.add(tileView, bomb.getCoordinates().getX(), bomb.getCoordinates().getY());
            // !! Bomb is not implemented yet. Wait for it
        }
        // All bombs are on map
        */
        // Bomb class is not finished yet
    }

}
