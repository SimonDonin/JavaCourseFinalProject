package cyberpro.game.model;

import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cyberpro.game.controller.ModifierType;
import javafx.scene.image.ImageView;

public class Modifier {
	private static int counter = 0;
	private static final ArrayList<ModifierType> endlessModifierTypes = new ArrayList<ModifierType>(
			Arrays.asList(ModifierType.PLUS_BOMB, ModifierType.PLUS_RANGE));
	private static final int DEFAULT_MODIFIERS_DURATION = 30;
	private String id;
	private Coordinates coordinates;
	private ModifierType type;
	private int duration;
	private Date dateOff;

	public Modifier(Coordinates coordinates, ModifierType type) {
		this.id = "M" + ++counter;
		this.coordinates = coordinates;
		this.type = type;
		this.duration = endlessModifierTypes.contains(type) ? 0 : DEFAULT_MODIFIERS_DURATION;
	}

	public String getId() {
		return id;
	}

	public int getDuration() {
		return duration;
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
