package cyberpro.game.model;

public class Board {

	 private enum Cell { EMPTY, WALL, BOMB }; // we use these as a list of indicators 
	 private Cell[][] cells;
	 private String name;
	 private int size;
	
	
	public Board(String name, int size) {
		this.name = name;
		this.size = size;
	//should we include cells?
		
		
	}
	public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public void setCell(int x, int y, Cell cell) {
        cells[x][y] = cell;
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
}
