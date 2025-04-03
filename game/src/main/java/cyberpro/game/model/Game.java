package cyberpro.game.model;

import java.util.ArrayList;

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
	
	
	
	public Game(String name, int playersNumber, Board board) {
		this.name = name;
		this.playersNumber = playersNumber;
		this.board = board;
		this.players = players;
		this.bombs = bombs;
		this.modifiers = modifiers;
		ArrayList<Player> players = new ArrayList<>();
		ArrayList<Bomb> bombs = new ArrayList<>();
		ArrayList<Modifier> modifiers = new ArrayList<>();
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Game " + name + " with " + playersNumber + " players on the " + board.getName() + " board" ;
	}



	/*
	 * plantBomb findBombById findModifierById
	 */
}
