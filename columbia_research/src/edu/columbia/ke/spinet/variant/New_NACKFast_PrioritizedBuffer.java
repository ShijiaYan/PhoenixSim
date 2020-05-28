package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Receiver;

public class New_NACKFast_PrioritizedBuffer extends PrioritizedBuffer implements ACK_or_NACK_Receiver {
	
	protected double waitTimeUpper = 1;
	protected double waitTimeLower = 0;
	protected double waitTimeExpBase = 2;
	
	private PriorityMode priorityMode;
	protected Message currentlyTransmitted;
	private double leastWaitTime;
	private double lastEOTTime;
	private Message backInQueueMsg = null;

	public New_NACKFast_PrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		priorityMode = pm;
	}
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			if (!idle)
				throw new IllegalStateException("still sending the last packet");
			
			if (this.priorityQueue.size() > 0){
				sendMessage(e, this.priorityQueue.pollFirstEntry().getValue());
				if (this.backInQueueMsg != null) {
					backInQueue(this.backInQueueMsg, e.getTimeNS());
					this.backInQueueMsg = null;
				}
			}
			
		} else if (e.getType() == 2) {
			receiveEndOfTransmissionEvent(e);
		} else if (e.getType() == 3){
			receiveNack(e);
		} else {
			throw new IllegalStateException("received unknown event type: "+e.getType());
		}
	}
	
	protected void receiveEndOfTransmissionEvent(Evt e) {
		double currentTime = e.getTimeNS();
		
		if (currentTime == lastEOTTime) {
			
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, currentTime, TimeLine.EnumType.OK, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + currentlyTransmitted.index + ":OK");
			idle = true;
			doAfterEndofTrans(e);
		}	
	}
	
	protected void doAfterEndofTrans(Evt e) {
		this.waitTimeUpper = 1;
		accessControlCenter(e.getTimeNS(), 2);
	}
	
	@Override
	protected void sendMessage(Evt e, Message m) {
		if (m == null){
			throw new IllegalStateException("Null Message.");
		}
		
		idle = false;
		this.currentlyTransmitted = m;
		
		double startTime = getDepartureTime(e);
		leastWaitTime = 0;
		
		// mark sending time in packet
		m.incNumTrans();
		m.object = new Pair<Double, ACK_or_NACK_Receiver>(startTime, this);
		m.setActive(true);
		Evt next = new Evt(startTime, this, nextDest, e);
		next.setMessage(m);
		defineSpinetMessage(next);
		lastPacketStart = startTime;
		lwSimExperiment.manager.queueEvent(next);
		lwSimExperiment.packetTransmitted(m);

		busyUntil = getEOTTimeNS(m, startTime);
		lastEOTTime = busyUntil;
		
		Evt self = new Evt(busyUntil, this, this, 2, e);
		self.setMessage(m);
		lwSimExperiment.manager.queueEvent(self);
	}
	
	protected double getDepartureTime(Evt e) {
		return e.getTimeNS();
	}

	protected void receiveNack(Evt e) {
		EventOrigin newOrigin = e.getOrigin();
		if (newOrigin == null)
			throw new IllegalStateException("Null NACK origin.");
		else{
			this.leastWaitTime = e.getMessage().getLeastTimeToWait();
			if (this.leastWaitTime < 0)
				throw new IllegalStateException("leastWaitTime < 0");
		}

		if (e.getMessage() != currentlyTransmitted) {
			//System.out
			//		.println("\n"+"A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
			throw new IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
		} else {
			e.getMessage().setActive(false);
			releaseSwitches(e);
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(
						lastPacketStart,
						e.getTimeNS(),
						TimeLine.EnumType.ERROR,
						currentlyTransmitted.origin + "->"
								+ currentlyTransmitted.dest + "\n"
								+ e.getMessage().index + ":NACK");
			
			// I don't immediately resend
			lastEOTTime = 0;
			lwSimExperiment.packetRetransmitted(currentlyTransmitted);
			idle = true;
			
			doAfterNACK(e);
		}
	}	
	
	protected void releaseSwitches(Evt e){
		for (LWSimComponent c: currentlyTransmitted.getOccupiedResources()){
			Evt release = new Evt(e.getTimeNS(), this, c, 9);
			release.setMessage(currentlyTransmitted);
			lwSimExperiment.manager.queueEvent(release);
		}
	}
	
	protected void backInQueue(Message m, double currentTime){

		priorityQueue.put(((SpinetMessage)m).getSpinetPriority(), m);
		
		if (priorityQueue.size() > maxSize) {
			throw new SimulationException("Buffer overflow");
		}
	}
	
	protected void doAfterNACK(Evt e) {
		this.waitTimeUpper *= waitTimeExpBase;
		double currentTime = e.getTimeNS();
		
		switch (this.priorityMode){
		case EDF_RETRAN_YIELD:
			if (this.priorityQueue.size() > 0)
				backInQueueMsg  = e.getMessage();
			else
				backInQueue(e.getMessage(), currentTime);
			break;
		default:
			backInQueue(e.getMessage(), currentTime);
			break;
		}

		accessControlCenter(currentTime, 3);
	}
	
	protected void accessControlCenter(double currentTime, int eventType){
		Evt self;
		switch (eventType){
		case 2:	// end of transmission
			// rebirth
			self = new Evt(currentTime, this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
			break;
		case 3:	// nacked
			// rebirth
			self = new Evt(currentTime + getNextWaitTime(currentTime), this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
			break;
		default:
			throw new IllegalStateException("illegal event type"); 
		}
	}

	protected double getNextWaitTime(double currentTime) {
		return 0;
	}

}
