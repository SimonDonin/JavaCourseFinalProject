package cyberpro.game.model;

import java.util.ArrayList;
import java.util.Set;

enum Colors {
	YELLOW, GREEN, PURPLE
};

public class Player {

	private String id;
	private static int counter = 0;
	private String name;
	private int hitpoints;
	private Coordinates coordinates;
	private boolean isAlive;
	private int speed;
	private Colors color;
	private ArrayList<Modifier> playerModifiers;

	public Player(String name, Coordinates coordinates) {
		this.id = "P" + ++counter;
		this.name = name;
		this.hitpoints = 100;
		this.coordinates = coordinates;
		this.speed = 1;
		this.color = Colors.GREEN;
		this.playerModifiers = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public int getSpeed() {
		return speed;
	}

	public Coordinates getCoordinates() {
		return coordinates;
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

	@Override
	public String toString() {
		return "Player " + name + ":" + "\n\tcoordinates: [" + coordinates.getX() + ", " + coordinates.getY() + "]"
				+ "\n\tspeed: " + speed + "\n\tcolor: " + color + "\n\t" + playerModifiers.toString() + "\n";
	}

}
