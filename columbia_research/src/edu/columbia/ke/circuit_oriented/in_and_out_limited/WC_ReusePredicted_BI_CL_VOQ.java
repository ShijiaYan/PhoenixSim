package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;

public class WC_ReusePredicted_BI_CL_VOQ extends AbstractDstTerminateVOQ {

	public WC_ReusePredicted_BI_CL_VOQ() {
		super();
	}
	
	private WC_ReusePredicted_BI_CL_VOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits, double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		voqName = "WC_ReusePredicted_CL_VOQ";
	}
	
	private int nBin = 3;

	@Override
	public int findReplacement(int dest, Set<Integer> replaceCandidates) {
		int key = -1;
		int farthest = -1;
		double threshold = 0 /*(double)circuitUseCount/nDest/nBin*/;
		
		for (Integer d : replaceCandidates) {
			int s = csi.get(d).predictNextSrcUse_StackWithCredit(threshold, circuitUseCount);
			if (s > farthest) {
				key = d;
				farthest = s;
		    } else if (s == farthest && key != -1 ){
		    	if ( csi.get(d).getSrcReuseScore() < csi.get(key).getSrcReuseScore()){
		    		key = d;
		    	}
		    }
		}
		
		if (key < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return key;
	}

	@Override
	public ReuseAwareReceiver getAssociatedReceiver(int nClient, int circuitPerNode) {
		return new WC_ReusePredicted_Receiver(-1, nClient, circuitPerNode);
	}

	@Override
	public AbstractCircuitLimitedVOQ getCopy(int parentID, int nDestPerVoq, int circuitPerVoq) {
		return new WC_ReusePredicted_BI_CL_VOQ(parentID, nDestPerVoq,
				this.circuitSetupLatency, circuitPerVoq, maxVacantTime);
	}

}
