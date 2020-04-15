package edu.columbia.ke.component;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Sender;

public class Defletion_HighRadixSwitch extends HighRadixSwitch {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TrafficDestination myDefletDest;

	public Defletion_HighRadixSwitch(int id, int nbNode, double swi,
			double linkLatency, TrafficDestination defletDest) {
		super(id, nbNode, swi, linkLatency);
		myDefletDest = defletDest;
	}

	@Override
	protected void packetDropped(Message m, Evt e, double time, int type,
			double smallestLWT) {
			
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(m, time);
		
		if (20 < elapsedTime) {
			@SuppressWarnings("unused")
			int eiruh = 0;
		}
		
		m.setLeastTimeToWait(smallestLWT);
		Evt next;
		if (m.origin == this.id) {		// from the local node
			next = new Evt(time + elapsedTime, this, ACK_or_NACK_Sender.getFeedbackDestination(m), 3);	
		} else if (e.getOrigin() == myDefletDest) {		// from the defletion buffer
			next = new Evt(time + elapsedTime, this, myDefletDest, 3);
		} else {	// from other nodes, assuming dropping needs some latency
			next = new Evt(time + packetTransTime, this, myDefletDest, 0);
		}
		
		//lwSimExperiment.packetDropped(m, toString(), this, type);
		
		next.setMessage(m);
		lwSimExperiment.manager.queueEvent(next);
	}
}
