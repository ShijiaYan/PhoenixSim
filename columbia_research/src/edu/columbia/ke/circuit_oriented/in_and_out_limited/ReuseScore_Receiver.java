package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

public class ReuseScore_Receiver extends ReuseAwareReceiver {

	public ReuseScore_Receiver(int index, int nDest, int maxNumCircuits) {
		super(index, nDest, maxNumCircuits);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int findReplacement(
			Set<Integer> vacantCircuitSet) {
		int minKey = -1;
		int lastSrc = -1;
		int minValue = Integer.MAX_VALUE;
		
		for (Integer d : vacantCircuitSet) {
			int s = csi[d].getSrcReuseScore();
			if (s < minValue) {
				if (s == 0 && csi[d].lastSeenIndexInSrc == this.circuitUseCount) {
					lastSrc = d;
					continue;
				}
		        minKey = d;
		        minValue = s;
		    }
		}
		
		if (minKey < 0 && vacantCircuitSet.size() == 1){
			minKey = lastSrc;
		}
		
		if (minKey < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return minKey;
	}

}
