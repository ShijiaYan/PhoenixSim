package edu.columbia.lrl.LWSim;

import ch.epfl.general_libraries.clazzes.ParamName;

public class WallClockTimeBasedEndCriterium extends SimulationEndCriterium {
	
	private long start;
	
	private double seconds;
	private double pointValue;
	private double currentPointValue;
	
	private int counter;
	
	public WallClockTimeBasedEndCriterium(@ParamName(name="Max simulation duration in seconds") double seconds) {
		this.seconds = seconds;
		this.pointValue = seconds/numberOfPoints;

	}
	
	public void simulationStarted(LWSIMExperiment exp) {
		super.simulationStarted(exp);
		start = System.currentTimeMillis();
		this.currentPointValue = pointValue;
	}

	@Override
	public int check(double timeSoFar) {
		counter++;
		if (counter % 100 == 0) {
			double diff = System.currentTimeMillis() - start;
			diff = diff/1000d;
			if (diff > seconds) {
				return -1;
			}
			if (diff > currentPointValue) {
				currentPointValue += pointValue;
				return 1;
			}
		}
		// TODO Auto-generated method stub
		return 0;
	}

}
