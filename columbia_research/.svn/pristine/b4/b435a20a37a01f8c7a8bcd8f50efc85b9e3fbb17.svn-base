package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.ArrayList;
import java.util.Map;

import edu.columbia.lrl.LWSim.LWSIMExperiment;

public abstract class AbstractReservationStrategy {
	
	public abstract Map<String, String> getAllParameters();
	public abstract String toString();
	
	public abstract ArrayList<Integer> reservePath(int source, int dest);
	public abstract void releasePath(int source, int dest);
	public abstract boolean pathAvailable(int source, int dest);
	public abstract void initReservation();	
	public abstract boolean hasTransitioningSwitch(ArrayList<Integer> transitioningSwitches, int source, int dest);
	public int[] getReservedSwitchPath(int source, int dest) { return null; }
	
	int paths[][][][];
//	int switches[][][][];
//	int switchIDs[][][][];
	LWSIMExperiment experiment;
	
	public void init(int paths[][][][], /*int switches[][][][], int switchIDs[][][][],*/ LWSIMExperiment experiment) {
		this.paths = paths;
//		this.switches = switches;
//		this.switchIDs = switchIDs;
		this.experiment = experiment;		

		//printPaths(switchIDs);
		
		initReservation();
	}
	
	public void printPaths(int[][][][] paths) {		
		for( int source = 0; source < paths.length; source++ ) {
			for( int dest = 0; dest < paths[source].length; dest++ ) {
				
				System.out.print("[" + source + ", " + dest + "] : ");
				
				for( int pathID = 0; pathID < paths[source][dest].length; pathID++ ) {
					
					System.out.print( pathID + "->[");
					for( int link = 0; link < paths[source][dest][pathID].length; link++ ) {
						System.out.print(paths[source][dest][pathID][link]);
						if( link != paths[source][dest][pathID].length -1) {
							System.out.print(", ");
						}
					}
					System.out.print("] ");
				}
				
				System.out.println();
			}
		}
	}
}
