package cyberpro.game.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Bomb {
	private static int counter = 0;
	private static final int DEFAULT_RAYS_RANGE = 2;
	private static final int DEFAULT_RAYS_DURATION = 2;
	private String id;
	private String playerId;
	private Coordinates coordinates;
	private boolean distantExplosion;
	private Date explosionTime;
	private int raysRange;
	private Date raysOffDate;
	private ArrayList<Coordinates> raysVertical;
	private ArrayList<Coordinates> raysHorizontal;

	/*
	 * A player plants a bomb and the following input values are passing on:
	 * playerId, coordinates, if it is a distantExplosion, exact explosionTime,
	 * raysRange. raysOffDate can't be specified at the moment as it will be defined
	 * at the explosion moment only. The explosion moment depends on a remote
	 * detonation, so it may vary.
	 */

	public Bomb(String playerId, Coordinates coordinates, boolean distantExplosion, Date explosionTime) {
		this.id = "B" + ++counter;
		this.coordinates = coordinates;
		this.distantExplosion = distantExplosion;
		this.explosionTime = explosionTime;
		this.raysRange = DEFAULT_RAYS_RANGE;
		this.raysHorizontal = new ArrayList<Coordinates>();
		this.raysVertical = new ArrayList<Coordinates>();
	}

	public static int getDefaultRaysRange() {
		return DEFAULT_RAYS_RANGE;
	}

	public ArrayList<Coordinates> getRaysVertical() {
		return raysVertical;
	}

	public boolean addToRaysHorizontal(Coordinates coordinates) {
		for (Coordinates coords : raysHorizontal) {
			if (coords.getX() == coordinates.getX() && coords.getY() == coordinates.getY()) {
				return false; 
			}
			raysHorizontal.add(coordinates);
		}
		return true;
	}
	
	public boolean addToRaysVertical(Coordinates coordinates) {
		for (Coordinates coords : raysHorizontal) {
			if (coords.getX() == coordinates.getX() && coords.getY() == coordinates.getY()) {
				return false; 
			}
			raysVertical.add(coordinates);
		}
		return true;
	}
	
	public ArrayList<Coordinates> getRaysHorizontal() {
		return raysHorizontal;
	}

	public String getId() {
		return id;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	// transforms to an explosion and causes rays appearance till a specified time
	// to make all the calculations the method should have
	// input should include:
	public void explode() {
		Date currentDate = new Date();

		// A calendar for calculations
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.SECOND, DEFAULT_RAYS_DURATION); // Adding the time specified

		// Getting a new date
		raysOffDate = calendar.getTime();

		// Formating the date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = dateFormat.format(raysOffDate);
		System.out.println("New time: " + formattedDate);
		
		System.out.println("The bomb id = " + id + " has exploded!");
	}

}
