package edu.columbia.ke.component;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Sender;

public class NACK_HighRadixSwitch extends HighRadixSwitch {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double reactionTime;
	private int deferThreshold = 8;

	public NACK_HighRadixSwitch(int id, int nbNode, double switchingTime, double linkLatency, double nackReactionTime) {
		super(id, nbNode, switchingTime, linkLatency);
		reactionTime = nackReactionTime;
	}
	
	@Override
	protected void packetDropped(Message m, Evt e, double time, int type, double smallestLWT) {
		if (reactionTime < 0)
			throw new IllegalStateException("Reacion time of the NACK_Switch has not been set, set it before running the simulation");
		
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(m, time);
		
		if (20 < elapsedTime) {
			@SuppressWarnings("unused")
			int eiruh = 0;
		}
		
		/* added for deadline experiment, Ke will remove it asap  
		double addBubble = 0;
		
			double deadline = m.getDeadline();
			double expectedPackets = (deadline - lastDeadline) / this.packetTransTime;

			if (expectedPackets > this.deferThreshold )
				addBubble = this.packetTransTime/4;
		
		/* added for deadline experiment, Ke will remove it asap */ 
	
		
		m.setLeastTimeToWait(smallestLWT);
		
		Evt next = new Evt(time + elapsedTime + reactionTime, this, ACK_or_NACK_Sender.getFeedbackDestination(m), 3);
		next.setMessage(m);
		lwSimExperiment.manager.queueEvent(next);
		lwSimExperiment.packetDropped(m, toString(), this, type);
	}

}
