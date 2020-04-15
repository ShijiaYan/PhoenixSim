package edu.columbia.lrl.experiments.spinet.variants;

import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.experiments.spinet.ImprovedBuffer;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class NACK_Buffer extends ImprovedBuffer implements ACK_or_NACK_Receiver {
	
	protected Message currentlyTransmitted;
	protected boolean nackReceived = false;
	
	public NACK_Buffer(int maxSize, double bufferLatency, double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
	}
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			// BIRTH of the process
			currentlyTransmitted = fifo.peek();
			sendMessage(currentlyTransmitted, e);
		} else if (e.getType() == 2) {
			receiveEndOfTransmissionEvent(e);
		} else {
			receiveNack(e);
		}
	}
	
	protected boolean receiveEndOfTransmissionEvent(Evt e) {
		if (nackReceived) {
			// retry
			sendMessage(currentlyTransmitted, e);
			lwSimExperiment.packetRetransmitted(currentlyTransmitted);
			return false;
			/*			if (currentlyTransmitted.index == 1)
							System.out.println("Resending message " + currentlyTransmitted.index + " at " + e.getTimeNS());*/
		} else {
			
			fifo.removeFirst();
			// rebirth
			if (fifo.size() > 0) {
				Evt self = new Evt(e.getTimeNS(), this, this, 1, e);
				lwSimExperiment.manager.queueEvent(self);
			}
			return true;
		}
		
	}
	
	protected void receiveNack(Evt e) {
		if (e.getMessage()!= currentlyTransmitted) {
			throw new IllegalStateException();
		}

		nackReceived = true;
		releaseSwitches(e);
		e.getMessage().setActive(false);
		lwSimExperiment.packetRetransmitted(currentlyTransmitted);
	}
	

	protected void releaseSwitches(Evt e){
		for (LWSimComponent c: currentlyTransmitted.getOccupiedResources()){
			Evt release = new Evt(e.getTimeNS(), this, c, 9);
			release.setMessage(currentlyTransmitted);
			lwSimExperiment.manager.queueEvent(release);
		}
	}
	
	protected double sendMessage(Message toSend, Evt e) {
	/*	if (toSend.index == 0) {
			System.out.println("Nack_buffer:" + this.index + " pkt " + toSend.index + " sent from buffer at time " + e.getTimeNS());
		}	*/
		nackReceived = false;
		double time = e.getTimeNS();
		// mark sending time in packet
		toSend.object = new Pair<Double, ACK_or_NACK_Receiver>(time, this);toSend.incNumTrans();
		toSend.setActive(true);
		Evt next = new Evt(time, this, nextDest, /* test */ e);
		next.setMessage(toSend);
		defineSpinetMessage(next);
		lastPacketStart = time;
		lwSimExperiment.manager.queueEvent(next);	
		busyUntil = getEOTTimeNS(toSend, e);	
		Evt self = new Evt(busyUntil, this, this, 2, e);	
		lwSimExperiment.manager.queueEvent(self);		
		lwSimExperiment.packetTransmitted(toSend);
		return busyUntil;
	}
		
	
	
}
