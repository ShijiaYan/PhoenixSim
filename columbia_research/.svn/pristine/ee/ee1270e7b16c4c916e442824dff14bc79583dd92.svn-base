package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.SimpleMap;

public class NextPathReservation extends AbstractReservationStrategy {

	//Stores which links and paths (for tear down) are reserved
	Map<Integer, Boolean> reservedLinks;
	Map<Integer, Integer> switchStates;
	int reservedPaths[][];	
	
	//Input and output blocking (TODO: can probably combine some data structures to save on space)
	boolean inputBusy[];
	boolean outputBusy[];

	public void initReservation() {		
		reservedLinks = new TreeMap<Integer, Boolean>();
		switchStates = new TreeMap<Integer, Integer>();
		//Pre: First two dimensions of paths form a rectangular matrix
		reservedPaths = new int[paths.length][paths[0].length];
		
		inputBusy = new boolean[paths.length];
		outputBusy = new boolean[paths[0].length];
		
		//Initialize reserved paths for error checking
		for( int source = 0; source < reservedPaths.length; source++ ) {
			for( int dest = 0; dest < reservedPaths[source].length; dest++ ) {
				reservedPaths[source][dest] = -1;
			}
		}
	}
	
	@Override
	public int[] getReservedSwitchPath(int source, int dest) {
		if( reservedPaths[source][dest] == -1 ) {
			return null;
		}
		return null;
//		return switchIDs[source][dest][reservedPaths[source][dest]];
	}
	
	private int findNextFreePath(int source, int dest) {
		int freePath = -1;
		for(int path = 0; path < paths[source][dest].length; path++) {
			//Return first path in which all paths are free
			boolean pathFree = true;
			for( int link = 0; link < paths[source][dest][path].length; link++) {
				int id = paths[source][dest][path][link];
				if( reservedLinks.get(id) != null && reservedLinks.get(id) ) {
					pathFree = false;
					break;
				}
			}
			
			if( pathFree ) {
				freePath = path;
				break;
			}
		}		
		
		return freePath;
	}
	
	
	public boolean pathAvailable(int source, int dest) {
		
		//check for input/output blocking
		if( inputBusy[source] || outputBusy[dest] )
			return false;
		
		//Check if there are any paths available
		return (findNextFreePath(source, dest) >= 0);
	}
	
	public ArrayList<Integer> reservePath(int source, int dest) {
		
		if( reservedPaths[source][dest] != -1 ) {
			throw new SimulationException("Trying to reserve a path for that is already reserved! (" + source + "->" + dest + ")");
		}
		
		//Get next free path
		int freePath = findNextFreePath(source, dest);
		
		if( freePath == -1 ) {
			throw new SimulationException("Could not reserve path ("+ source + "->" + dest + ")");
		}
		
		//Reserve links
		for( int link = 0; link < paths[source][dest][freePath].length; link++ ) {
			int id = paths[source][dest][freePath][link];
			if( reservedLinks.get(id) != null && reservedLinks.get(id) ) {
				throw new SimulationException("Trying to reserve link that is already reserved!\n" +
						source + " " + dest + " " + freePath + " " + id);
			}
			reservedLinks.put(id, true);
		}
				
		//Change switch state if needed
		ArrayList<Integer> transitions = new ArrayList<Integer>();
/*		for( int sw = 0; sw < switches[source][dest][freePath].length; sw++ ) {
			int requiredState = switches[source][dest][freePath][sw];
			int switchID = switchIDs[source][dest][freePath][sw];
			
			if( !switchStates.containsKey(switchID) ) {
				switchStates.put(switchID, requiredState);
			} 
			
			int currentState = switchStates.get(switchID);
			
			if( currentState != requiredState ) {
				transitions.add(switchID);
				switchStates.put(switchID, requiredState);
			}
		}*/

		reservedPaths[source][dest] = freePath;
		inputBusy[source] = true;
		outputBusy[dest] = true;
		
		//Let arbiter know if extra time is needed for state change
		return transitions;
	}

	public void releasePath(int source, int dest) {

		//Check that path is actually reserved
		int path = reservedPaths[source][dest];
		if( path == -1) {
			throw new SimulationException("Trying to release path (" + source + "->" + dest + ") that is not reserved");
		}
		
		//Free all links
		for( int link = 0; link < paths[source][dest][path].length; link++ ) {
			int id = paths[source][dest][path][link];
			if( reservedLinks.get(id) == null || !reservedLinks.get(id) ) {
				throw new SimulationException("Trying to release link that isn't reserved!\n" +
						source + " " + dest + " " + path + " " + id);
			} 
			reservedLinks.put(id, false);
		}
		
		//Mark path as released
		reservedPaths[source][dest] = -1;
		inputBusy[source] = false;
		outputBusy[dest] = false;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public String toString() {
		return "NextPathReservation";
	}

	@Override
	public boolean hasTransitioningSwitch(ArrayList<Integer> transitioningSwitches, int source, int dest) {
		if( reservedPaths[source][dest] == -1 ) {
			throw new SimulationException("Checking for transitioning switch for unreserved path (" + source + ", " + dest + ")");
		}
		// int[] switchPath = switchIDs[source][dest][reservedPaths[source][dest]];
	//	int[] switchPath = null;
		//TODO: Figure out java way to do this efficiently
	/*	for( int switchID : switchPath ) {
			if( transitioningSwitches.contains(switchID)) {
				return true;
			}
		}*/
		
		return false;
	}
}
