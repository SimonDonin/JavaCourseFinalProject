package cyberpro.game.model;

	import java.util.ArrayList;
	import java.util.Set;
	enum Color { YELLOW, GREEN, PURPLE }; 
	
	public class Player {
		
		private String id; 
		private static int counter = 0;
		private String name;
		private int hitpoints;
	    private Coordinates coordinates;
		private boolean isAlive;
		private int speed;
		private ArrayList <Modifier> playerModifiers; 
		private Color color;

		
		 
		public Player(String name,Coordinates coordinates, Color color) {
				 
			this.id= "P"+ ++counter;
			this.name = name;
			this.coordinates = coordinates;
			this.color=color;
			
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
		    
		    
		
	