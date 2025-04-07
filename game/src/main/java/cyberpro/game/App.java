package cyberpro.game;

import cyberpro.game.controller.GameController;
import cyberpro.game.model.Board;
import cyberpro.game.model.Game;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        GameController gameController = new GameController();  
    	gameController.mainMenu();
    }
}
