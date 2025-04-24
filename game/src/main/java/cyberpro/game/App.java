package cyberpro.game;

import cyberpro.game.controller.GameController;
import cyberpro.game.model.Board;
import cyberpro.game.model.Game;
import java.io.IOException;

public class App {
	public static void main(String[] args) {
		GameController gameController = new GameController();
		gameController.enterController();
	}
}
    