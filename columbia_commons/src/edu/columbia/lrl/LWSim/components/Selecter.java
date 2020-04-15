package edu.columbia.lrl.LWSim.components;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class Selecter implements TrafficDestination, EventOrigin  {
		
	private LWSIMExperiment lwSimExperiment;
	private int indexToKeep;
	
	private TrafficDestination dest;
	
	public Selecter(int indexToKeep) {
		this.indexToKeep = indexToKeep;
	}
	
	public void setTrafficDestination(TrafficDestination dest) {
		this.dest = dest;
	}
	
	public void processEvent(Evt e) {
		Message m = e.getMessage();
		if (m.dest == indexToKeep)
			lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS(), this, dest, e)); // add some delay for mimicking processing time
	}

	@Override
	public String toShortString() {
		return "DestinationFilter";
	}

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}			
	
	@Override
	public void notifyEnd(double ref, double status) {	}	
}
