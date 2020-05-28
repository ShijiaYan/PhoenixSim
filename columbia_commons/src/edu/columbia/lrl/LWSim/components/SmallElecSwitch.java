package edu.columbia.lrl.LWSim.components;

import java.util.TreeMap;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;


/**
 *  This element must first be connected to other elements using the
 *  addDestination method. The first parameter is the destination, the second
 *  parameter is the index of the port to which this destination will be attached
 *  
 *  During simulation, if a message with destination "i" is received, it
 *  is forwarded to the element connected to the "i"th port
 * @author rumley
 *
 */
public class SmallElecSwitch implements TrafficDestination, EventOrigin {
	
	private int portIndex;
	private LWSIMExperiment lwSimExperiment;
	private TreeMap<Integer, LWSimComponent> dests = new TreeMap<>();
	private double processDelay;
	
	public SmallElecSwitch(double processDelay) {
		this(processDelay, -1);
	}
	
	public SmallElecSwitch(double processDelay, int index) {
		this.processDelay = processDelay;
		this.portIndex = index;
	}		
	
	public void addDestination(LWSimComponent dest, int index) {
		dests.put(index, dest);
	}

	@Override
	public void processEvent(Evt e) {
		LWSimComponent d = dests.get(e.getMessage().dest);
		if (d == null) {
			throw new NullPointerException("No destination found for " + e.getMessage());
		}
		lwSimExperiment.manager.queueEvent(new Evt(e.getTimeNS() + processDelay, this, d , e));
	}

	@Override
	public String toShortString() {
		return "Small Router at port" + portIndex;
	}

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}
	
	@Override
	public void notifyEnd(double ref, double status) {	}	
	
}