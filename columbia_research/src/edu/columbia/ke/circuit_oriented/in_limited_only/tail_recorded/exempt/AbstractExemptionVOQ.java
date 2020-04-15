package edu.columbia.ke.circuit_oriented.in_limited_only.tail_recorded.exempt;

import java.util.Set;

import edu.columbia.ke.circuit_oriented.in_limited_only.tail_recorded.AbstractTailRecordedVOQ;

public abstract class AbstractExemptionVOQ extends AbstractTailRecordedVOQ {
	
	protected int exemptTailSize = -1;

	public AbstractExemptionVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits,
			double maxVacantTime, int tailLen) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime, tailLen);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Set<Integer> findReplaceCandidates(int dest) {
		
		Set<Integer> replaceCandidates = super.findReplaceCandidates(dest);
		
		if (exemptTailSize > 0) {
			
			Set<Integer> exemptSet = getTail(dest);
			
			if (!exemptSet.containsAll(replaceCandidates)) {
				replaceCandidates.removeAll(getTail(dest));
			}
		}
		
		return replaceCandidates;
	}

}
