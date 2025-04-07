package cyberpro.game.model;

import java.text.SimpleDateFormat;
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

		// Календарь для вычислений
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.SECOND, DEFAULT_RAYS_DURATION); // Добавляем заданное время

		// Получаем новую дату
		raysOffDate = calendar.getTime();

		// Форматируем дату
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = dateFormat.format(raysOffDate);
		System.out.println("Новое время: " + formattedDate);
	}

}
