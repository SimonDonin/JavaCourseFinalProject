package cyberpro.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import cyberpro.game.controller.ModifierType;

enum Colors {
	YELLOW, GREEN, PURPLE
};

public class Player implements Serializable {

	private String id;
	private static int counter = 0;
	private String name;
	private Coordinates coordinates;
	private boolean isAlive;
	private int speed;
	private Colors color;
	private CopyOnWriteArrayList<Modifier> playerModifiers;
	private int winsCount = 0;
	private int lossesCount = 0;
	private int drawsCount = 0;
	private static final int DEFAULT_PLAYER_SPEED = 100;

	public Player(String name, Coordinates coordinates) {
		this.id = "P" + ++counter;
		this.name = name;
		this.coordinates = coordinates;
		this.speed = 100;
		this.color = Colors.GREEN;
		this.playerModifiers = new CopyOnWriteArrayList<>();
		this.isAlive = true;
	}

	public String getId() {
		return id;
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Player.counter = counter;
	}

	public String getName() {
		return name;
	}

	public int getSpeed() {
		return speed;
	}

	private void setSpeed(int speed) {
		this.speed = speed;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public int getWinsCount() {
		return winsCount;
	}

	public int getLossesCount() {
		return lossesCount;
	}

	public int getDrawsCount() {
		return drawsCount;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public void resetModifiers() {
		playerModifiers = new CopyOnWriteArrayList<>();
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
		return "Player " + "id =" + id + " " + name + ":" + "\n\tcoordinates: [" + coordinates.getX() + ", "
				+ coordinates.getY() + "]" + "\n\tspeed: " + speed + "\n\tcolor: " + color + "\n\t"
				+ playerModifiers.toString() + "\n\t Score: wins = " + winsCount + ", losses  = " + lossesCount
				+ ", draws = " + drawsCount + "\n";
	}

	// kills the player
	public boolean kill() {
		if (!isAlive())
			return false;
		System.err.println("Player " + id + " was killed by an aweful explosion!");
		setAlive(false);
		return true;
	}

	// finds the player's modifiers by id
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

	// finds a player's modifier by modifier's type
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

	// counts number of modifiers by the type provided
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

	// calculates the player's speed
	public void calculatePlayerSpeed() {
		int speedInModifiers = 0;
		if (findModifierByType(ModifierType.SPEED_UP) != null) {
			speedInModifiers = 100;
		}
		setSpeed(DEFAULT_PLAYER_SPEED + speedInModifiers);
	}

	public void win() {
		winsCount++;
	}

	public void loose() {
		lossesCount++;
	}

	public void draw() {
		drawsCount++;
	}
}