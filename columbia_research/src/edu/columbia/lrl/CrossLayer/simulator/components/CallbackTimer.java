package edu.columbia.lrl.CrossLayer.simulator.components;

import ch.epfl.general_libraries.simulation.SimulationException;
import edu.columbia.lrl.LWSim.EventManager;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;


//TODO: Make callback object a template parameter
interface Callback {
	void callback(EventTarget t);
}

class CallbackTimer implements TrafficDestination, EventOrigin {
	
	LWSIMExperiment experiment;
	EventManager manager;
	Callback callback;
	EventTarget callbackObject;
	
	boolean active;
	
	public CallbackTimer(EventTarget callbackObject, Callback callback) {
		active = false;
		this.callbackObject = callbackObject;
		this.callback = callback;
	} 
	
	void start(double time) {
		if( active ) throw new SimulationException("Trying to start timing before finishing previous timer.");
		
		Evt timerEvt = new Evt(experiment.getSimTimeNS() + time, this, this);
		manager.queueEvent(timerEvt);
		active = true;
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) { 
		this.experiment = lwSimExperiment;
		this.manager = lwSimExperiment.manager;
		
		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {
	}

	@Override
	public String toShortString() {
		return "Timer";
	}

	@Override
	public void processEvent(Evt e) {
		//Mark timer as inactive and perform whatever task
		active = false;
		callback.callback(callbackObject);			
	}
	
	public boolean isActive() {
		return active;
	}
	
}
