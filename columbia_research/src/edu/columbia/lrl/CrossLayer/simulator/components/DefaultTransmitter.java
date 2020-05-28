package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.simulation.SimulationException;
import edu.columbia.lrl.LWSim.EventManager;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class DefaultTransmitter extends Transmitter {

	LWSIMExperiment experiment;
	EventManager manager;

	Map<Integer, CircuitRequestBuffer<Message>> buffers;
	Arbiter arbiter;
	
	int index;
	
	public DefaultTransmitter(Collection<Integer> destinations, Arbiter arbiter, int index) {
		this.arbiter = arbiter;
		this.index = index;
		
		//Use virtual output queues
		buffers = new TreeMap<>();
		
		//Use virtual output queues
		for( int dest : destinations ) {
			buffers.put(dest, new CircuitRequestBuffer<>(10000));
		}	
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		
		this.experiment = lwSimExperiment;
		this.manager = experiment.manager;
			
		return null;
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toShortString() {
		return "CrossLayerTransmitter";
	}
	
	public String toString() {
		return toShortString() + "_" + index;
	}
	
	private void tryToSend(int output) {

		//if not sending, send!
		if( buffers.get(output).getDepth() > 0) {
			
			//Get front of queue
			Message msg = buffers.get(output).dequeue();
			
			//Create new event and send
			Evt newEvent = new Evt(experiment.getSimTimeNS(), this, nextDest, 0, msg);
			manager.queueEvent(newEvent);
		} else if( buffers.get(output).getDepth() <= 0 ) {
			throw new SimulationException("Virtual output buffer " + output + " has depth " 
						+ buffers.get(output).getDepth() + " on grant, arbiter shoudln't allow this to happen.");
		}
	}

	private void request(Evt e) {
		//TODO: Expose this parameter
		double shiftLatency = 10;
		
		//Issue request to arbiter
		e.getMessage().origin = index;
		Evt req = new Evt(e.getTimeNS() + shiftLatency, this, arbiter, MessageType.REQUEST, e.getMessage());
		manager.queueEvent(req);
	}
	
	@Override
	public void processEvent(Evt e) {
		/**
		 * Pre: Grant message has output ID in message dest
		 */
		if( e.getType() == MessageType.GRANT ) {
			tryToSend(e.getMessage().dest);
		} else {			
			//Enqueue all incoming messages
			buffers.get(e.getMessage().dest).enqueue(e.getMessage());
			request(e);
		}		
	}

}
