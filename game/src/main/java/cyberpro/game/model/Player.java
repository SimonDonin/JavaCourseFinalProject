package cyberpro.game.model;

	import java.util.ArrayList;
	import java.util.Set;

	enum Color {Yellow, Green, Purple}; // change colors when we have sprite
	
	public class Player {
		
		private String id; 
		private static int counter = 0;
		private String name;
		private int hitpoints;
	    private Coordinates coordinates;
		private boolean isAlive;
		private int speed;
		Color color;
		private ArrayList <Modifier> playerModifiers; 
		
		 public Player(String id, String name, int hitpoints, Coordinates coordinates, int speed, Color color) {
		        this.id = "P"+ ++counter;
		        this.name = name;
		        this.hitpoints = hitpoints;
		        this.coordinates = coordinates;
		        this.speed = speed;
		        this.color = color;
		        this.playerModifiers = new ArrayList<>();
		    }


		/*
		 * public boolean moveUp() { coordinates[1] -= speed; return true; }
		 * 
		 * public boolean moveDown() { coordinates[1] += speed; return true; }
		 * 
		 * public boolean moveLeft() { coordinates[0] -= speed; return true; }
		 * 
		 * public boolean moveRight() { coordinates[0] += speed; return true; }
		 */

		/*
		 * public void addModifier(String modificator) {
		 * playerModifiers.add(modificator); }
		 */

		 public void removeModifier(String modificator) {
			 playerModifiers.remove(modificator);
		 }

		 public static Player findPlayerById(int id) {
			 return null; 
		  
		 }
	}

