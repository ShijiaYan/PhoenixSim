package edu.columbia.lrl.LWSim;

import ch.epfl.general_libraries.clazzes.ParamName;

public class SimulationTimeBasedEndCriterium extends SimulationEndCriterium {
	private double simTimeNS;
	private double pointValue;
	private double nextPointValue;
	private boolean point = true;
	
	public SimulationTimeBasedEndCriterium(@ParamName(name="Simulation time in ns", default_="100000") double simTimeNS) {
		this.simTimeNS = simTimeNS;
		if (simTimeNS < 0) {
			this.simTimeNS = Double.POSITIVE_INFINITY;
			point = false;
		}
		this.pointValue = simTimeNS/60d;
		this.nextPointValue = pointValue;
	}

	@Override
	public int check(double timeSoFar) {
		if (timeSoFar > simTimeNS) return -1;
		if (timeSoFar > nextPointValue && point) {
			nextPointValue += pointValue;
			return 1;
		}
		return 0;
	}
}
