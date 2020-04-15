package edu.columbia.ke.circuit_oriented.in_limited_only;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;
import edu.columbia.lrl.LWSim.components.Receiver;

public abstract class InCircuitLimitedVOQ extends AbstractCircuitLimitedVOQ {

	public InCircuitLimitedVOQ() {
		super();
	}	

	public InCircuitLimitedVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
	}
	
	@Override
	public Receiver getAssociatedReceiver(int nClient, int circuitPerNode) {
		return new Receiver(-1);
	}	
	
}
