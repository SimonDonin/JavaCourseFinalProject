package cyberpro.game.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cyberpro.game.model.Board;
import cyberpro.game.view.TileType;

public class FileResourcesImporter {
	public Board importLevelIntoBoard(String levelName, String level) throws FileNotFoundException, IOException {
		// This is simple implementation of loadLevel method
		// External method will decide, with level we want to load
		
		System.err.println("level = " + level);
		
		try (InputStream is = getClass().getResourceAsStream(level);)
		{if (is == null) {
			throw new FileNotFoundException("Resource not found: " + levelName);
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		// [TODO] There should be a better way to simple read a file

		List<String> lines = new ArrayList<>();
		String line;

		while ((line = reader.readLine()) != null) {
			lines.add(line);
			System.out.println(line);
		} 

		int rows = lines.size();
		int cols = lines.get(0).length();
		if (rows != cols) {
			System.out.println("Board is not square");
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
					// Set player coordinates
					// playerX = x;
					// playerY = y;
				}
				default -> board.setCell(x, y, TileType.FLOOR);
				}
			}
		}
		return board;
	}
		
	}

}