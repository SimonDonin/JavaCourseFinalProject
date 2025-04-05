package cyberpro.game.model;

enum Cell {
	EMPTY, WALL, BOMB
}

public class Board {

	private Cell[][] cells;
	private String name;
	private int size;

	public Board(String name, int size) {
		this.name = name;
		this.size = size;
		cells = new Cell[size][size];
	}

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	public void setCell(int x, int y, Cell cell) {
		cells[x][y] = cell;
	}

	public int getSize() {
		return size;
	}

	public void initialize() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				cells[i][j] = Cell.EMPTY;
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