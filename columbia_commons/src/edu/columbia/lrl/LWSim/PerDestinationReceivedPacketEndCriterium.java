package edu.columbia.lrl.LWSim;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;

public class PerDestinationReceivedPacketEndCriterium extends
		SimulationEndCriterium {

	protected LWSIMExperiment exp;
	
	private int nbPackets;
	private double pointValue;
	private double currentPointValue;
	
	private int counter = 0;
	
	public PerDestinationReceivedPacketEndCriterium(@ParamName(name="Number of packets", default_="100000") int nbPackets) {
		this.nbPackets = nbPackets;
		this.pointValue = (double)nbPackets/(double)numberOfPoints;
	}
	
	public void simulationStarted(LWSIMExperiment exp) {
		this.exp = exp;
		this.currentPointValue = pointValue;
	}

	@Override
	public int check(double timeSoFar) {
		if (counter % 50 == 0) {
			int min = MoreArrays.min(exp.perDestReceived);
			if (min > nbPackets) return -1;
			if (min > currentPointValue) {
				currentPointValue += pointValue;
				return 1;
			}
		}
		return 0;
	}
	
}
