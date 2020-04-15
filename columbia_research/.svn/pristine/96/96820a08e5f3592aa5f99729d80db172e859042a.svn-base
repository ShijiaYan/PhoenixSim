package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

public class WC_ReusePredicted_Receiver extends ReuseAwareReceiver {

	public WC_ReusePredicted_Receiver(int index, int nDest, int maxNumCircuits) {
		super(index, nDest, maxNumCircuits);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int findReplacement(
			Set<Integer> vacantCircuitSet) {
		int key = -1;
		int farthest = -1;
		double threshold = 0 /*(double)circuitUseCount/nDest/nBin*/;
		
		for (Integer d : vacantCircuitSet) {
			int s = csi[d].predictNextSrcUse_StackWithCredit(threshold, circuitUseCount);
			if (s > farthest) {
				key = d;
				farthest = s;
		    } /*else if (s == farthest && key != -1 ){
		    	if ( csi[d].lastSeenIndexInSrc != this.circuitUseCount && csi[d].getSrcReuseScore() < csi[key].getSrcReuseScore()){
		    		key = d;
		    	}
		    }*/
		}
		
		if (key < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return key;
	}

}
