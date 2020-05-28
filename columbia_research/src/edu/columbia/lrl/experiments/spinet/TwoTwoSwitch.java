package edu.columbia.lrl.experiments.spinet;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.LWSim.components.AbstractSwitch;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TwoTwoSwitch extends AbstractSwitch implements SpinetComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DIRECTIONS {
		UP, DOWN
	}

	// defined at init	
	protected TrafficDestination upDest;
	protected TrafficDestination downDest;	
	protected EventOrigin upOrig;
	protected EventOrigin downOrig;		
	protected LWSIMExperiment lwSimExperiment;
	protected AbstractSpinetBuilder builder;
//	protected int position;	// vertical position, on the same stage
//	protected int rank; // on which stage is this switch
	protected String id;
	protected double switchingTime;
	//private boolean doubleWay;
	protected int[] routingDecisions;		
	
	// working variable
	protected double stateFixedUntil;
	protected boolean stateCrossed;
	private Message[] OutputOccupyingMsgs;
	private double[] perOutputFixedUntil;
	
	
	public TwoTwoSwitch(String id, double swi, boolean doubleWay) {
		this.id = id;
		this.switchingTime = swi;
		this.OutputOccupyingMsgs = new Message[2];
		this.perOutputFixedUntil = new double[2];
	}
	
	public TwoTwoSwitch(double swi) {
		this("anonymousSwitch", swi, true);
	}
	
	public void setRoutingDecisions(int[] decisions) {
		routingDecisions = decisions;
	}
	
	public void setTrafficDestination(TrafficDestination dest, int index) {
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
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;	
		return null;
	}
	
	@Override
	public void setSpinetBuilder(AbstractSpinetBuilder builder) {
		this.builder = builder;
	}	
	
	private void packetExistenceCheck(Evt e){
		double now = e.getTimeNS();
		if (this.stateFixedUntil < now)
			return;

		double biggerFixedUntil = 0;
		for (int i = 0; i < 2; i++) {
			if (this.perOutputFixedUntil[i] > biggerFixedUntil) 
				biggerFixedUntil = this.perOutputFixedUntil[i];
		}
			
		this.stateFixedUntil = biggerFixedUntil;
	}
	
	public void processEvent(Evt e) {	
		//release switch
		if (e.getType()==9)	{
			releasePort(e);
			// check if switch state is up-to-date
			this.packetExistenceCheck(e);
			return;
		}
		
		
		double arrivalTime = e.getTimeNS();	
		SpinetMessage msg = (SpinetMessage)e.getMessage();		
		int type = getTransmissionType(msg, e);
			// packet is granted if :
			// 	1) arrivalTime is after the last instant the state is fixed
			//  2) if switch is doubleway, if the actual state of the switch is compatible
		if (type > 0) {
			// here the switch decrease the offset only if accepted
			msg.decrementOffset();
			msg.enqueOccupyResource(this);
			Evt next;
			if (type == 1) {
				next = new Evt(arrivalTime+switchingTime, this, upDest, e);
			} else {
				next = new Evt(arrivalTime+switchingTime, this, downDest, e);
			}
			lwSimExperiment.manager.queueEvent(next);
		} else {
			// dropped due to contention, use type = 1
			packetDropped(msg, arrivalTime, 1);
		}
	}
	
	private void releasePort(Evt e) {
		Message m = e.getMessage();
		if (m==null)
			throw new IllegalStateException("Releasing msg == null");
		for (int i = 0; i < 2; i++) {
			if (this.OutputOccupyingMsgs[i] == m) {
				this.perOutputFixedUntil[i] = e.getTimeNS();
				this.OutputOccupyingMsgs[i] = null;
				break;
			}
		}
	}

	/*
	 * Return 1 if up
	 *        2 if down
	 *        0 if rejected
	 */
	protected int getTransmissionType(SpinetMessage msg, Evt e) {
		int toward = routingDecisions[msg.dest];
		if (toward == 2) toward += lwSimExperiment.getRandomStreamForEverythingButTraffic().nextInt(0, 1);
		switch (toward) {
			case 0: // packet must go up
				if (tryUp(msg, e))
					return 1;
				else
					return -1;
			case 1: // packet must go down
				if (tryDown(msg, e)) 
					return 2;
				else
					return -2;
			case 2:
				if (tryUp(msg, e)) {
					return 1;
				} else if (tryDown(msg, e)) {
					return 2;
				} else {
					return -3;
				}
			case 3:
				if (tryDown(msg, e)) {
					return 2;
				} else if (tryUp(msg, e)) {
					return 1;
				} else {
					return -3;
				}
		}
		throw new IllegalStateException();				
	}
	 
	
	protected boolean tryUp(SpinetMessage msg, Evt e) {
		double arrival = e.getTimeNS();
		boolean fromUp = e.getOrigin().equals(upOrig);
		if (fromUp) { // up --> up  case
			// sanity check
	//		if (busyUntilUp - arrival > 1e-8 && !stateCrossed && !e.getMessage().equals(lastEventForUp.getMessage())) throw new IllegalStateException();
			
				// case 1: switch is already in the non crossed state
			if (stateCrossed == false || 
				// case 2: switch is in the crossed state and thus must be changed
				stateFixedUntil + switchingTime < arrival) {
			    acceptUpUp(msg, e);
			    //lastEventForUp = e;
			    return true;
			}
			
		} else { // down --> up case
			// sanity check
	//		if (busyUntilUp - arrival > 1e-8 && stateCrossed  && !e.getMessage().equals(lastEventForUp.getMessage())) throw new IllegalStateException();
			
				// case 1: switch is already in the crossed state
			if (stateCrossed ||
				// case 2: switch is in the straight state and thus must be changed
				stateFixedUntil + switchingTime < arrival) {
				acceptDownUp(msg, e);
				//lastEventForUp = e;
				return true;
			}
		}
		return false;		
	}
	
	protected boolean tryDown(SpinetMessage msg, Evt e) {
		double arrival = e.getTimeNS();
		boolean fromUp = e.getOrigin().equals(upOrig);
		if (fromUp) { // up --> down  case
			// sanity check
		//	if (busyUntilDown - arrival > 1e-8 && stateCrossed && !e.getMessage().equals(lastEventForDown.getMessage())) throw new IllegalStateException();
			
			// case 1: switch is already in the crossed state
			if (stateCrossed ||
			// case 2: switch is in the straight state and thus must be changed
				stateFixedUntil + switchingTime < arrival) {
				acceptUpDown(msg, e);
				//lastEventForDown = e;
				return true;
			}
			
		} else { // down --> down case
			// sanity check
	//		if (busyUntilDown - arrival > 1e-7 && !stateCrossed && !e.getMessage().equals(lastEventForDown.getMessage())) throw new IllegalStateException();
			
			// case 1: switch is already in the straight state
			if (!stateCrossed ||
			// case 2: switch is in the straight state and thus must be changed
				stateFixedUntil + switchingTime < arrival) {
				acceptDownDown(msg, e);
				//lastEventForDown = e;
				return true;
			}
		}
		return false;		
	}
	
	protected void acceptUpUp(SpinetMessage msg,Evt e) {
		accept(msg, e, true, false);
		this.OutputOccupyingMsgs[0] = msg;
		//System.out.println("msg id:"+msg.index+"  src:"+msg.origin+"  dst:"+msg.dest+" --> 0");
	}

	protected void acceptDownUp(SpinetMessage msg,Evt e) {
		accept(msg, e, true, true);
		this.OutputOccupyingMsgs[0] = msg;
		//System.out.println("msg id:"+msg.index+"  src:"+msg.origin+"  dst:"+msg.dest+" --> 1");
	}
		
	protected void acceptUpDown(SpinetMessage msg,Evt e) {
		accept(msg, e, false, true);
		this.OutputOccupyingMsgs[1] = msg;
		//System.out.println("msg id:"+msg.index+"  src:"+msg.origin+"  dst:"+msg.dest+" --> 1");
	}
	
	protected void acceptDownDown(SpinetMessage msg,Evt e) {
		accept(msg, e, false, false);
		this.OutputOccupyingMsgs[1] = msg;
		//System.out.println("msg id:"+msg.index+"  src:"+msg.origin+"  dst:"+msg.dest+" --> 0");
	}	
	
	protected void accept(SpinetMessage msg, Evt e, boolean toUp, boolean crossed) {
	//	SpinetMessage msg = ((SpinetMessage)e.getMessage());
		double until = e.getTimeNS() + msg.getTransmissionTimeNS();
		int outputIndex = 1;
		if (toUp)
			outputIndex = 0;
		this.perOutputFixedUntil[outputIndex] = until;
		stateFixedUntil = until;		
		stateCrossed = crossed;
	}	
	
	protected void packetDropped(Message m, double time, int type) {
		// by default, the drops at the switch are due to contention
		// dropType = 1
		lwSimExperiment.packetDropped(m, toString(), this, type);
	//	log("switch_dropping", m.origin + (double)m.dest/10, time);			
	}
	
/*	protected void log(String result, double value, double time) {
		lwSimExperiment.log("switch_id", id, result, value, time);		
	}*/
	
	public String toShortString() {
		return "2x2 switch";
	}
	
	public String toString() {
		return "switch " + id;
	}
	
	public String getMapping() {
		return 	toString() + ":\n" + String.format("%1$20s", upOrig) + "--\\_/--" + String.format("%1$-20s", upDest) + "\r\n" +
				String.format("%1$20s", downOrig) + "--/ \\--" + String.format("%1$-20s", downDest);
	}

	@Override
	public void notifyEnd(double ref, double status) {}


	
}
