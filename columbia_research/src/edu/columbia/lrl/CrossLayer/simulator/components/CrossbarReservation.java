package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class CrossbarReservation extends AbstractReservationStrategy {

	//Input and output blocking 
	boolean[] inputBusy;
	boolean[] outputBusy;
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public String toString() {
		return "Crossbar reservation";
	}

	@Override
	public ArrayList<Integer> reservePath(int source, int dest) {
		inputBusy[source] = true;
		outputBusy[dest] = true;
		
		return null;
	}

	@Override
	public void releasePath(int source, int dest) {
		inputBusy[source] = false;
		outputBusy[dest] = false;
	}

	@Override
	public boolean pathAvailable(int source, int dest) {
		return !inputBusy[source] && !outputBusy[dest];
	}

	@Override
	public void initReservation() {
		//pre: first two dimensions of paths form a rectangular matrix
		inputBusy = new boolean[paths.length];
		outputBusy = new boolean[paths[0].length];
	}

	@Override
	public boolean hasTransitioningSwitch(ArrayList<Integer> transitioningSwitches, int source, int dest) {
		return false;
	}
}
