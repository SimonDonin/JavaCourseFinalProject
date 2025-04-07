package cyberpro.game.controller;

import cyberpro.game.model.Board;

public interface ControllerInterface {
	void playerMoveUp(String playerId);

	void playerMoveDown(String playerId);

	void playerMoveLeft(String playerId);

	void playerMoveRight(String playerId);
	
	void playerPlantBomb(String playerId);

	String getPlayerIdByNumber(int playerNumber);

	Board getBoard();
}
