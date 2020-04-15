package edu.columbia.ke.circuit_oriented.in_limited_only;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class ReuseScore_CL_VOQ extends InCircuitLimitedVOQ {

	public ReuseScore_CL_VOQ() {
		super();
	}
	
	private ReuseScore_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "Minimum Reuse Score";
	}

	@Override
	public int findReplacement(int dest, Set<Integer> replaceCandidates) {
		int minKey = -1;
		int lastDest = -1;
		int minValue = Integer.MAX_VALUE;
		
		for (Integer d : replaceCandidates) {
			int s = csi.get(d).getSrcReuseScore();
			if (s < minValue) {
				if (s == 0 && csi.get(d).lastSeenIndexInSrc == this.circuitUseCount) {
					lastDest = d;
					continue;
				}
		        minKey = d;
		        minValue = s;
		    }
		}
		
		if (minKey < 0 && replaceCandidates.size() == 1){
			minKey = lastDest;
		}
		
		if (minKey < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return minKey;
	}

	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq,
			int circuitPerVoq) {
		return new ReuseScore_CL_VOQ(parentID, nDestPerVoq,
				this.circuitSetupLatency, circuitPerVoq, maxVacantTime);
	}


}