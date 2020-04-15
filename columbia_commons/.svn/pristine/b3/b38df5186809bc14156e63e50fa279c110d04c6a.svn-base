package edu.columbia.lrl.LWSim.components;

import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class Merger extends AbstractTrafficOrigin implements LWSimComponent, TrafficDestination {
	
	private LWSIMExperiment exp;
	
	public Merger() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void notifyEnd(double ref, double status) {}

	@Override
	public void processEvent(Evt e) {
		Evt ex = new Evt(e.getTimeNS(), this, nextDest, e);
		exp.manager.queueEvent(ex);
	}

	@Override
	public String toShortString() {
		return "merger";
	}

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.exp = lwSimExperiment;	
		return null;
	}




}
