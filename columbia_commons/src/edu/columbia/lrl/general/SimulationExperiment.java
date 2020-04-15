package edu.columbia.lrl.general;

import edu.columbia.lrl.LWSim.SimulationEndCriterium;

public abstract class SimulationExperiment {
	
	public abstract void timeElapsed(double timeNS);
	/**
	 * If returns -1, simulation end. If return 1, a point is displayed. If 0, nothing happens
	 * @param timeNS
	 * @return
	 */
	public abstract int checkIfContinueSimulation(double timeNS);
	
	public abstract SimulationEndCriterium getEndCriterium();

}
