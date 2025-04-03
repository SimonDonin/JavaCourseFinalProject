package cyberpro.game;

import cyberpro.game.model.Board;
import cyberpro.game.model.Game;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Game game = new Game("myGame", 2, new Board("myBoard", 12));
        System.out.println(game);
    }
}
