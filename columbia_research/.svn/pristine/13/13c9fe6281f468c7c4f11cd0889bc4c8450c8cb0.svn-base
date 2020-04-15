package edu.columbia.ke.spinet.variant;

import java.util.Random;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.variants.NACKFast_Buffer;

public class NACKFast_Backoff_Buffer extends NACKFast_Buffer {

	protected double waitTimeUpper = 1;
	protected double waitTimeLower = 0;
	protected double waitTimeExpBase = 2;
	protected int nbNACKForCurrentMsg;
	protected double packetTransTime;
	//protected int nextWaitTime;
	//private int waitTimeUnit = 10;	// 10 nanoseconds as a basic unit
	
	public NACKFast_Backoff_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		super.initComponent(lwSimExperiment);
		// int size = lwSimExperiment.getBuilder().getExampleTrafficGenerator().getAveragePacketSize();
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();	
		return null;
	}

	@Override
	protected boolean receiveEndOfTransmissionEvent(Evt e) {
		boolean successEOT = super.receiveEndOfTransmissionEvent(e);
		if (successEOT) {
			/*
			 * reset number of NACK received (for the currently Transmitted msg) 
			 * reset waitTimeUpper
			 */
			nbNACKForCurrentMsg = 0;
			resetWaitTime();
		}
			
		return successEOT;
	}
	
	protected void incWaitTimeUpper(){
		this.nbNACKForCurrentMsg++;
		this.waitTimeUpper *= waitTimeExpBase;
	}
	
	protected void resetWaitTime(){

		if (this.waitTimeLower == 0)
			this.waitTimeUpper = 1;
		else
			this.waitTimeUpper = waitTimeLower;
	}

	@Override
	protected void receiveNack(Evt e) {
		if (e.getMessage() != currentlyTransmitted) {
			// System.out
			// 		.println("\n"+"A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
			throw new IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
		} else {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(
						lastPacketStart,
						e.getTimeNS(),
						TimeLine.EnumType.ERROR,
						currentlyTransmitted.origin + "->"
								+ currentlyTransmitted.dest + "\n"
								+ e.getMessage().index + ":NACK");
			
			// report transmission time
			lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart, currentlyTransmitted);
						
			// I don't immediately resend

			// check that lastEOT not called twice...
			// sendMessage(currentlyTransmitted, e);
			nackReceived = true;
			lwSimExperiment.packetRetransmitted(currentlyTransmitted);
		
			/*
			 * // trial for safety this.currentEntry = null;
			 * this.currentlyTransmitted = null;
			 */
			
			this.currentlyTransmitted = null;
			lastEOTTime = 0;
			
			// rebirth
			Evt self = new Evt(e.getTimeNS() + getNextWaitTime(e), this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
		}
	}
	
	protected double getNextWaitTime(Evt e){
		this.incWaitTimeUpper();
		int waitTime;
		if (this.waitTimeLower == this.waitTimeUpper)
			waitTime = (int)waitTimeLower;
		else
			waitTime = new Random().nextInt((int)Math.ceil(this.waitTimeUpper - this.waitTimeLower)) + (int)this.waitTimeLower;
		return waitTime;
	}
	
	
}
