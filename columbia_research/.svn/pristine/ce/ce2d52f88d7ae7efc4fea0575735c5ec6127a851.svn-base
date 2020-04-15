package edu.columbia.ke.spinet.variant;

import java.util.Random;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.experiments.spinet.variants.NACKFast_SlottedBuffer;

public class slottedALOHABuffer extends NACKFast_SlottedBuffer {
	
	protected double p;
	protected boolean backingoff = false;

	public slottedALOHABuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, double slotDuration, int index, double retranProb) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, slotDuration,
				index);
		p = retranProb;
	}
	
	
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			if (this.fifo.size() <= 0)
				return;
			
			if (!this.lastSuccessful) {
				double r = new Random().nextDouble();
				if (r > p) {
					// rebirth
					Evt self = new Evt(e.getTimeNS() + this.slotDuration, this, this, 1);
					lwSimExperiment.manager.queueEvent(self);
					return;
				}
			}
		}
		super.processConsumerEvent(e);
	}
	
	@Override
	protected void receiveNack(Evt e) {
	/*	if (index == 2) {
			System.out.println(e.getTimeNS() + " : Receive NACK for msg " + currentlyTransmitted.index);
		}	*/	
		if (e.getMessage()!= currentlyTransmitted) {
			//throw new IllegalStateException();
			System.out.println("e.getMessage()!= currentlyTransmitted");
			return;
		}
		
		if (lwSimExperiment.isWithTimeLine())
			timeline.addJobPhase(lastPacketStart, e.getTimeNS(), TimeLine.EnumType.ERROR, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + e.getMessage().index + ":NACK");		
		// immediately resend	
		
		// report transmission time
		lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
		
		// check that lastEOT not called twice...
		nackReceived = true;	
		this.lastSuccessful = false;
		lastEOTTime = 0;
		lwSimExperiment.packetRetransmitted(currentlyTransmitted);
		
		// rebirth
		Evt self = new Evt(e.getTimeNS() + 0.1*this.slotDuration, this, this, 1);
		lwSimExperiment.manager.queueEvent(self);
	}
}
