package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.Set;

public class LRU_Receiver extends ReuseAwareReceiver {

	public LRU_Receiver(int index, int nDest, int maxNumCircuits) {
		super(index, nDest, maxNumCircuits);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int findReplacement(
			Set<Integer> vacantCircuitSet) {
		int minKey = -1;
		int minValue = Integer.MAX_VALUE;
		for (Integer d : vacantCircuitSet) {
			if (csi[d].lastSeenIndexInSrc >= 0 && csi[d].lastSeenIndexInSrc < minValue) {
		        minKey = d;
		        minValue = csi[d].lastSeenIndexInSrc;
		    }
		}
		if (minKey < 0){
			throw new IllegalStateException("No vacant circuit found!");
		} else
			return minKey;
	}

}
