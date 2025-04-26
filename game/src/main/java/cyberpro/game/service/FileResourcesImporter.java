package cyberpro.game.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cyberpro.game.model.Board;
import cyberpro.game.view.TileType;

public class FileResourcesImporter {
	private final Logger logger = Logger.getLogger(FileResourcesImporter.class.getName());

	public Board importLevelIntoBoard(String levelName, String level) throws FileNotFoundException {
		// This is simple implementation of loadLevel method
		// External method will decide, with level we want to load

		logger.setLevel(Level.FINE);
		logger.log(Level.INFO, "level = " + level);

		 InputStream is = getClass().getResourceAsStream(level); 
			if (is == null) {
				throw new FileNotFoundException("Resource not found: " + levelName);
			}
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);

			List<String> lines = new ArrayList<>();
			String line;

			try {
				while ((line = reader.readLine()) != null) {
					lines.add(line);
					System.out.println(line);
				}

			int rows = lines.size();
			int cols = lines.get(0).length();
			if (rows != cols) {
				logger.log(Level.INFO, "Board is not square");
			}

			// closing all streams
			isr.close();
			reader.close();
			
			

			Board board = new Board(levelName, rows);

			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					char c = lines.get(y).charAt(x);
					switch (c) {
					case '#' -> board.setCell(x, y, TileType.CONCRETE_WALL);
					case '$' -> board.setCell(x, y, TileType.BRICK_WALL);
					case '@' -> {
						board.setCell(x, y, TileType.FLOOR);
					}
					default -> board.setCell(x, y, TileType.FLOOR);
					}
				}
			}
			
			return board;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Got an IOException exception...");
				e.printStackTrace();
				return null;
			}
	}

}