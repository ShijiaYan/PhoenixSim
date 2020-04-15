package edu.columbia.lrl.experiments.spinet.variants;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;



public class NACKFast_SlottedBuffer extends NACKFast_Buffer {


	protected double slotDuration;
	
	public NACKFast_SlottedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, double slotDuration, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.slotDuration = slotDuration;
	}
	
	@Override

	protected double sendMessage(Message toSend, Evt e) {
	/*	if (toSend.index == 0) {
			System.out.println("Nack_buffer:" + this.index + " pkt " + toSend.index + " sent from buffer at time " + e.getTimeNS());
		}	*/
		
		
		nackReceived = false;
		// mark sending time in packet
		lastPacketStart = getSlottedDepartureTime(e);
		double diff = lastPacketStart - e.getTimeNS();
		toSend.object = new Pair<Double, ACK_or_NACK_Receiver>(lastPacketStart, this);
		toSend.setActive(true);
		Evt next = new Evt(lastPacketStart, this, nextDest);
		
		next.setMessage(toSend);
		defineSpinetMessage(next);
		lwSimExperiment.manager.queueEvent(next);	
		busyUntil = getEOTTimeNS(toSend, e)+diff;
		lastEOTTime = busyUntil;
		Evt self = new Evt(busyUntil, this, this, 2);	
		lwSimExperiment.manager.queueEvent(self);			
		lwSimExperiment.packetTransmitted(toSend);
		return busyUntil;
	}
	
	@Override
	protected void receiveNack(Evt e) {
	/*	if (index == 2) {
			System.out.println(e.getTimeNS() + " : Receive NACK for msg " + currentlyTransmitted.index);
		}	*/	
		if (e.getMessage()!= currentlyTransmitted) {
			throw new IllegalStateException();
		}
		e.getMessage().setActive(false);
		releaseSwitches(e);
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
		
		sendMessage(currentlyTransmitted, e);
	}	
	
	
	private double getSlottedDepartureTime(Evt e) {
		double mod = e.getTimeNS() % slotDuration;
		double slotId = (e.getTimeNS() - mod)/slotDuration;
		double nextTime = (slotId+1)*slotDuration;
		return nextTime;
	}
	
	

}
