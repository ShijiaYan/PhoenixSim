package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.variants.NACKFast_Buffer;

public class NACKFast_Quenchable_Buffer extends NACKFast_Buffer {
	
	private boolean quenchEnabled;
	private double packetTransTime;

	public NACKFast_Quenchable_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, boolean quenchEnabled, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.quenchEnabled = quenchEnabled;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		// int size = lwSimExperiment.getBuilder().getExampleTrafficGenerator().getAveragePacketSize();
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();	
		return null;
	}
	
	@Override
	protected void processProducerEvent(Evt e) {	
		defineSpinetMessage(e);		
		super.processProducerEvent(e);				
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			quenchQueue(e.getTimeNS());
			if (fifo.size() <= 0) return;
		}
		super.processConsumerEvent(e);
	}
	
	protected void quenchQueue(double currentTime){
		if (this.quenchEnabled) {
			// delete expired msg before sending msgs out from the priority queue				
			double addTime = 0;
			while (this.fifo.size() > 0) {								
				Message first = fifo.peek();
				double deadline = first.getDeadline();
				// assume you don't have this info
				/*if (first == this.justNACKed)
					addTime = this.leastWaitTime;
				*/
				
				if (deadline <= currentTime + addTime + packetTransTime) {
					// dropped due to expiration, use dropType = 2
					lwSimExperiment.packetDropped(first, toString(), this, 2);
					fifo.poll();
				} else
					break;
			}	
			this.currentlyTransmitted = null;
		}
	}
	
	@Override
	protected void receiveNack(Evt e) {
	/*	if (index == 2) {
			System.out.println(e.getTimeNS() + " : Receive NACK for msg " + currentlyTransmitted.index);
		}	*/	
		if (e.getMessage()!= currentlyTransmitted) {
			throw new IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
		}
		e.getMessage().setActive(false);
		releaseSwitches(e);
		if (lwSimExperiment.isWithTimeLine())
			timeline.addJobPhase(lastPacketStart, e.getTimeNS(), TimeLine.EnumType.ERROR, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + e.getMessage().index + ":NACK");		
		
		// report transmission time
		lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
					
		// immediately resend
		
		// check that lastEOT not called twice...
		nackReceived = true;	
		lastEOTTime = 0;
		lwSimExperiment.packetRetransmitted(currentlyTransmitted);

		// rebirth
		Evt self = new Evt(e.getTimeNS(), this, this, 1);
		lwSimExperiment.manager.queueEvent(self);
		

	}	

}
