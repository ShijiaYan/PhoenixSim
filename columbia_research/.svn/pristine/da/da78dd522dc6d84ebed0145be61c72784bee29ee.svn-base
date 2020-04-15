package edu.columbia.sebastien.fat_trees;

import java.util.ArrayDeque;
import java.util.ArrayList;

import cern.colt.Arrays;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;

public class OrthogonalFatTreeMatching {
	
	private int decisionsNb = 0;
	private int wrongDecisions = 0;
	
	private int lines;
	private int columns;
	private int radixPerLine;
	private int radixPerColumn;
	private int[] yesPerColumn;
	private int[] yesPerLine;
	private int[] noPerColumn;
	private int[] noPerLine;	
	private int[][] matchings;
	private STATE[][] states_;
	
	private int[] maxMatchingsAvailable;
	
	private int bonusMatchingLeft;
	
	PcolorGUI gui ;
	
	private boolean infer(Decision d) {
		try {
			if (states_[d.column][d.line]== d.state ) return true;
			states_[d.column][d.line] = d.state;
			if (d.state == STATE.YES) {
				yesPerLine[d.line]++;
				yesPerColumn[d.column]++;
				boolean fails = false;
				if (yesPerLine[d.line] > radixPerLine) fails = true;
				if (yesPerColumn[d.column] > radixPerColumn) fails = true;
				// infering matchings
				for (int k = 0 ; k < lines ; k++) {
					if (k == d.line) continue;
					if (states_[d.column][k] == STATE.YES) {
						matchings[d.line][k]++;
						matchings[k][d.line]++;
						maxMatchingsAvailable[k]--;
						maxMatchingsAvailable[d.line]--;
						if (maxMatchingsAvailable[k] < MoreArrays.countsOfValue(matchings[k], 0)) {
							fails = true;
						}
						if (maxMatchingsAvailable[d.line] < MoreArrays.countsOfValue(matchings[d.line], 0)) {
							fails = true;
						}						
					/*	if (maxMatchingsAvailable[k] == 0 && MoreArrays.hasThisValue(,0)) {
							fails = true;
						}
						if (maxMatchingsAvailable[d.line] == 0 && MoreArrays.hasThisValue(matchings[d.line],0)) {
							fails = true;
						}	*/					
						
					}
				}
				if (fails) return false;
				

				// infering radix
				if (yesPerColumn[d.column] == radixPerColumn) {
					for (int k = 0 ; k < lines ; k++) {
						if (states_[d.column][k] == STATE.MAYBE) {
							Decision sub_d = new Decision(k, d.column, STATE.NO);
							d.addDependence(sub_d);
							boolean problem = infer(sub_d);
							if (!problem) return problem;
						}
					}
				}
				if (yesPerLine[d.line] == radixPerLine) {
					for (int k = 0 ; k < columns ; k++) {
						if (states_[k][d.line] == STATE.MAYBE) {
							Decision sub_d = new Decision( d.line,k, STATE.NO);
							d.addDependence(sub_d);
							boolean problem = infer(sub_d);
							if (!problem) return problem;
						}
					}
				}				
				
				if (noPerColumn[d.column] == lines - radixPerColumn) {
					for (int k = 0 ; k < lines ; k++) {
						if (states_[d.column][k] == STATE.MAYBE) {
							Decision sub_d = new Decision(k, d.column, STATE.YES);
							d.addDependence(sub_d);
							boolean problem = infer(sub_d);
							if (!problem) return problem;
						}
					}					
				}
				if (bonusMatchingLeft == 0) {
					for (int k = 0 ; k < lines ; k++) {
						if (k == d.line) continue;
						if (states_[d.column][k] == STATE.YES) { // k matches with d.column
							for (int c = 0 ; c < columns ; c++) {
								if (c == d.column) continue;
								if (states_[c][k] == STATE.YES) {
									if (states_[c][d.line] == STATE.MAYBE) {
										Decision sub_d = new Decision(d.line, c, STATE.NO);
										d.addDependence(sub_d);
										boolean problem = infer(sub_d);
										if (!problem) return problem;
									}
								}
							}
						
						}
					}
				}
				
				
				// infering matchings
			/*	for (int i = 0 ; i < lines ; i++) { // search for completed lines
					if (yesPerLine[i] == radixPerLine) {
						for (int j = 0 ; j < columns ; j++) { // looking up for indexes
							if (state[i][j] == STATE.YES) { // looking up for other indexes
								
							}
						}
					}
				}*/
				return true;
			}
			if (d.state == STATE.NO) {
				noPerLine[d.line]++;
				noPerColumn[d.column]++;				
				if (noPerLine[d.line] > columns - radixPerLine) return false;
				if (noPerColumn[d.column] > lines - radixPerColumn) return false;		
				return true;
			}
			return true;
		}
		finally {
		//	updateToUser();
		//	System.out.print(Arrays.toString(maxMatchingsAvailable));
		//	System.out.println();
		}
	}
	
