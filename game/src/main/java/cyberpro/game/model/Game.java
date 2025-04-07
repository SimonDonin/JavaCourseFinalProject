package cyberpro.game.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import cyberpro.game.view.TileType;
import java.io.FileNotFoundException;

public class Game {
	private static final int DEFAULT_PLAYERS_NUMBER = 2;
	private static final String DEFAULT_PLAYER1_NAME = "Player 1";
	private static final String DEFAULT_PLAYER2_NAME = "Player 2";

	private String name;
	private int playersNumber;
	private Board board;
	private ArrayList<Player> players;
	private ArrayList<Bomb> bombs;
	private ArrayList<Modifier> modifiers;

	public Game(String name, int playersNumber) {
		this.name = name;
		this.playersNumber = playersNumber;
		// this.board = board;
		players = new ArrayList<>();
		bombs = new ArrayList<>();
		modifiers = new ArrayList<>();
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Bomb> getBombs() {
		return bombs;
	}

	public ArrayList<Modifier> getModifiers() {
		return modifiers;
	}

	public Player findPlayerById(String Id) {
		if (players == null)
			return null;
		for (Player player : players) {
			if (player.getId().equalsIgnoreCase(Id)) {
				return player;
			}
		}
		return null;
	}

	public Board getBoard() {
		return board;
	}

	public Bomb findBombById(String Id) {
		if (bombs == null)
			return null;
		for (Bomb bomb : bombs) {
			if (bomb.getId().equalsIgnoreCase(Id)) {
				return bomb;
			}
		}
		return null;
	}

	public Modifier findModifierById(String Id) {
		if (modifiers == null)
			return null;
		for (Modifier modifier : modifiers) {
			if (modifier.getId().equalsIgnoreCase(Id)) {
				return modifier;
			}
		}
		return null;
	}

	public boolean addPlayer(Player player) {
		// search for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.add(player);
		return true;
	}

	public boolean removePlayer(Player player) {
		// if there are no players
		if (players == null)
			return false;
		// search for a double and if it is found - return false
		Player playerFound = findPlayerById(player.getId());
		if (playerFound != null) {
			return false;
		}
		this.players.remove(player);
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Game " + name + " with " + playersNumber + " players on the " + board.getName() + " board" + "\nBOARD"
				+ "\n" + board.toString() + "\nPLAYERS" + "\n" + players.toString() + "\nBOMBS" + "\n"
				+ bombs.toString() + "\nMODIFIERS" + "\n" + modifiers.toString();
	}

	/*
	 * plantBomb findBombById findModifierById
	 */
        
        public void loadLevel(String level) throws FileNotFoundException, IOException {
            // This is simple implementation of loadLevel method
            // External method will decide, with level we want to load
            InputStream is = getClass().getResourceAsStream("level1.txt");
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + level);
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            // [TODO] There should be a better way to simple read a file

            List<String> lines = new ArrayList<>();
            String line;
        
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                System.out.println(line);
            }
        
            int rows = lines.size();
            int cols = lines.get(0).length();
            if (rows != cols) { System.out.println("Board is not square"); }
            
            board = new Board(level, rows);
        
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    char c = lines.get(y).charAt(x);
                    switch (c) {
                        case '#' -> board.setCell(y, x, TileType.CONCRETE_WALL);
                        case '$' -> board.setCell(y, x, TileType.BRICK_WALL);
                        case '@' -> {
                            board.setCell(y, x, TileType.FLOOR);
                            // Set player coordinates
                            // playerX = x;
                            // playerY = y;
                        }
                        default -> board.setCell(y, x, TileType.FLOOR);
                    }
                }
            }
        }
}
