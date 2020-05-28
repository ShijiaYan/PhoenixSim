package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.EventManager;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.general.EventTarget;
import edu.columbia.lrl.general.Evt;

public class DessiesArbiter extends Arbiter {
	
	EventManager manager;
	
	public DessiesArbiter() {
		super(null);
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		
		// at init, record the pointer to the event manager
		manager = lwSimExperiment.manager;
		
		Evt e = new Evt(0, this, this, MessageType.SELF);
		manager.queueEvent(e);
		
		// if there is no problem in initialisation, return null
		return null;
		
	}
	
	public Transmitter getTransmitter(int index, Collection<Integer> destinations) {
		//return new Transmitter(destinations, this, index);
		return null;
	}	

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toShortString() {
		return "Dessie's arbiter";
	}

	@Override
	public void processEvent(Evt e) {
		if (e.getType() == MessageType.SELF) {
		//	processSlot(e);
		} else {
		//	queueMessage(e.getMessage());
		}
		
	}

	@Override
	public Map<String, String> getArbiterParameters() {
		return new SimpleMap<>(0);
	}

	@Override
	public void registerInput(EventTarget target, int index) {
		// TODO Auto-generated method stub
		
	}


	public void registerOutput(EventTarget target, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
