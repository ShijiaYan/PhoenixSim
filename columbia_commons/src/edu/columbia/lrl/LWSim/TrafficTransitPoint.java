package edu.columbia.lrl.LWSim;

public interface TrafficTransitPoint extends TrafficDestination, EventOrigin {

	public void setTrafficDestination(TrafficDestination sw);
	
	/**
	 * This method has no effect on the link behavior, but can be used to ease tracking the events for debug
	 * @param origin
	 */
	public void setTrafficOrigin(EventOrigin origin);	

}
