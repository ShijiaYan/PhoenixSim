package edu.columbia.lrl.CrossLayer.simulator.components;

import java.util.Collection;
import java.util.Map;

import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractSwitchFabric;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.EventTarget;

public abstract class Arbiter implements TrafficDestination, EventOrigin {
	
	protected AbstractSwitchFabric fabric;
	protected AbstractReservationStrategy reservation;
	protected int numClients;
	
	public Arbiter(AbstractReservationStrategy reservation) {
		this.reservation = reservation;
	}
	
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getArbiterParameters();
		m.putAll(reservation.getAllParameters());
		m.put("Reservation strategy", reservation+"");
		return m;
	}
	
	public void setNumOutputs(int numOutputs) {
		this.numClients = numOutputs;
	}
	
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		reservation.init(fabric.getPaths(), /*fabric.getSwitchStates(), fabric.getSwitchIDs(),*/ lwSimExperiment);
		return null;
	}
	
	public abstract Map<String, String> getArbiterParameters();
	public abstract void registerInput(EventTarget target, int index);
	///public abstract void registerOutput(EventTarget target, int index);
	public abstract String toString();
	public abstract void clear();
	public abstract Transmitter getTransmitter(int i, Collection<Integer> destinations);
	
	public AbstractSwitchFabric getSwitchFabric() {
		return fabric;
	}
}
