package cyberpro.game.model;

import java.util.Date;

enum Type {
	SPEED_UP, PLUS_BOMB, PLUS_RANGE, REVERSE_CONTROLS, REMOTE_EXPLOSION
};

public class Modifier {
	private int counter = 0;
	private String id;
	private Coordinates coordinates;
	private Type type;
	private int duration;
	private Date dateOff;

	public Modifier(Coordinates coordinates, Type type, int duration) {
		this.id = "M" + ++counter;
		this.coordinates = coordinates;
		this.type = type;
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

}
