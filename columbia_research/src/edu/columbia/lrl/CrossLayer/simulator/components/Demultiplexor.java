package edu.columbia.lrl.CrossLayer.simulator.components;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class Demultiplexor implements TrafficDestination, EventOrigin {

	private TrafficDestination dest;
	private LWSIMExperiment experiment;
	private int index;
	
	public Demultiplexor(int index) {
		this.index = index;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.experiment = lwSimExperiment;
		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {		
	}

	@Override
	public String toShortString() {
		return "Demultiplexor_"+index;
	}

	public void setTrafficDestination(TrafficDestination dest) {
		this.dest = dest;
	}
	
	@Override
	public void processEvent(Evt e) {
		experiment.manager.queueEvent(new Evt(e.getTimeNS(), this, dest, 0, e.getMessage()));
	}
	
	public String toString() {
		return toShortString();
	}

}
