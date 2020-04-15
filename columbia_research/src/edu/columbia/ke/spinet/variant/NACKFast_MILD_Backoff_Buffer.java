package edu.columbia.ke.spinet.variant;

import java.util.Random;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;

import edu.columbia.lrl.general.Evt;

public class NACKFast_MILD_Backoff_Buffer extends NACKFast_Backoff_Buffer {

	private double minLinearDecUnit;
	private boolean frozen;
	
	public NACKFast_MILD_Backoff_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, double waitTimeRatioMin) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.waitTimeExpBase = 1.5;
		this.minLinearDecUnit = waitTimeRatioMin;
		frozen = false;
	}
	
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 10) {
			frozen = false;
			// rebirth
			if (fifo.size() > 0) {
				Evt self = new Evt(e.getTimeNS(), this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
			}
		}
		else {
			if (!(frozen && e.getType() == 1))
				super.processConsumerEvent(e);
		}
			
	}



	@Override
	protected boolean receiveEndOfTransmissionEvent(Evt e) {
		boolean successEOT;
		double time = e.getTimeNS();
		if (time == lastEOTTime) {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, time, TimeLine.EnumType.OK, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + currentlyTransmitted.index + ":OK");
			fifo.removeFirst();
			
			// report transmission time
			lwSimExperiment.reportTransTime(index, time - lastPacketStart, currentlyTransmitted);
			
			/*// rebirth
			if (fifo.size() > 0) {
				Evt self = new Evt(e.getTimeNS(), this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
			}
			*/
			
			successEOT = true;
		}	else {
			successEOT = false;
		}
		
		if (successEOT) {
			/*
			 * reset number of NACK received (for the currently Transmitted msg) 
			 * reset waitTimeUpper
			 */
			nbNACKForCurrentMsg = 0;
			resetWaitTime();

			// wait
			int waitTime;
			if (this.waitTimeLower == this.waitTimeUpper)
				waitTime = (int)waitTimeLower;
			else
				waitTime = new Random().nextInt((int)Math.ceil(this.waitTimeUpper - this.waitTimeLower)) + (int)this.waitTimeLower;
			
			frozen = true;
			Evt self = new Evt(time + waitTime, this, this, 10);
			lwSimExperiment.manager.queueEvent(self);
		}
		
		return successEOT;
	}

	@Override
	protected void resetWaitTime() {
		
		this.waitTimeUpper -= minLinearDecUnit * this.packetTransTime;
		
		if (waitTimeUpper < 1)
			this.waitTimeUpper = 1;
	}
	
	

	
	

}
