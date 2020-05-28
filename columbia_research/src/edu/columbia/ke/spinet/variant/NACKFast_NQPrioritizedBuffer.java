package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Receiver;

public class NACKFast_NQPrioritizedBuffer extends NQPrioritizedBuffer  implements ACK_or_NACK_Receiver {
	
	private double lastEOTTime;
	protected double[] waitUntil;
	protected Message currentlyTransmitted;
	protected int totalNACK;
	protected int totalTran;
	protected int totalSuccess = 1;
	
	protected PriorityMode priorityMode;

	public NACKFast_NQPrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.priorityMode = pm;
	}
	
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		waitUntil = new double[this.nbClient];
		return null;
	}


	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			super.processConsumerEvent(e);
		} else if (e.getType() == 2) {
			receiveEndOfTransmissionEvent(e);
		} else if (e.getType() == 3){
			receiveNack(e);
		} else {
			throw new IllegalStateException("received unknown event type: "+e.getType());
		}
	}
	
	@Override
	protected void sendMessage(Evt e, Message m) {
		if (m == null){
			throw new IllegalStateException("Null Message.");
		}
		
		idle = false;
		this.currentlyTransmitted = m;
		
		double startTime = getDepartureTime(e);
		
		// mark sending time in packet
		m.incNumTrans();
		m.setActive(true);
		m.object = new Pair<Double, ACK_or_NACK_Receiver>(startTime, this);
		Evt next = new Evt(startTime, this, nextDest, e);
		next.setMessage(m);
		defineSpinetMessage(next);
		lastPacketStart = startTime;
		lwSimExperiment.manager.queueEvent(next);
		lwSimExperiment.packetTransmitted(m);
		this.totalTran++;

		busyUntil = getEOTTimeNS(m, startTime);
		lastEOTTime = busyUntil;
		this.waitUntil[m.dest] = busyUntil;
		
		Evt self = new Evt(busyUntil, this, this, 2, e);
		self.setMessage(m);
		lwSimExperiment.manager.queueEvent(self);
	}
	
	protected double getDepartureTime(Evt e) {
		return e.getTimeNS();
	}
	
	protected void receiveEndOfTransmissionEvent(Evt e) {
		
		double currentTime = e.getTimeNS();
		if (currentTime == lastEOTTime) {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, currentTime, TimeLine.EnumType.OK, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + currentlyTransmitted.index + ":OK");
			idle = true;
			this.totalSuccess++;
			
			// report transmission time
			lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
			
			doAfterEndofTrans(e);
		}	
	}
	
	protected void doAfterEndofTrans(Evt e) {
		accessControlCenter(e.getTimeNS(), 2);
	}
	
	protected void receiveNack(Evt e) {
		EventOrigin newOrigin = e.getOrigin();
		
		if (newOrigin == null)
			throw new IllegalStateException("Null NACK origin.");

		if (e.getMessage() != currentlyTransmitted) {
			System.out
					.println("\n"+"A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
			// throw new IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
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
			this.totalNACK++;
			
			// report transmission time
			lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
			
			// get lwt
			SpinetMessage msg = (SpinetMessage)e.getMessage();
			double lwt = msg.getLeastTimeToWait();
			if (lwt < 0)
				throw new IllegalStateException("leastWaitTime < 0");
			updateWaitUntil(msg, e.getTimeNS(), lwt);
			
			
			// update the waitUntil value of VOQs that share the same upstream path section as the NACKed packet
			
			
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
	
	protected void updateWaitUntil(SpinetMessage msg, double now, double lwt){
		/* this part for flattened butterfly
		 * 
		 *\/
		int clientPerCluster = 8;		// assuming 8 clients per switch, may change in future
		
		double newWaitUntil = lwt + now;
		int stagesNotPass = msg.getNumberOfHeaderOffset();	
		/* msg.getNumberOfHeaderOffset() is equal to number of stages left for the packet to PASS through
		 * 2 sw stages in total (shortest path; single path)
		 * so stagesLeft = either 2 or 1
		 *\/
		if (!(stagesNotPass == 1 || stagesNotPass == 2))
			throw new IllegalStateException("illegal stagesNotPass");
		
		int destClusterIndex = msg.dest/clientPerCluster;
		int srcClusterIndex = msg.origin/clientPerCluster;
		
		if (destClusterIndex == srcClusterIndex)	// inner cluster traffic
			this.waitUntil[msg.dest] = newWaitUntil;
		else {	// inter cluster traffic
			if (stagesNotPass == 2) {	// blocked by inter cluster link
				for (int i = destClusterIndex * clientPerCluster; i < (destClusterIndex + 1)
						* clientPerCluster; i++) {
					this.waitUntil[i] = newWaitUntil;
				}
			} else	// blocked by destination
				this.waitUntil[msg.dest] = newWaitUntil;
		} */
		// end for flattened butterfly
		
		
		/* this part for omega network
		 * routing tag = src addr XOR dest addr
		 */
		
		int shiftStage = msg.getNumberOfHeaderOffset() - 1;
		int destShifted = msg.dest;
		for (int j = 0 ; j < shiftStage; j++)
			destShifted = destShifted/2;
		
		double newWaitUntil = lwt + now;
		
		for (int i = 0; i < this.nbClient; i++){
			int ii = i;
			for (int j = 0 ; j < shiftStage; j++)
				ii = ii/2;
			
			int diff = ii ^ msg.dest;
			
			if (diff==0 && waitUntil[i] < newWaitUntil)
				this.waitUntil[i] = newWaitUntil;
		}
	}
	
	protected void doAfterNACK(Evt e) {
		double currentTime = e.getTimeNS();
		backInQueue(e.getMessage(), currentTime);
		accessControlCenter(currentTime, 3);
	}
	
	protected void backInQueue(Message m, double currentTime){
		priorityQueue[m.dest].put(((SpinetMessage)m).getSpinetPriority(), m);
		
		if (priorityQueue[m.dest].size() > maxSize) {
			throw new SimulationException("Buffer overflow");
		}
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
