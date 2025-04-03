package cyberpro.game.model;

	import java.util.ArrayList;
	import java.util.Set;

	enum Color {YELLOW, GREEN, PURPLE}; 
	
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

     public boolean moveDown() {
		        coordinates.setY(coordinates.getY() + speed);
		        return true;
		    }
		 
		 public boolean moveUp() {
		        coordinates.setX(coordinates.getX() - speed);
		        return true;
		    }

		 public boolean moveLeft() {
		        coordinates.setX(coordinates.getX() - speed);
		        return true;
		    }

		 public boolean moveRight() {
		        coordinates.setX(coordinates.getX() + speed);
		        return true;
		    }

		 public void addModifier(Modifier modificator) {
		        playerModifiers.add(modificator);
		    }

		 public void removeModifier(Modifier modificator) {
		        playerModifiers.remove(modificator);
		    }

		    
		 public boolean isAlive() {
		        return isAlive;
		    }

		 public void setAlive(boolean status) {
		        this.isAlive = status;
		    }
		}
