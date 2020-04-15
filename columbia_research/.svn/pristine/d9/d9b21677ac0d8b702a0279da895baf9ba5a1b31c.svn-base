package edu.columbia.ke.circuit_oriented.in_limited_only.tail_recorded.prefetch;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import edu.columbia.ke.DataStructure.CircuitReuseInfo;
import edu.columbia.ke.circuit_oriented.in_limited_only.tail_recorded.AbstractTailRecordedVOQ;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public abstract class AbstractPrefetchVOQ extends AbstractTailRecordedVOQ {
	
	private enum PrefetchResult {
		HIT, ADD, REPLACE, RPL_REJECT, ALL_BUSY
	}
	
	protected Queue<Integer> protectedPrefetched = new LinkedList<Integer>();
	protected int protectedDistance;
	protected int protectedPrefetchedLen;
	
	public AbstractPrefetchVOQ(int tailLen) {
		super(tailLen);
		this.protectedDistance = tailLen;
	}	

	public AbstractPrefetchVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits,
			double maxVacantTime, int tailLen) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime, tailLen);
		writeName = "prefetch";
		
		protectedPrefetchedLen = protectedDistance * tailLen;
		for (int i = 0; i < protectedPrefetchedLen; i++) {
			protectedPrefetched.add(-1);
		}
	}

	@Override
	protected Evt forwardEvt(double time) {

		Evt nextInLine = super.forwardEvt(time);

		Message msg = nextInLine.getMessage();
		int dest = msg.dest;
		
		prefetchAfterDest(dest, time);

		return nextInLine;
	}


	/* (non-Javadoc)
	 * @see edu.columbia.ke.component.AbstractCircuitLimitedVOQ#handleCircuitHit(int, double)
	 */
	@Override
	protected void handleCircuitHit(int msgDest, double now) {
		
		super.handleCircuitHit(msgDest, now);
		
		if (!(coalescMode && lastDest == msgDest )) {
			
			// check if circuit is prefetched
			CircuitReuseInfo c = csi.get(msgDest);
			if (c.isPrefetched()) {
				
				boolean withinTail =  (circuitUseCount - c.getPretetchedTime() <= tailLen);
				lwSimExperiment.logPrefetchHit(true, withinTail);
				
				c.resetPrefetch();
			} 
				
		}
	}

	/* (non-Javadoc)
	 * @see edu.columbia.ke.component.AbstractCircuitLimitedVOQ#removeFromCacheSet(int, double)
	 */
	@Override
	protected void removeFromCacheSet(int dest, double time) {
		CircuitReuseInfo c = csi.get(dest);
		c.resetPrefetch();
		super.removeFromCacheSet(dest, time);
	}


	private void prefetchAfterDest(int dest, double time) {
		
		Set<Integer> tail = getTail(dest);
		Set<Integer> success = new TreeSet<Integer>();
		
		String s = "Dst " + dest;
		s += "\t State: " + getInCacheDest().toString();
		s += "\t Tail: " + tail.toString();
		
		tail.removeAll(getInCacheDest());
		
		s += "\t Prefetch: [" /*+ tail.toString()*/;
		
		for (Integer t : tail) {
			PrefetchResult rv = prefetch(t, dest, time);
			s += t + "-" + rv.toString() + ", ";
			if (rv == PrefetchResult.ADD || rv == PrefetchResult.REPLACE) {
				success.add(t);
			}
		}
		
		s += "]";
		s += "\t Success: " + success.toString();
		
		// update protected prefetched circuit
		for (int i = 0; i < tailLen; i++) {
			protectedPrefetched.poll();
		}
		for (int i = 0; i < tailLen - success.size(); i++) {
			protectedPrefetched.add(-1);
		}
		protectedPrefetched.addAll(success);
		
		if (index == trackingNode) {
			logMyRank(s);
		}	
	}

	protected PrefetchResult prefetch(int dstToFetch, int dstInService, double time) {
		
		PrefetchResult rv;
		
		if (isCircuitHit(dstToFetch)){			
			rv = PrefetchResult.HIT;
		} 
		else if (!isCacheFull()) {
			handleCacheAvailable(dstToFetch, time);
			rv = PrefetchResult.ADD;
		}
		else if (this.isCacheFullButWithVacant()) { // missed, but there are some vacant circuits; need replacement
			if (handlePrefetchReplace(dstToFetch, dstInService, time) < 0) {
				rv = PrefetchResult.RPL_REJECT;
			} else {
				rv = PrefetchResult.REPLACE;
			}
		}
		else {	
			handleNoVacant(dstToFetch, time);
			rv = PrefetchResult.ALL_BUSY;
		}
		
		if (rv == PrefetchResult.ADD || rv == PrefetchResult.REPLACE) {
			
			// set the circuit state as vacant
			addInVacantSet(dstToFetch, time);
			
			// mark the circuit as prefetched
			csi.get(dstToFetch).setPrefetch(circuitUseCount);
			
			// log in lwExp
			lwSimExperiment.logPrefetch();
			
			// update protected prefetch set
			/* well, seems the parent method has done this..
			if (protectedPrefetched.size() == protectedPrefetchedLen) {
				protectedPrefetched.poll();
			}
			protectedPrefetched.add(dstToFetch);
			*/
		}
		
		return rv;
		
	}
	
	protected int handlePrefetchReplace(int dstToFetch, int dstInService, double now){
		
		Set<Integer> candidates = findPrefetchReplaceCandidates(dstInService);
		
		if (candidates.isEmpty()) {
			return -1;
		}
		
		int toBeReplaced = findReplacement(dstToFetch, candidates);

		replaceCircuit(toBeReplaced, now);
		
		/*
		 * The new circuit is assumed to be busy right after being brought into the cache
		 * so I didn't add it into the vacant set
		 */
		trySetupNewCircuit(dstToFetch, now);
		
		return toBeReplaced;
	}

	protected Set<Integer> findPrefetchReplaceCandidates(int dstInService) {
		Set<Integer> replaceCandidates = findReplaceCandidates(dstInService);
		replaceCandidates.removeAll(getTail(dstInService));
		return replaceCandidates;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.ke.component.AbstractCircuitLimitedVOQ#findReplaceCandidates(int)
	 */
	@Override
	protected Set<Integer> findReplaceCandidates(int dest) {
		
		Set<Integer> replaceCandidates = super.findReplaceCandidates(dest);
		
		if (!protectedPrefetched.containsAll(replaceCandidates)) {
			replaceCandidates.removeAll(protectedPrefetched);
		}
		
		return replaceCandidates;
	}
	
	

}
