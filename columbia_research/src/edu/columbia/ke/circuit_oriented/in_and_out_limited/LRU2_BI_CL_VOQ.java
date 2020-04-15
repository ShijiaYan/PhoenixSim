package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class LRU2_BI_CL_VOQ extends AbstractDstTerminateVOQ {

	public LRU2_BI_CL_VOQ() {
		super();
	}
	
	private LRU2_BI_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "LRU2_CL_VOQ";
	}

	@Override
	public int findReplacement(int dest, Set<Integer> replaceCandidates) {
		int minKey = -1;
		int minValue = Integer.MAX_VALUE;
		for (Integer d : replaceCandidates) {
			if (csi.get(d).lastSeenIndexInSrc2 < minValue) {
		        minKey = d;
		        minValue = csi.get(d).lastSeenIndexInSrc2;
		    }
		}
		if (minKey < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return minKey;
	}

	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq) {
		return new LRU2_BI_CL_VOQ(parentID, nDestPerVoq,
				this.circuitSetupLatency, circuitPerVoq, maxVacantTime);
	}

	@Override
	public ReuseAwareReceiver getAssociatedReceiver(int nClient, int circuitPerNode) {
		return new LRU_Receiver(-1, nClient, circuitPerNode);
	}
}
