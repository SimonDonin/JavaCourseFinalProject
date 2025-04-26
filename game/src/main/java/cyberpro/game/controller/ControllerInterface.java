package cyberpro.game.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
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

	void specifyPlayersSetAndLevel(ArrayList<Player> playersSet, String level);

	String getPlayerIdByNumber(int playerNumber);

	Board getBoard();

	ArrayList<Player> getPlayers();

	void draw();

	public void setPlayers(ArrayList<Player> players);
	
	public void setLevel(String level);
	
	public ArrayList<ArrayList<Player>> getPlayersSets();
	
	public void startGame() throws FileNotFoundException, IOException;
	
	public void gameOverComplete();
	
	public void pauseOn();
	
	public void pauseOff();	
	
	public void exitApp();

}
