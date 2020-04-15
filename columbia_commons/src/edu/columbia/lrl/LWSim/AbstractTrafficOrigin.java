package edu.columbia.lrl.LWSim;

public abstract class AbstractTrafficOrigin implements EventOrigin {
	
	protected TrafficDestination nextDest;

	public void setTrafficDestination(TrafficDestination nextDest) {
		this.nextDest = nextDest;
	}
	
	public TrafficDestination getTrafficDestination() {
		return nextDest;
	}
	
}