	private void backtrack(Decision d) {
		// backtracking sub-decisions
		for (Decision sub_d : d.dependentDecisions) {
			backtrack(sub_d);
		}
		states_[d.column][d.line] = STATE.MAYBE;
		if (d.state == STATE.YES) {
			yesPerLine[d.line]--;
			yesPerColumn[d.column]--;
			for (int k = 0 ; k < lines ; k++) {
				if (k == d.line) continue;
				if (states_[d.column][k] == STATE.YES) {
					matchings[d.line][k]--;
					matchings[k][d.line]--;
					maxMatchingsAvailable[k]++;
					maxMatchingsAvailable[d.line]++;
				}
			}
		}
		if (d.state == STATE.NO) {
			noPerLine[d.line]--;
			noPerColumn[d.column]--;	
		}
	}
	
	private static enum STATE {
		YES, NO, MAYBE
	}
	
	private class Decision {
		
		int line;
		int column;
		STATE state;
		ArrayList<Decision> dependentDecisions;
		
		Decision(int line, int column, STATE state) {
			this.line = line;
			this.column= column;
			this.state = state;
			dependentDecisions = new ArrayList<Decision>(0);
			decisionsNb++;
		}
		
		void setNO() {
			state = STATE.NO;
			dependentDecisions.clear();
			decisionsNb++;
		}

		public boolean isYES() {
			return (state == STATE.YES);
		}
		
		public void addDependence(Decision d) {
			dependentDecisions.add(d);
		}
		
		public String toString() {
			return String.format("%02d - %02d ", line, column) + "" + state;
		}
	}
	
	public OrthogonalFatTreeMatching(int lines, int columns, int radixPerLine, int radixPerColumn) {
		this.columns = columns;
		this.lines = lines;
		this.radixPerLine = radixPerLine;
		this.radixPerColumn = radixPerColumn;
		
		noPerColumn = new int[columns];
		noPerLine = new int[lines];
		yesPerColumn = new int[columns];
		yesPerLine = new int[lines];
		matchings = new int[lines][lines];
		states_ = new STATE[columns][lines];
		maxMatchingsAvailable = new int[lines];

		
		for (int i = 0 ; i < lines ; i++) {
			maxMatchingsAvailable[i] = (radixPerColumn-1)*radixPerLine;
			matchings[i][i] = 1;
			for (int j = 0 ; j < columns ; j++) {
				states_[j][i] = STATE.MAYBE;
			}
		}
		gui = new PcolorGUI(states_, new PcolorGUI.Adapter<STATE>() {
			@Override
			public int adapt(STATE val) {
				return val.ordinal();
			}
		});
		
		int requiredMatchings = lines*(lines-1)/2;
		int possibleMatchings = (radixPerColumn*(radixPerColumn-1)/2)*columns;
		bonusMatchingLeft = possibleMatchings - requiredMatchings;
		
		gui.showInFrame();
	}
	
	private void updateToUser() {
		
		gui.close();
		gui = new PcolorGUI(states_, new PcolorGUI.Adapter<STATE>() {

			@Override
			public int adapt(STATE val) {
				return val.ordinal();
			}
		});	
		gui.setYMult(12);
		gui.setXMult(12);		
		gui.showInFrame();
		gui.toString();
	}
	
