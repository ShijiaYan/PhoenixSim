package edu.columbia.lrl.LWSim.components;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class ElecSwitch implements TrafficDestination, EventOrigin {
	
	protected Buffer[] output;		
	protected LWSIMExperiment lwSimExperiment;
	protected double switchProcessDelay;
	int nbPorts;
	protected boolean usePriorities;
	protected double bufferLatency;
	
	public ElecSwitch(int nbPorts, double switchProcessDelay, double outputRate, boolean usePriorities, double bufferLatency) {
		this.switchProcessDelay = switchProcessDelay;
		this.nbPorts = nbPorts;
		this.bufferLatency = bufferLatency;
		output = new Buffer[nbPorts];			
		for (int i = 0 ; i < nbPorts ; i++) {
			output[i] = new Buffer(10000, bufferLatency, i+10, outputRate, usePriorities);
		}
		this.usePriorities = usePriorities;
	}	
	
	public ElecSwitch(int nbPorts, double switchProcessDelay, double outputRate, boolean usePriorities) {
		this.switchProcessDelay = switchProcessDelay;
		this.nbPorts = nbPorts;
		output = new Buffer[nbPorts];			
		for (int i = 0 ; i < nbPorts ; i++) {
			output[i] = new Buffer(10000, 0, i+10, outputRate, usePriorities);
		}
		this.usePriorities = usePriorities;
	}
	
	public ElecSwitch(int nbPorts, double switchProcessDelay, boolean usePriorities) {
		this(nbPorts, switchProcessDelay, 1, usePriorities);
	}
	
	public void addDestination(TrafficDestination dest, int index) {
		output[index].setTrafficDestination(dest);
	}		

	@Override
	public void processEvent(Evt e) {
		lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS() + switchProcessDelay, this, output[e.getMessage().dest] , e));			// TODO Auto-generated method stub	
	}

	@Override
	public String toShortString() {
		return "Elec Router";
	}

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;	
		for (int i = 0 ; i < nbPorts ; i++) {
			output[i].initComponent(lwSimExperiment);
		}	
		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {	}
	
}