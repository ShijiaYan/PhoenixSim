package edu.columbia.ke.circuit_oriented;

import java.util.LinkedList;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class CircuitAvailLimitedBuffer extends Buffer {
	
	/*
	 * note: index has no meaning in this class
	 */

	private int src;
	private int myDest;
	private AbstractCircuitLimitedVOQ parentVOQ;
	private double lastEndTime;
	private int trackDstIndex = -1;
	private int trackSrcIndex = -1;
	private LinkedList<Evt> queue;
	
	// states
	private boolean circuitAvail = false;
	private boolean beingSetup = false;
	private boolean fwdLocked = false;

	public CircuitAvailLimitedBuffer(int maxSize, double bufferLatency,
			int index, double outputRateCoeff, boolean usePriorities, int src, int dest, AbstractCircuitLimitedVOQ parentVOQ) {
		super(maxSize, bufferLatency, index, outputRateCoeff, usePriorities);
		this.src = src;
		this.myDest = dest;
		this.parentVOQ = parentVOQ;
		this.queue = new LinkedList<Evt>();
	}

	public CircuitAvailLimitedBuffer(int maxSize, double bufferLatency,
			int index, int src, int dest, AbstractCircuitLimitedVOQ parentVOQ) {
		this(maxSize, bufferLatency, index, 1, false, src, dest, parentVOQ);
		this.lastPacketStart = -1;
	}
	
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback fb = super.initComponent(lwSimExperiment);
		if (timeline != null) {
			timeline.setGroup("VOQ");
			timeline.setTitle(src + "-" + myDest);
		}
		return fb;
	}
	
	@Override
	public void processProducerEvent(Evt e) {
		size++;
		enqueue(e);
		unlockFwd();
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		if (isTransmitting()) {
			return;
		}
		
		if (!isCircuitAvail()) {
			return;
		}
		
		Evt nextInLine = pollEvt();
		double t = e.getTimeNS();
		if (nextInLine != null) {
			if (src == trackSrcIndex && myDest == trackDstIndex) {
				String s = myDest + "\t" + nextInLine.getMessage().index + "\t" + "transmit\t" + e.getTimeNS();
				lwSimExperiment.logThread(s, "buf_" + myDest);
			}
			// added; report busy state
			this.parentVOQ.bufReportBusy(myDest, e.getTimeNS());
			
			// let buffer transmit
			transmit(t, nextInLine.getMessage());
		}
		
		/*
		 * calling super.processConsumerEvent to make sure queue is flushed
		 * then report to parent voq
		 * use message.origin to note this buffer
		 */
		
		// directly call function
		if (!this.transmitting && !this.isFwdLocked() && e.getTimeNS() == lastEndTime) {
			if (src == trackSrcIndex && myDest == trackDstIndex) {
				String s = myDest + "\t" + "vacant\t" + e.getTimeNS();
				lwSimExperiment.logThread(s, "buf_" + myDest);
			}
			AssertBufferEmpty();
			this.parentVOQ.bufReportVacantAfterBusy(myDest, e.getTimeNS());
		}
	}
	
	private void AssertBufferEmpty() {
		if (this.getSize() != 0) {
			throw new IllegalStateException("Buffer is not empty");
		}
	}

	@Override
	public void processEvent(Evt e) {
		if (e.getsType() == null) {
			if (e.getType() == 0) {
				processProducerEvent(e);
				if (src == trackSrcIndex && myDest == trackDstIndex) {
					String s = myDest + "\t" + e.getMessage().index + "\t" + "enqueue\t" + e.getTimeNS();
					lwSimExperiment.logThread(s, "buf_" + myDest);
				}
			} else
				throw new IllegalStateException("Not expecting other types of events");
		} else {
			String sType = e.getsType();
			if (sType.equals("CIRCUIT_AVAILABLE")) {
				handleCircuitAvailable(e);
			} else if (sType.equals("CIRCUIT_REPLACED")) {
				handleCircuitReplaced(e);
			} else if (sType.equals("CIRCUIT_SETUP")) {
				handleCircuitSetup(e);
			} else if (sType.equals("EOT")) {
				handleEOT(e);
			} else {
				throw new IllegalStateException("Not expecting this type of event: " + e.getsType());
			}
		}
		processConsumerEvent(e);
	}
	
	private void handleCircuitSetup(Evt e) {
		// TODO Auto-generated method stub
		
	}

	private void handleCircuitReplaced(Evt e) {
		// TODO Auto-generated method stub
		
	}

	private void handleEOT(Evt e) {
		if (src == trackSrcIndex && myDest == trackDstIndex) {
			String s = myDest + "\t" + e.getMessage().index + "\t" + "EOT\t" + e.getTimeNS() + "\t" + lastEndTime;
			lwSimExperiment.logThread(s, "buf_" + myDest);
		}
		if (e.getTimeNS() != lastEndTime)
			throw new IllegalStateException("EOT not arriving at lastEndTime");
		this.transmitting = false;		
	}

	private void handleCircuitAvailable(Evt e) {
		if (circuitAvail) {
			// throw new IllegalStateException("Already available");
		}
		
		if (!beingSetup) {
			// this circuit has been replaced before being setup finishes
			return;
		}
		
		this.setCircuitAvail(true);
		beingSetup = false;
		
		if (src == trackSrcIndex && myDest == trackDstIndex) {
			String s = myDest + "\t" + "set_available\t" + e.getTimeNS();
			lwSimExperiment.logThread(s, "buf_" + myDest);
		}
		
		/*
		// if no event in queue for this buffer, report vacant state
		if (queue.isEmpty()) {
			parentVOQ.reportVacantState(myDest, e.getTimeNS());
			return;
		}
		*/	
	}

	@Override
	protected void scheduleEOTEvt(double startTimeNS, Message msg) {
		lastEndTime = getEOTTimeNS(startTimeNS, msg);
		Evt self = new Evt(lastEndTime, this, this, "EOT", msg);
		lwSimExperiment.manager.queueEvent(self);		
	}
	
	public void handleCircuitSetup() {
		if (beingSetup) {
			throw new IllegalStateException("Already being set up");
		}
		if (circuitAvail) {
			throw new IllegalStateException("Already available");
		}
		beingSetup = true;
		setCircuitAvail(false);
	}

	public void handleCircuitReplaced() {
		if (!circuitAvail && !beingSetup) {
			System.out.print("already dead");
		//	throw new IllegalStateException("Circuit is already inactive, why replace?");
		}
		if (beingSetup) {
			beingSetup = false;
		}
		setCircuitAvail(false);
	}

	public boolean isCircuitAvail() {
		return circuitAvail;
	}

	private void setCircuitAvail(boolean circuitAvail) {
		this.circuitAvail = circuitAvail;
	}

	@Override
	protected void enqueue(Evt e) {
		queue.offer(e);
	}

	@Override
	protected Evt pollEvt() {
		return queue.poll();
	}

	public boolean isFwdLocked() {
		return fwdLocked;
	}

	public void lockFwd() {
		this.fwdLocked = true;
	}
	
	public void unlockFwd() {
		this.fwdLocked = false;
	}
	
	protected void logEmission(double time, int bits, int val, Message m) {
		super.logEmission(time, bits, val, m);
		double tranTime = outputRate.getTime(bits).getNanoseconds();
		parentVOQ.logEmission(time, tranTime, bits, val, m);
	}
	
	

}
