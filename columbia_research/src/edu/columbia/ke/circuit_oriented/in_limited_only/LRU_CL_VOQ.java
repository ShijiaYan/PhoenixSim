package edu.columbia.ke.circuit_oriented.in_limited_only;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class LRU_CL_VOQ extends InCircuitLimitedVOQ {
	
	public LRU_CL_VOQ() {
		super();
	}

	private LRU_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "Least Recently Used";
	}

	@Override
	public int findReplacement(int dest, Set<Integer> replaceCandidates) {
		int minKey = -1;
		int minValue = Integer.MAX_VALUE;
		for (Integer d : replaceCandidates) {
			if (/*csi.get(d).lastSeenIndexInSrc >= 0 &&*/ csi.get(d).lastSeenIndexInSrc < minValue) {
		        minKey = d;
		        minValue = csi.get(d).lastSeenIndexInSrc;
		    }
		}
		if (minKey < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return minKey;
	}

	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq) {
		return new LRU_CL_VOQ(parentID, nDestPerVoq, this.circuitSetupLatency,
				circuitPerVoq, maxVacantTime);
	}
}