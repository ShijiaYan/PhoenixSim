package edu.columbia.lrl.LWSim.components;

import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;


public class TransmissionLink extends AbstractTrafficOrigin implements TrafficTransitPoint {
	
	private static int idCpt = 0;
	
	private int id;
	
	private double latencyNS;

	private LWSIMExperiment lwSimExperiment;
	
//	private EventOrigin trafficOrigin;
	
	public TransmissionLink(double latencyNS) {
		id = idCpt;
		idCpt++;
		this.latencyNS = latencyNS;
	}	
	
	/**
	 * This method has no effect on the link behavior, but can be used to ease tracking the events for debug
	 * @param origin
	 */
	public void setTrafficOrigin(EventOrigin origin) {
	//	this.trafficOrigin = origin;
	}
	
	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}	
	
	@Override
	public void processEvent(Evt e) {
		double t = e.getTimeNS() + latencyNS;
		Evt next = new Evt(t, this, nextDest, e);
		lwSimExperiment.manager.queueEvent(next);
	}
	
	public String toString() {
		return "link"+id;
	}
	
	@Override
	public String toShortString() {
		return "transmission link";
	}
	
	@Override
	public void notifyEnd(double ref, double status) {	}	
}
