package edu.columbia.lrl.experiments.spinet.variants;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;


public class ACK_Receiver extends Receiver {
	
	double reactionTime = -1;

	public ACK_Receiver(int index, double reactionTime) {
		super(index);
		this.reactionTime = reactionTime;
	}
	
	@Override
	public Receiver getReceiverCopy(int index) {
		return new ACK_Receiver(index,  reactionTime);
	}
	
	@Override
	public void processEvent(Evt e) {
		Message m = e.getMessage();
		if (reactionTime < 0)
			throw new IllegalStateException("Reaction time of ACK_receiver should be set to a non-negative value prior to run simulation");
		if (m.dest != index)
			throw new IllegalStateException("Wrong index at receiver");
		lwSimExperiment.packetReceived(m, m.origin, m.dest, m.timeEmitted, e.getTimeNS());
	//	System.out.println("At ACK receiver "+m.index+" @ "+e.getTimeNS());
	/*	if (m.index == 6  && m.dest == 3) 
		System.out.println("Pkt " + m.index + " received at " + e.getTimeNS()+ " destination: "+ m.dest);*/
		
		double elapsedTime = ACK_or_NACK_Sender.getElapsedTimeNS(e);
		Evt next = new Evt(e.getTimeNS() + elapsedTime + reactionTime, this, ACK_or_NACK_Sender.getFeedbackDestination(e), 3);
		next.setMessage(e.getMessage());
		lwSimExperiment.manager.queueEvent(next);
	}
	
}
