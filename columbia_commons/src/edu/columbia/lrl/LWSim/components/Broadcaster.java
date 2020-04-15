package edu.columbia.lrl.LWSim.components;

import java.util.ArrayList;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class Broadcaster implements TrafficDestination, EventOrigin {
		
	private ArrayList<LWSimComponent> dests;
	private LWSIMExperiment lwSimExperiment;

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}
	
	public void addDestination(LWSimComponent dest) {
		dests.add(dest);
	}

	@Override
	public void processEvent(Evt e) {
		for (LWSimComponent d : dests) {
			lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS(), this, d, e));
		}
		
	}

	@Override
	public String toShortString() {
		return "Broadcaster with now " + dests.size() + " destinations";
	}
	
	@Override
	public void notifyEnd(double ref, double status) {	}
}
