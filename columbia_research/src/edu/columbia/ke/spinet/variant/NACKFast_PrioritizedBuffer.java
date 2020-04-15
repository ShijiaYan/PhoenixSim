package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class NACKFast_PrioritizedBuffer extends NACK_PrioritizedBuffer {
	
	protected double lastEOTTime = 0;
	protected double waitTimeUpper = 1;
	protected double waitTimeLower = 0;
	protected double waitTimeExpBase = 2;
	protected int nbNACKForCurrentMsg;

	public NACKFast_PrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, pm);
	}
	
	@Override
	protected void receiveEndOfTransmissionEvent(Evt e) {
		
		if (e.getTimeNS() == lastEOTTime) {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, e.getTimeNS(), TimeLine.EnumType.OK, currentlyTransmitted.origin + "->" + currentlyTransmitted.dest + "\n" + currentlyTransmitted.index + ":OK");
			/*if (this.currentIterator == null)
				throw new IllegalStateException();
			else 
				this.currentIterator.remove();*/
			this.priorityQueue.entrySet().remove(currentEntry);
			
			/*
			 * reset number of NACK received (for the currently Transmitted msg) 
			 */
			nbNACKForCurrentMsg = 0;
			doAfterEndofTrans(e);
		}	
	
	}
	
	protected void doAfterEndofTrans(Evt e) {
		this.waitTimeUpper = 1;
		
		// rebirth
		if (this.priorityQueue.size() > 0) {
			Evt self = new Evt(e.getTimeNS(), this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
		}			
		
	}

	@Override
	protected void sendMessage(Message toSend, Evt e) {
		if (builder == null) throw new NullPointerException("A NACKFAst_Buffer has no reference for builder, need to be set when constructing the model");
		//lastEOTTime = getEOTTimeNS(toSend, e);
		//System.out.println("Buffer "+this.index+" sendMessage: msg " + toSend.index);
		super.sendMessage(toSend, e);	
		lastEOTTime = this.busyUntil;
	}		
	
	@Override
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
			System.out
					.println("\n"+"A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
			// throw new
			// IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong");
		} else {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(
						lastPacketStart,
						e.getTimeNS(),
						TimeLine.EnumType.ERROR,
						currentlyTransmitted.origin + "->"
								+ currentlyTransmitted.dest + "\n"
								+ e.getMessage().index + ":NACK");
			// I don't immediately resend

			// check that lastEOT not called twice...
			// sendMessage(currentlyTransmitted, e);
			nackReceived = true;
			lwSimExperiment.packetRetransmitted(currentlyTransmitted);

			justNACKed = currentlyTransmitted;
			justNACKedEntry = this.currentEntry;
			
			this.nbNACKForCurrentMsg++;
			this.currentlyTransmitted = null;
			this.currentEntry = null;
			lastEOTTime = 0;
			
			doAfterNACK(e);
		}
	}	
	
	protected void doAfterNACK(Evt e) {
		this.waitTimeUpper *= waitTimeExpBase;

		// rebirth
		Evt self = new Evt(e.getTimeNS() + getNextWaitTime(e.getTimeNS()), this, this, 1);
		lwSimExperiment.manager.queueEvent(self);
		
	}

	protected double getNextWaitTime(double currentTime) {
		return 0;
	}

}
