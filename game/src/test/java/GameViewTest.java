/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit3TestClass.java to edit this template
 */

import cyberpro.game.model.Bomb;
import cyberpro.game.model.Modifier;
import cyberpro.game.model.Player;
import java.util.ArrayList;
import junit.framework.TestCase;



/**
 *
 * @author mikhail
 */
public class GameViewTest extends TestCase {
    
    public GameViewTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of drawGrid method, of class GameView.
     */
    public void testDrawGrid() {
        System.out.println("drawGrid");
        ArrayList<Player> players = null;
        ArrayList<Bomb> bombs = null;
        ArrayList<Modifier> modifiers = null;
        GameView instance = null;
        instance.drawGrid(players, bombs, modifiers);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
