package edu.columbia.lrl.experiments.spinet.variants;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;

public class PriorityReceiver extends Receiver {

	public PriorityReceiver(int index) {
		super(index);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Receiver getReceiverCopy(int index) {
		return new PriorityReceiver(index);
	}	
	
	
	
	public void processEvent(Evt e) {
		SpinetMessage m = (SpinetMessage)e.getMessage();
		if (m.dropped == true) return;
		
	//	if (m.index == 6  && m.dest == 3) 
	//	System.out.println("Received " + m.origin + "-->" + m.dest + "  " + m + " at time + " + e.getTimeNS());
		
		/*if (m.index <= getLatest(m.origin)) {
			if (!m.partialData || m.partialIndex <= getLatestPartialIndex(m.index))
				//throw new IllegalStateException("Message received twice or out-of-order");
		}*/
		if (m.dest != index)
			throw new IllegalStateException("Wrong index at receiver");
		setLatest(m.origin, m.index);
		if ((m.partialData == false || m.lastPartial == true)) {
			if (notifiable != null)
				notifiable.objectReceived(m, m.carriedData, m.origin, lwSimExperiment.getReferenceBandwidth().getTime(m.sizeInBits).getNanoseconds() + e.getTimeNS());
			if (m.lastPartial == true)
				m.sizeInBits = m.totalSize;
			lwSimExperiment.packetReceived(m, m.origin, m.dest, m.timeEmitted, e.getTimeNS());
			lastPartial.remove(m.index);
		}
		if (m.partialData == true) {
			setLastPartial(m.index, m.partialIndex);
		}
	}	
	

}
