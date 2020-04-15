package edu.columbia.lrl.LWSim;

import edu.columbia.lrl.general.EventTarget;


public interface LWSimComponent extends EventTarget {
	
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment);
	public void notifyEnd(double ref, double status);	
	public String toShortString();
//	public void init();
}
