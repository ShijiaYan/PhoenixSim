package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class LRU_BI_CL_VOQ extends AbstractDstTerminateVOQ {

	public LRU_BI_CL_VOQ() {
		super();
	}
	
	private LRU_BI_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "LRU_CL_VOQ";
	}
	
	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq) {
		return new LRU_BI_CL_VOQ(parentID, nDestPerVoq,
				this.circuitSetupLatency, circuitPerVoq, this.maxVacantTime);
	}

	@Override
	public ReuseAwareReceiver getAssociatedReceiver(int nClient, int circuitPerNode) {
		return new LRU_Receiver(-1, nClient, circuitPerNode);
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


}
