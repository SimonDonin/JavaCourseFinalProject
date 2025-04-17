package cyberpro.game.controller;

import java.util.ArrayList;

import cyberpro.game.model.Board;
import cyberpro.game.model.Player;

public interface ControllerInterface {
	void playerMoveUp(String playerId);

	void playerMoveDown(String playerId);

	void playerMoveLeft(String playerId);

	void playerMoveRight(String playerId);
	
	void playerPlantBomb(String playerId);
	
	void playerRemoteBombExplode(String playerId);

	String getPlayerIdByNumber(int playerNumber);

	Board getBoard();
	
	ArrayList<Player> getPlayers();
	
	void draw();
}
