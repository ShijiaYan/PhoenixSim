package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Sender;


public class NonBlockingReceiver extends Receiver{
	
	private double 	nextPacketStart;
	private Rate 	inputRate; 

	public NonBlockingReceiver(int index) {
		super(index);
		nextPacketStart = 0;
	}

	@Override
	public void processEvent(Evt e) {
		double arrivalTime = e.getTimeNS();
		if(arrivalTime < nextPacketStart){
			packetDropped(e.getMessage(), arrivalTime);		
		} else {
			nextPacketStart = getEOTTimeNS(e);
			//super.processEvent(e);
			
			Message m = e.getMessage();
		
			/*if (m.index <= getLatest(m.origin))
				throw new IllegalStateException(
						"Message received twice or out-of-order");*/
			
			if (m.dest != index)
				throw new IllegalStateException("Wrong index at receiver");
			/*lwSimExperiment.packetReceived(m.origin, m.dest, m.timeEmitted,
					e.getTimeNS());*/
			lwSimExperiment.packetReceived(m, m.origin, m.dest, m.timeEmitted, e.getTimeNS());
			setLatest(m.origin, m.index);
			if (notifiable != null) {
				notifiable.objectReceived(
						m,
						m.carriedData,
						m.origin,
						lwSimExperiment.getReferenceBandwidth()
								.getTime(m.sizeInBits).getNanoseconds()
								+ e.getTimeNS());
			}

		}
	}
	
	public double getEOTTimeNS(Evt e) {	
		return e.getTimeNS()+inputRate.getTime(e.getMessage().sizeInBits).getNanoseconds();
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		super.initComponent(lwSimExperiment);
		inputRate = lwSimExperiment.getReferenceBandwidth();
		return null;
	}	
	
	// copied from NACK two two switch
	protected void packetDropped(Message m, double time) {
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(m, time);
		
		if (20 < elapsedTime) {
			@SuppressWarnings("unused")
			int eiruh = 0;
		}
		
		m.setLeastTimeToWait(this.nextPacketStart - time);
		Evt next = new Evt(time + elapsedTime, this, ACK_or_NACK_Sender.getFeedbackDestination(m), 3);
		next.setMessage(m);
		lwSimExperiment.manager.queueEvent(next);
		// use type = 1 for contention
		// used to be packetContented ()
		lwSimExperiment.packetDropped(m, toString(), this, 1);
	}

}
