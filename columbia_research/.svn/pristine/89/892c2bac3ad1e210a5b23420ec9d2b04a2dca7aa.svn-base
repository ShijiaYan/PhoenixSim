package edu.columbia.ke.component.burst_assembly;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class BurstReceiver extends Receiver {

	public BurstReceiver(int index) {
		super(index);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void processEvent(Evt e) {
		Message m = e.getMessage();		
		if (e.getType() == 0) {
		//	if (m.index == 6  && m.dest == 3) 
		//	System.out.println("Receiver: " + index + " Pkt " + m.index + " received at " + e.getTimeNS()+ " destination: "+ m.dest);
			/*if (m.index <= getLatest(m.origin))
				throw new IllegalStateException("Message received twice or out-of-order");*/
			if (m.dest != index)
				throw new IllegalStateException("Wrong index at receiver");
			
			double now = e.getTimeNS();
			/*
			 * A burst may carry multiple messages
			 * need to receive them one by one
			 */
			BurstMessage b = (BurstMessage)m;
			for (Message item: b.msgList)
				lwSimExperiment.packetReceived(item, item.origin, item.dest, item.timeEmitted, now);
			
			setLatest(m.origin, m.index);
			log(e);
			if (notifiable != null) {
				Evt newEv = new Evt(m.lastDuration + e.getTimeNS(), this, this, 1, e);
				lwSimExperiment.manager.queueEvent(newEv);
			}
		} else {
			notifiable.objectReceived(m, m.carriedData, m.origin, m.lastDuration + e.getTimeNS());

		}
		
	}

}
