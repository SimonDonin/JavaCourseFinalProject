package cyberpro.game.model;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bomb {
	private static int counter = 0;
	private static final int DEFAULT_RAYS_RANGE = 2;
	private static final int DEFAULT_RAYS_DURATION = 2;
	private String id;
	private String playerId;
	private Coordinates coordinates;
	private boolean distantExplosion;
	private Date raysOffDate;
	private CopyOnWriteArrayList<Coordinates> rays;

	public Bomb(String playerId, Coordinates coordinates, boolean distantExplosion, Date explosionTime) {
		this.id = "B" + ++counter;
		this.playerId = playerId;
		this.coordinates = coordinates;
		this.distantExplosion = distantExplosion;
		this.rays = new CopyOnWriteArrayList<Coordinates>();
	}

	public static int getDefaultRaysRange() {
		return DEFAULT_RAYS_RANGE;
	}

	public static int getDefaultRaysDuration() {
		return DEFAULT_RAYS_DURATION;
	}

	public Date getRaysOffDate() {
		return raysOffDate;
	}

	public String getPlayerId() {
		return playerId;
	}

	public CopyOnWriteArrayList<Coordinates> getRays() {
		return rays;
	}

	public boolean isDistantExplosion() {
		return distantExplosion;
	}

	// adds coordinates to the explosion rays collection for the bomb
	public boolean addToRays(Coordinates coordinates) {
		System.out.println("Adding rays coordinate " + coordinates);
		for (Coordinates coords : rays) {
			if (coords.getX() == coordinates.getX() && coords.getY() == coordinates.getY()) {
				return false;
			}
		}
		rays.add(new Coordinates(coordinates.getX(), coordinates.getY()));
		return true;
	}

	public String getId() {
		return id;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	// explodes the bomb
	public void explode() {
		Date currentDate = new Date();
		// A calendar for calculations
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		// Adding the time specified
		calendar.add(Calendar.SECOND, DEFAULT_RAYS_DURATION); 
		// Getting a new date
		raysOffDate = calendar.getTime();
	}

	@Override
	public String toString() {
		return "Bomb " + id + " with coords " + coordinates;
	}

}
