package cyberpro.game.model;

import cyberpro.game.view.*;

public class Board {

	private TileType[][] cells;
	private String name;
	private int size;

	public Board(String name, int size) {
		this.name = name;
		this.size = size;
		cells = new TileType[size][size];
	}

	public TileType getCell(int x, int y) {
		return cells[x][y];
	}

	public TileType[][] getCells() {
		return cells;
	}

	public void setCell(int x, int y, TileType cell) {
		cells[x][y] = cell;
	}

	public int getSize() {
		return size;
	}

	public void initialize() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				cells[i][j] = TileType.FLOOR;
			}
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				str.append(i + ":" + j + "=" + cells[i][j] + " ");
			}
			str.append("\n");
		}
		return str + "";
	}
}