package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Sender;



public class DA_NACK_TwoTwoSwitch extends TwoTwoSwitch {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double reactionTime = -1;
	protected double packetTransTime;
//	protected double lamda;
	protected int deferThreshold = 10;
	
	public DA_NACK_TwoTwoSwitch(String id, double switchingTime, double nackReactionTime, boolean doubleWay) {
		super(id, switchingTime, doubleWay);
		reactionTime = nackReactionTime;
	}	
	
	/* added for deadline experiment, Ke will remove it asap */ 
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();	
//		lamda = this.lwSimExperiment.getNormalisedLoad();
		return null;
	}
	/* added for deadline experiment, Ke will remove it asap */ 


	@Override
	protected void packetDropped(Message m, double time, int type) {
		if (reactionTime < 0)
			throw new IllegalStateException("Reacion time of the NACK_Switch has not been set, set it before running the simulation");
		
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(m, time);
		SpinetMessage msg = (SpinetMessage)m;
		elapsedTime = elapsedTime - msg.getSwitchPassed()*msg.getOffsetDurationNS();
		if (elapsedTime < 0)
			throw new IllegalStateException("elapsedTime=0");
		
		if (20 < elapsedTime) {
			@SuppressWarnings("unused")
			int eiruh = 0;
		}
		
		/* added for deadline experiment, Ke will remove it asap */ 
		double addBubble = 0;
		/*if (this.lastSuccessPacketDeadline > 0) {
			double deadline = m.getDeadline();
			double expectedPackets = lamda
					* (deadline - time)
					/ this.packetTransTime;

			if (expectedPackets > this.deferThreshold)
				addBubble = this.packetTransTime;
		//}
		/* added for deadline experiment, Ke will remove it asap */ 
		
		m.setLeastTimeToWait(stateFixedUntil-time+switchingTime+addBubble);
		
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
