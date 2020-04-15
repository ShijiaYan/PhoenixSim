package edu.columbia.lrl.LWSim;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class SimulationEndCriterium {

	
	protected static int numberOfPoints = 50;

	/**
	 * Returning negative number means criterium reached, positive number means progression, 0 means nothing
	 * @param timeSoFar
	 * @return
	 */
	public abstract int check(double timeSoFar);
	
	/** This method has not implementation by default but can be overidden to obtain the experiment pointer or
	 * simply be told that simulation is about to be launched
	 * @param exp
	 */
	public void simulationStarted(LWSIMExperiment exp) {}

	public Map<String,String> getAllParameters() {
		return SimpleMap.getMap("End criterion", this.getClass().getSimpleName());
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}
}
