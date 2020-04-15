package edu.columbia.lrl.experiments.spinet.variants;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;

public class PriorityAwareTwoTwoSwitch extends NACK_TwoTwoSwitch {
	
	private static int idCpt = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpinetMessage lastMsgGoingDown;
	private SpinetMessage lastMsgGoingUp;
	
	private double busyGoingDown;
	private double busyGoingUp;
	
	public PriorityAwareTwoTwoSwitch(String id, double switchingTime,
			double nackReactionTime, boolean doubleWay) {
		super(id, switchingTime, nackReactionTime, doubleWay);
		if (id.equals("")) {
			this.id = idCpt+"";
			idCpt++;
		}
	}
	
	/*private void resetDropped(Evt e) {
		if (lastMsgGoingDown!=null && lastMsgGoingDown.dropped && busyGoingDown > e.getTimeNS()) {
			if (lastMsgGoingUp!=null && lastMsgGoingUp.dropped && busyGoingUp > e.getTimeNS()) {
				stateFixedUntil = 0;
			}		
		}
	}*/
	
	@Override
	protected boolean tryUp(SpinetMessage msg, Evt e) {
		//resetDropped(e);
		double arrival = e.getTimeNS();
		boolean fromUp = e.getOrigin().equals(upOrig);
		if (fromUp) { // up --> up  case
			
			// 1 : switch is straight
			if (!stateCrossed) {
				acceptUpUp(msg, e);
				return true;
			}
			// 2 : switch is crossed but can be changed in time
			if (stateFixedUntil + switchingTime < arrival) {
				acceptUpUp(msg, e);
				return true;
			}
			
			// 3 : packet going from down is lower priority
			if (lastUpValid(e)) {
				if (lastMsgGoingUp.spinetPriority < msg.spinetPriority) {
					((SpinetMessage)lastMsgGoingUp).dropped = true;
					packetDropped(lastMsgGoingUp, e.getTimeNS(), -2);
					acceptUpUp(msg, e);
					return true;
				}				
			} else {
				// invalid
				acceptUpUp(msg, e);
				return true;				
			}				
		} else { // down --> up case
			// 1 : switch is crossed
			if (stateCrossed) {
				acceptDownUp(msg, e);
				return true;
			}
			// 2 : switch is straight but can be changed in time
			if (stateFixedUntil + switchingTime < arrival) {
				acceptDownUp(msg, e);
				return true;
			}
			
			// 3 : packet going from down is lower priority
			if (lastUpValid(e)) {
				if (lastMsgGoingUp.spinetPriority < msg.spinetPriority) {
					((SpinetMessage)lastMsgGoingUp).dropped = true;
					packetDropped(lastMsgGoingUp, e.getTimeNS(), -2);
					acceptUpUp(msg, e);
					return true;
				}				
			} else {
				// invalid
				acceptUpUp(msg, e);
				return true;				
			}			
		}
		return false;		
	}
	
	private boolean lastDownValid(Evt e) {
		return (lastMsgGoingDown != null && !lastMsgGoingDown.dropped && busyGoingDown > e.getTimeNS());
	}
	
	private boolean lastUpValid(Evt e) {
		return (lastMsgGoingUp != null && !lastMsgGoingUp.dropped && busyGoingUp > e.getTimeNS());
	}
	
	@Override
	protected boolean tryDown(SpinetMessage msg, Evt e) {
		//resetDropped(e);
		double arrival = e.getTimeNS();
		boolean fromUp = e.getOrigin().equals(upOrig);
		if (fromUp) { // up --> down  case		
			// 1 : switch is crossed
			if (stateCrossed) {
				acceptUpDown(msg, e);
				return true;
			}
			// 2 : switch is straight but can be changed in time
			if (stateFixedUntil + switchingTime < arrival) {
				acceptUpDown(msg, e);
				return true;
			}
			// 3 : packet going from down is lower priority
			if (lastDownValid(e)) {
				if (lastMsgGoingDown.spinetPriority < msg.spinetPriority) {
					((SpinetMessage)lastMsgGoingDown).dropped = true;
					packetDropped(lastMsgGoingDown, e.getTimeNS(), -2);
					acceptUpDown(msg, e);
					return true;
				}				
			} else {
				// invalid
				acceptUpDown(msg, e);
				return true;				
			}
		} else { // down --> down case
			// 1 : switch is straight
			if (!stateCrossed) {
				acceptDownDown(msg, e);
				return true;
			}
			// 2 : switch is straight but can be changed in time
			if (stateFixedUntil + switchingTime < arrival) {
				acceptDownDown(msg, e);
				return true;
			}
		
			// 3 : packet going from down is lower priority
			if (lastDownValid(e)) {
				if (lastMsgGoingDown.spinetPriority < msg.spinetPriority) {
					((SpinetMessage)lastMsgGoingDown).dropped = true;
					packetDropped(lastMsgGoingDown, e.getTimeNS(), -2);
					acceptDownDown(msg, e);
					return true;
				}				
			} else {
				// invalid
				acceptDownDown(msg, e);
				return true;				
			}
		}
		return false;		
	}
	
	@Override
	protected void accept(SpinetMessage msg, Evt e, boolean toUp, boolean crossed) {
		stateFixedUntil = e.getTimeNS() + msg.getTransmissionTimeNS();		
		stateCrossed = crossed;
		if (toUp) {
			lastMsgGoingUp = (SpinetMessage)e.getMessage();
			busyGoingUp = stateFixedUntil;
		} else {
			lastMsgGoingDown = (SpinetMessage)e.getMessage();
			busyGoingDown = stateFixedUntil;
		}
	}	
	
	

}