	private void findMatching() {
		int offset = 0;
		for (int i = 0 ; i < columns ; i++) {
		//	states_[offset][i] = STATE.YES;
			Decision d = new Decision(offset, i, STATE.YES);
			infer(d);
		//	yesPerLine[offset]++;
		//	yesPerColumn[i]++;
			if (i > 1 && (i % (radixPerLine-1)) == 0) offset++;
		}
		offset = 0;
		for (int i = 1 ; i < lines ; i++) {
			Decision d = new Decision(i, offset, STATE.YES);
			infer(d);
		//	states_[i][offset] = STATE.YES;
		//	yesPerColumn[offset]++;
		//	yesPerLine[i]++;
			if (i > 1 && (i % (radixPerColumn-1)) == 0) offset++;
		}
		offset = radixPerColumn;
		for (int i = radixPerLine ; i < columns ; i++) {
			Decision d = new Decision(offset, i, STATE.YES);
			infer(d);
			offset++;
			if (offset >= radixPerColumn + radixPerColumn - 1) offset = radixPerColumn;
		}
		offset = radixPerLine;
		for (int i = radixPerColumn*2 - 1 ; i < lines ; i++) {
			Decision d = new Decision(i, offset, STATE.YES);
			infer(d);
			offset++;
			if (offset >= radixPerLine + radixPerLine - 1) offset = radixPerLine;
		}		
		
		ArrayDeque<Decision> decisions = new ArrayDeque<Decision>();
		boolean backtrackPrev = false;
		do {
			int[] coord = findNextMaybe();
		//	System.out.println(Matrix.arrayToString(matchings));
			if (coord == null) {
				System.out.println("Decisions : " + decisionsNb);
				System.out.println("Wrong Decisions : " + wrongDecisions);
				updateToUser();
				return;
			}
			Decision d = new Decision(coord[0], coord[1], STATE.YES);
			System.out.println((decisions.size()+1) + " : " + d);
			boolean problemYes = !infer(d);
			if (problemYes) {
				wrongDecisions++;
				backtrack(d);
				d.setNO();
				System.out.println((decisions.size()+1) + " : " + d);
				boolean problemNo = !infer(d);
				if (problemNo) {
					wrongDecisions++;
					backtrack(d);
					backtrackPrev = true;
				} else {
					decisions.addLast(d);
				}
			} else {
				decisions.addLast(d);
			}
			while (backtrackPrev) {
				if (decisions.size() == 0) {
					return;
				}
				Decision prev = decisions.removeLast();
				backtrack(prev);
				if (prev.isYES()) {
					prev.setNO();
					System.out.println((decisions.size()+1) + " : " + prev);
					boolean problemNo = !infer(prev);
					if (problemNo) {
						wrongDecisions++;
						backtrack(prev);
					} else {
						decisions.addLast(prev);
						backtrackPrev = false;
					}
				}
			}
			
		}
		while (true);
		
		
	}
	

	
	
	


	// auxiliairy methods
	
	int[] findNextMaybe() {
		for (int i = 0 ; i < columns ;i++) {
			for (int j = 0 ; j < lines ; j++) {
				if (states_[i][j] == STATE.MAYBE) {
					return new int[]{j,i};
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		//new OrthogonalFatTreeMatching(4, 6, 3, 2).findMatching();
	//	new OrthogonalFatTreeMatching(6, 6, 3, 3).findMatching();	
	//	new OrthogonalFatTreeMatching(7, 7, 3, 3).findMatching();
	//	new OrthogonalFatTreeMatching(13, 13, 4, 4).findMatching();		
	//	new OrthogonalFatTreeMatching(21, 21, 5, 5).findMatching();	
		new OrthogonalFatTreeMatching(31, 31, 6, 6).findMatching();	
	//	new OrthogonalFatTreeMatching(43, 43, 7, 7).findMatching();
		
	}	

}
