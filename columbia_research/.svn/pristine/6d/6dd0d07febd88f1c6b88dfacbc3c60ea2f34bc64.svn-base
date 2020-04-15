package edu.columbia.ke.component;


public class IndexTable {
	
	private int[][] coordinateTable;
	private int[][][] idTable;

	public IndexTable(int dx, int dy, int dz) {
		coordinateTable = new int [dx*dy*dz][3];
		idTable = new int [dz][dy][dx];
		int counter = 0;
		for (int k = 0 ; k < dz; k++) {
			for (int j = 0 ; j < dy; j++) {
				for (int i = 0 ; i < dx; i++) {
					coordinateTable[counter][0] = i;
					coordinateTable[counter][1] = j;
					coordinateTable[counter][2] = k;
					
					idTable[k][j][i] = counter;
					
					counter++;
				}
			}
		}
	}
	
	public int get(int id, int dim){
		return coordinateTable[id][dim];
	}
	
	public int getID(int x, int y, int z) {
		return idTable[z][y][x];
	}

}
