package edu.columbia.ke.circuit_oriented.in_limited_only;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class NC_ReusePredicted_CL_VOQ extends InCircuitLimitedVOQ {

	public NC_ReusePredicted_CL_VOQ() {
		super();
	}
	
	private NC_ReusePredicted_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "NC_ReusePredicted_CL_VOQ";
	}

	@Override
	public int findReplacement(int dest, Set<Integer> replaceCandidates) {
		int key = -1;
		int farthest = 0;
		
		for (Integer d : replaceCandidates) {
			int nextUse = csi.get(d).predictedNextSrcUse_NoCredit();
			if (nextUse >= farthest) {
				key = d;
				farthest = nextUse;
		    }
		}
		
		if (key < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return key;
	}

	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq) {
		return new NC_ReusePredicted_CL_VOQ(parentID, nDestPerVoq,
				this.circuitSetupLatency, circuitPerVoq, maxVacantTime);
	}

}
