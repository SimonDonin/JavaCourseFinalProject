package cyberpro.game.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bomb {
	private static int counter = 0;
	private static final int DEFAULT_RAYS_LENGTH = 2;
	private String id;
	private String playerId;
	private Coordinates coordinates;
	private boolean distantExplosion;
	private SimpleDateFormat explosionTime;
	private int raysRange;
	private SimpleDateFormat raysOffDate;
	
	public Bomb(String playerId, Coordinates coordinates, boolean distantExplosion, SimpleDateFormat explosionTime,
			int raysRange) {
		this.id = "B" + ++counter;
		this.coordinates = coordinates;
		this.distantExplosion = distantExplosion;
		this.explosionTime = explosionTime;
		this.raysRange = raysRange;
	}
	
	// transform to explosion and cause rays appearance till specified time
	// to make all the calculations the method should have
	// input should include: 
	public void explode() {
		raysOffDate
	}

}
