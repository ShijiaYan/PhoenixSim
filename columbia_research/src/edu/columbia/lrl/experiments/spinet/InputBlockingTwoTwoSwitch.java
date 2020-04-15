package edu.columbia.lrl.experiments.spinet;

import java.nio.channels.IllegalSelectorException;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class InputBlockingTwoTwoSwitch extends TwoTwoSwitch implements SpinetComponent {

	private static final long serialVersionUID = 1L;
	protected LWSIMExperiment lwSimExperiment;	
	protected AbstractSpinetBuilder builder;
	protected int[] routingDecisions;
	protected double switchingTime;
	
	protected TrafficDestination upDest;
	protected TrafficDestination downDest;	
	protected EventOrigin upOrig;
	protected EventOrigin downOrig;		
	
	protected double busyUntilUp;
	protected double busyUntilDown;
	protected double blockedUntil;
	protected boolean stateCrossed;
	
	
	
	public InputBlockingTwoTwoSwitch() {
		super(0);
		// TODO Auto-generated constructor stub
	}
	
	public InputBlockingTwoTwoSwitch(double switchingTime) {
		super(switchingTime);
		this.switchingTime = switchingTime;
	}

	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}
	
	@Override
	public void setSpinetBuilder(AbstractSpinetBuilder builder) {
		this.builder = builder;	
	}
	
	// consider put this method in an upper class (together with TWOTWOSWITCH)
	public void setRoutingDecisions(int[] decisions) {
		routingDecisions = decisions;
	}
	
	public final void setTrafficDestination(TrafficDestination dest, int index) {
		if (index == 0) {
			upDest = dest;
		} else if (index == 1) {
			downDest = dest;
		} else {
			throw new IllegalStateException();
		}
	}
	
	public void setEventOrigin(EventOrigin orig, int index) {
		if (index == 0) {
			this.upOrig = orig;
		} else if (index == 1) {
			this.downOrig = orig;
		} else {
			throw new IllegalStateException();
		}
	}	
	
	@Override
	public void processEvent(Evt e) {
		int dest = routingDecisions[e.getMessage().dest];
		if (dest == 0) {
			if (busyUntilUp > e.getTimeNS()) {				
				drop(e, upDest);
				return;
			}
			if (e.getOrigin() == upOrig) {
				if (stateCrossed && blockedUntil > e.getTimeNS() + switchingTime) {
					drop(e, upDest);
					return;
				}
				acceptUp(e, false);
			} else {
				if (!stateCrossed && blockedUntil > e.getTimeNS() + switchingTime) {
					drop(e, upDest);
					return;
				}
				acceptUp(e, true);
			}
				
		}
		if (dest == 1) {
			if (busyUntilDown > e.getTimeNS()) {				
				drop(e, downDest);
				return;
			}
			if (e.getOrigin() == downOrig) {
				if (stateCrossed && blockedUntil > e.getTimeNS()  + switchingTime) {
					drop(e, downDest);
					return;
				}
				acceptDown(e, false);
			} else {
				if (!stateCrossed && blockedUntil > e.getTimeNS() + switchingTime) {
					drop(e, downDest);
					return;
				}
				acceptDown(e, true);
			}
		}
		if (dest < 0 || dest > 1) {
			throw new IllegalSelectorException();	
		}
	}
	
	private void acceptUp(Evt e, boolean crossed) {
		SpinetMessage msg = ((SpinetMessage)e.getMessage());
		stateCrossed = crossed;
		busyUntilUp = e.getTimeNS() + msg.getTransmissionTimeNS();
		blockedUntil = busyUntilUp;
		Evt next = new Evt(e.getTimeNS(), this, upDest, e);
		lwSimExperiment.manager.queueEvent(next);
	}
	
	private void acceptDown(Evt e, boolean crossed) {
		SpinetMessage msg = ((SpinetMessage)e.getMessage());
		stateCrossed = crossed;
		busyUntilDown = e.getTimeNS() + msg.getTransmissionTimeNS();
		blockedUntil = busyUntilDown;
		Evt next = new Evt(e.getTimeNS(), this, downDest, e);		
		lwSimExperiment.manager.queueEvent(next);		
	}
	
	private void drop(Evt e, TrafficDestination dest) {
		lwSimExperiment.packetDropped(e.getMessage(), "switch", dest, 0);
	}

	@Override
	public String toShortString() {
		return "InputBswitch";
	}
	
	@Override
	public void notifyEnd(double ref, double status) {}	
	

}
