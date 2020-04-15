package edu.columbia.lrl.experiments.spinet.variants;


import ch.epfl.general_libraries.graphics.timeline.TimeLine;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;


public class NACKFast_Buffer extends NACK_Buffer {
	
	protected double lastEOTTime = 0;
	protected boolean lastSuccessful = true;
	
	public NACKFast_Buffer(int maxSize, double bufferLatency, double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
	}
	
	@Override
	protected boolean receiveEndOfTransmissionEvent(Evt e) {
	/*	if (index == 2) {
			System.out.println(e.getTimeNS() + " : EOT of msg " + currentlyTransmitted.index);
		}*/
		
		if (e.getTimeNS() == lastEOTTime) {
			
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, e.getTimeNS(), TimeLine.EnumType.OK, "m:" + currentlyTransmitted.index  + "\r\n->" + currentlyTransmitted.dest);
			
			if (fifo.size()<=0)
				return false;
			
			fifo.removeFirst();
			
			this.lastSuccessful = true;
			
			// report transmission time
			lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
			
			// rebirth
			if (fifo.size() > 0) {
				Evt self = new Evt(e.getTimeNS(), this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
			}	
			return true;
		}	else {
			return false;
		}
	}
	
	@Override
	protected double sendMessage(Message toSend, Evt e) {
		if (builder == null) throw new NullPointerException("A NACKFAst_Buffer has no reference for builder, need to be set when constructing the model");
		/* added for safety
		SpinetMessage tmp = (SpinetMessage)toSend;
		defineSpinetMessage(tmp);
		*/
		lastEOTTime = super.sendMessage(toSend, e);	
		return lastEOTTime;
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
		this.lastSuccessful = false;
		lastEOTTime = 0;
		lwSimExperiment.packetRetransmitted(currentlyTransmitted);
		sendMessage(currentlyTransmitted, e);

	}	
	
	
}
