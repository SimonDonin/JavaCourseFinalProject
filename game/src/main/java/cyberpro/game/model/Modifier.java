package cyberpro.game.model;

import java.util.Date;

import cyberpro.game.controller.ModifierType;

public class Modifier {
	private static int counter = 0;
	private String id;
	private Coordinates coordinates;
	private ModifierType type;
	private int duration;
	private Date dateOff;

	public Modifier(Coordinates coordinates, ModifierType type, int duration) {
		this.id = "M" + ++counter;
		this.coordinates = coordinates;
		this.type = type;
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}
        
        public ModifierType getType() {
            return type;
        }

	@Override
	public String toString() {
		return "Modifier " + id + " with type " + type + " with coords " + coordinates;
	}

}
