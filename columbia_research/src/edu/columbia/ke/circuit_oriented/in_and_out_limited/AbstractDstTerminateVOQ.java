package edu.columbia.ke.circuit_oriented.in_and_out_limited;

import edu.columbia.ke.circuit_oriented.AbstractCircuitLimitedVOQ;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Evt;

public abstract class AbstractDstTerminateVOQ extends AbstractCircuitLimitedVOQ {

	private Receiver[] recv;
	private boolean[] requested;

	private double reqDelay = 0.1;
	
	private int pendingReq;
	
	protected AbstractDstTerminateVOQ() {
		super();
	}
	
	protected AbstractDstTerminateVOQ(int index, int nDest,
			double circuitSetupLatency, int maxNumCircuits,
			double maxVacantTime) {
		super(index, nDest, circuitSetupLatency, maxNumCircuits, maxVacantTime);
		requested = new boolean[nDest];
	}	

	public void dstTearDownCircuit(int dest, double time) {
		this.removeFromCacheSet(dest, time);
		/*
		 * Maybe do not need to bootStrap here
		 */
		//bootStrap(time);
	}

	protected void handleCircuitGrant(Evt e){
		int dest = ((ReuseAwareReceiver) e.getOrigin()).getIndex();
		requested[dest] = false;
		pendingReq--;
		allowedSetupNewCircuit(dest, e.getTimeNS());
	}

	/* 
	 * notify receiver of Removal
	 * must be atomic
	 */
	private void notifyRecvOfRemoval(int dest, double time) {
		((ReuseAwareReceiver)recv[dest]).notifiedOfTearDown(this, time);	
	}
	
	@Override
	public void processEvent(Evt e) {
		String type = e.getsType();
		
		if (type!=null && type.equals("CIRCUIT_GRANT")){
			handleCircuitGrant(e);
		} else{
			super.processEvent(e);
		}
	}

	@Override
	protected void recordCircuitUse(int dest, double time, int size) {
		super.recordCircuitUse(dest, time, size);
		((ReuseAwareReceiver)recv[dest]).notifiedOfUse(index, time, size);	
	}
	@Override
	protected void removeFromCacheSet(int dest, double time) {
		super.removeFromCacheSet(dest, time);
		notifyRecvOfRemoval(dest, time);
	}
	
	

	@Override
	public boolean bufReportVacantAfterBusy(int dest, double time) {
		boolean keepVacant = super.bufReportVacantAfterBusy(dest, time);
		if (keepVacant){
			((ReuseAwareReceiver)recv[dest]).notifiedOfVacantEvent(this, time);
		}
		/*
		 * if not keep vacant, the circuit is removed;
		 * then notifyRecvOfRemoval is called;
		 * so the destination can also accept new circuits
		 */
		return keepVacant;
	}

	public void setRecv(Receiver[] recv){
		this.recv = recv;
	}

	@Override
	protected void trySetupNewCircuit(int msgDest, double now) {
		if (requested[msgDest])
			return;
		Evt req = new Evt(now + reqDelay, this, recv[msgDest], "CIRCUIT_SETUP_REQ");
		lwSimExperiment.manager.queueEvent(req);
		requested[msgDest] = true;
		pendingReq++;
	}

	@Override
	public boolean isCacheFull() {
		return this.getCacheSize() + pendingReq >= this.maxNumCircuits;
	}

	
	
	

}
