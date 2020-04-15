package edu.columbia.lrl.experiments.spinet.variants;

import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;



public class NACK_TwoTwoSwitch extends TwoTwoSwitch {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double reactionTime = -1;
	
	public NACK_TwoTwoSwitch(String id, double switchingTime, double nackReactionTime, boolean doubleWay) {
		super(id, switchingTime, doubleWay);
		reactionTime = nackReactionTime;
	}	
	
	@Override
	protected void packetDropped(Message m, double time, int type) {
		if (reactionTime < 0)
			throw new IllegalStateException("Reacion time of the NACK_Switch has not been set, set it before running the simulation");
		
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(m, time);
		
		if (20 < elapsedTime) {
			@SuppressWarnings("unused")
			int eiruh = 0;
		}
		
		m.setLeastTimeToWait(stateFixedUntil-time+switchingTime);
		Evt next = new Evt(time + elapsedTime + reactionTime, this, ACK_or_NACK_Sender.getFeedbackDestination(m), 3);
		next.setMessage(m);
		lwSimExperiment.manager.queueEvent(next);
		lwSimExperiment.packetDropped(m, toString(), this, type);
	//	if (e.getMessage().dest == 2 && e.getMessage().origin == 30 && (e.getMessage().index % 10 == 0)) {
		//	System.out.println(" sending NACK at " + time +
		//		" for message m:" + m.index + " orig-dest "+ m.origin + "-->"+ m.dest+ " (NACK will arrive at " + (time + elapsedTime + reactionTime));
	//	}
	}
}
