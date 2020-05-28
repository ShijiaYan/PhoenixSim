package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import edu.columbia.ke.DataStructure.CircuitReuseInfo;
import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;
import edu.columbia.ke.generic.dataStucture.AvlTreeRm;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public abstract class ReuseAwareReceiver extends Receiver {
	
	private int maxNumCircuits;
	private int nDest;
	private boolean coalescMode = false;
	protected Set<Integer> inCacheCircuitSet = new TreeSet<>();
	
	protected CircuitReuseInfo[] csi;
	protected int circuitUseCount = 0;
	private int lastSrc = -1;
	
	private LinkedList<Evt> reqWaitList = new LinkedList<>();
	private AbstractCircuitLimitedVOQ[] voq;

	private AvlTreeRm avlTree = new AvlTreeRm();
	
	public ReuseAwareReceiver(int index, int nDest, int maxNumCircuits) {
		super(index);
		this.maxNumCircuits = maxNumCircuits;
		this.csi = new CircuitReuseInfo[nDest];
		this.nDest = nDest;
		for (int i = 0; i < nDest; i++) 
			csi[i] = new CircuitReuseInfo(-1);
	}
	
	// may be move this into a dedicated class that makes copies
	public Receiver getReceiverCopy(int index) {
		try {
			return this.getClass().getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(index, nDest, maxNumCircuits);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void setVOQ(AbstractCircuitLimitedVOQ[] voq2){
		this.voq = voq2;
	}

	@Override
	public void processEvent(Evt e) {
		String s = e.getsType();
		if (s!=null && s.equals("CIRCUIT_SETUP_REQ")) {
			handleCircuitReq(e);
			return;
		} else {
			super.processEvent(e);
			Message m = e.getMessage();

			/*
			 * Take care of the reuse part/circuit management
			 */
			//recordReuse(m.origin, e.getTimeNS(), m.sizeInBits / 8);
		}
	}
	
	private void handleVacantEvt(int src, double grantTime) {
		if (reqWaitList.isEmpty())
			return;
		
		// tear down old circuit
		tearDownCircuit(src, grantTime);
		
		/*
		 * tearDownCircuit will also cause the processing of reqWaitList
		 */
	}

	private void tearDownCircuit(int src, double grantTime) {
		((AbstractDstTerminateVOQ) voq[src]).dstTearDownCircuit(index, grantTime);
		removeFromCacheSet(src);
	}

	private void handleCircuitReq(Evt e) {
		double now = e.getTimeNS();
		if (!isCacheFull()){			// empty slot in cache
			grantCircuitReq(e, now);
		} else {
			int r = findVacantCircuit();
			if (r == -1)				// no vacant found
				enqueueCircuitReq(e);
			else {						// vacant found
				tearDownCircuit(r, now);
				grantCircuitReq(e, now);
			}
		}
	}

	private int findVacantCircuit() {
		Set<Integer> vacantCircuitSet = new TreeSet<>();
		for (int s: inCacheCircuitSet){
			AbstractDstTerminateVOQ src = (AbstractDstTerminateVOQ) voq[s];
			if (!src.isCircuitHit(index)){	// not in source cache anymore
				inCacheCircuitSet.remove(s);
				return s;
			} else if (src.isCircuitVacant(index)){
				vacantCircuitSet.add(s);
			}
		}
		
		if (vacantCircuitSet.isEmpty())
			return -1;
		
		return findReplacement(vacantCircuitSet);
	}
	
	public abstract int findReplacement(Set<Integer> vacantCircuitSet);

	private void enqueueCircuitReq(Evt e) {
		if (e == null)
			throw new IllegalStateException();
		reqWaitList.offer(e);		
	}

	private double grantDelay = 0.1;
	private void grantCircuitReq(Evt e, double grantTime) {
		if (e == null)
			throw new IllegalStateException();
		AbstractDstTerminateVOQ src = (AbstractDstTerminateVOQ) e.getOrigin();
		Evt grant = new Evt(grantTime + grantDelay, this, src, "CIRCUIT_GRANT");
		lwSimExperiment.manager.queueEvent(grant);
		addInCacheSet(src.index);
	}
	
	public void notifiedOfVacantEvent(AbstractDstTerminateVOQ src, double time){
		handleVacantEvt(src.index, time);
	}
	
	public void notifiedOfTearDown(
			AbstractDstTerminateVOQ src, double time) {
		removeFromCacheSet(src.index);
		
		if (reqWaitList.isEmpty())
			return;
	
		// grant new circuit
		grantCircuitReq(reqWaitList.poll(), time);
		
	}
	
	/*
	 * %%%%%%%%%%%%%%%%%%%%%% BELOW THE SAME AS THE SOURCE SIDE %%%%%%%%%%%%%%%%%%%%%%%
	 */
	
	private void addInCacheSet(int src){
		this.inCacheCircuitSet.add(src);
	}
	
	protected void removeFromCacheSet(int src){
		this.inCacheCircuitSet.remove(src);
	}
	
	public boolean isCacheFull(){
		return this.inCacheCircuitSet.size() >= this.maxNumCircuits;
	}

	public void notifiedOfUse(int src, double time, int size){
		if (!(coalescMode && src == lastSrc  )) {
			circuitUseCount++;
			
			// find unqiue distance
	//		int uniqueDistance = findUniqueDistance(src);

			if (csi[src].addSrcUseInstance(size, time, this.circuitUseCount)){
				//lwSimExperiment.logAccuratePredict();
			}
			lastSrc = src;
			
			// insert this use into the tree
			avlTree.insert(circuitUseCount);
		}
	}

	
	/*
	 * return how many uses after lastSeen
	 * return -1 if first time seeing this dest
	 */
	private int findUniqueDistance(int src) {
		int lastSeen = csi[src].lastSeenIndexInSrc;
		if (lastSeen < 0)
			return -1;		// no reuse distance yet

		int rv = avlTree.removeAndRank(lastSeen);
		if (rv < 0)
			throw new IllegalStateException("cannot find key!");
		return rv;
	}



}
