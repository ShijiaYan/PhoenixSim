package edu.columbia.lrl.experiments.spinet.variants;

import java.util.HashSet;
import java.util.Iterator;

import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.experiments.spinet.ImprovedBuffer;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;



public class ACK_Buffer extends ImprovedBuffer implements ACK_or_NACK_Receiver {
	
	private double timeOut = -1;
	private HashSet<Message> receivedAcks = new HashSet<Message>();
	
	public ACK_Buffer(int maxSize, double bufferLatency, double spinetSwitchingTimeNS, int index, double timeOut) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.timeOut = timeOut;
	}
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			// BIRTH of the process
			sendMessage(e, fifo.poll());		
		} else if (e.getType() == 2) {
			// timeout received
			if (!receivedAcks.remove(e.getMessage())) {
				// message has not been acknowledged, re-add it to the queue, using its number as index
				if (fifo.size() == 0) {
					fifo.add(e.getMessage());
				} else {
					int index = 0;
					for (Iterator<Message> it = fifo.iterator() ; it.hasNext() ; ) {
						Message m = it.next();
						if (m.index > e.getMessage().index) {
							break;
						}
						index++;
					}
					fifo.add(index, e.getMessage());
					lwSimExperiment.packetRetransmitted(e.getMessage());
				}
				testAndPotentiallyBootstrapConsumetProcess(e);
			}
		} else {
			// ACK received, add the message in the acked list
			receivedAcks.add(e.getMessage());
		}
	}	
	
	protected void sendMessage(Evt e, Message toSend) {
		if (timeOut < 0) 
			throw new IllegalStateException("Timeout must be set to a non-negative value prior to run simulation");
		// mark sending time in packet
		toSend.object = new Pair<Double, ACK_or_NACK_Receiver>(e.getTimeNS(), this);
		super.sendMessage(e, toSend);	
		
		//for timeline of packet:
		Message m = toSend;
	   if (m.index <10 )// == 6  && m.dest == 3) 
			System.out.println("Pkt " + m.index + " leaves ACK_buffer at " + e.getTimeNS() + " from "+m.origin +" to destination: "+ m.dest);
		/*if(m.origin==0 && m.index ==  7){
			System.out.println("Pkt " + m.index + " leaves ACK_buffer at " + e.getTimeNS() + " destination: "+ m.dest);
		}*/
		// timeout event
		Evt self = new Evt(e.getTimeNS()+timeOut, this, this, 2, e);
		self.setMessage(toSend);
		lwSimExperiment.manager.queueEvent(self);			

	}
}
