package cyberpro.game.model;

import java.util.ArrayList;
import java.util.Set;

import cyberpro.game.controller.ModifierType;

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
	private static final int DEFAULT_PLAYER_SPEED = 1;

	public Player(String name, Coordinates coordinates) {
		this.id = "P" + ++counter;
		this.name = name;
		this.hitpoints = 100;
		this.coordinates = coordinates;
		this.speed = 1;
		this.color = Colors.GREEN;
		this.playerModifiers = new ArrayList<>();
		this.isAlive = true;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public void addModifier(Modifier modifier) {
		playerModifiers.add(modifier);
	}

	public void removeModifier(Modifier modifier) {
		playerModifiers.remove(modifier);
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

	public boolean kill() {
		if (!isAlive()) return false;
		System.err.println("Player " + id + " was killed by an aweful explosion!");
		setAlive(false);
		return true;
	}
	
	public Modifier findModifierById(String Id) {
		if (playerModifiers == null)
			return null;
		for (Modifier modifier : playerModifiers) {
			if (modifier.getId().equalsIgnoreCase(Id)) {
				return modifier;
			}
		}
		return null;
	}
	
	public Modifier findModifierByType(ModifierType modifierType) {
		if (playerModifiers == null)
			return null;
		for (Modifier modifier : playerModifiers) {
			if (modifier.getType() == modifierType) {
				return modifier;
			}
		}
		return null;
	}
	
	public int countModifiersByType(ModifierType modifierType) {
		if (playerModifiers == null)
			return -1;
		int count = 0;
		for (Modifier modifier : playerModifiers) {
			if (modifier.getType() == modifierType) {
				count++;
			}
		}
		return count;
	}
	
	public int calculatePlayerSpeed(String playerId) {
		int speedInModifiers = 0;
		if (findModifierByType(ModifierType.SPEED_UP) != null) {
			speedInModifiers = 1;
		}
		return DEFAULT_PLAYER_SPEED; /* DEFAULT_PLAYER_SPEED + speedInModifiers; */
	}

}