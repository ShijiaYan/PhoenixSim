package edu.columbia.ke.spinet.variant;

import java.util.Iterator;
import java.util.Map;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.experiments.spinet.variants.ACK_or_NACK_Receiver;

public class NACK_PrioritizedBuffer extends PrioritizedBuffer implements ACK_or_NACK_Receiver {
	
	protected Message currentlyTransmitted;
	protected Map.Entry currentEntry;
	protected boolean nackReceived = false;
	protected PriorityMode priorityMode;
	protected Message justNACKed;
	protected Map.Entry justNACKedEntry;
	protected double leastWaitTime;

	public NACK_PrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.priorityMode = pm;
	}
	
	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 1) {
			/*if (e.getTimeNS() <= busyUntil) {
				// wait till finishing the last packet
				Evt self = new Evt(busyUntil+1, this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
			} else*/ {
				// BIRTH of the process
				if (priorityQueue.size() > 0) {
					Iterator currentIterator;
					currentIterator = priorityQueue.entrySet().iterator();
					currentEntry = (Map.Entry) currentIterator.next();
					currentlyTransmitted = (Message) currentEntry.getValue();
					if (priorityQueue.size() > 1) {
						switch (this.priorityMode) {
						case EDF_RETRAN_YIELD:
							if (currentlyTransmitted == justNACKed) {
								// Get the second one
								currentEntry = (Map.Entry) currentIterator
										.next();
								currentlyTransmitted = (Message) currentEntry
										.getValue();
							}
							break;
						case EDF:
							break;
						default:
							break;
						}
					}
					sendMessage(currentlyTransmitted, e);
				}
			}
		} else if (e.getType() == 2) {
			receiveEndOfTransmissionEvent(e);
		} else if (e.getType() == 3){
			receiveNack(e);
		} else {
			throw new IllegalStateException("received unknown event type: "+e.getType());
		}
	}
	
	protected void receiveEndOfTransmissionEvent(Evt e) {
		if (nackReceived) {
			// I don't resend the dropped packet immediately
			// sendMessage(currentlyTransmitted, e);
			lwSimExperiment.packetRetransmitted(currentlyTransmitted);
		} else {
			this.priorityQueue.entrySet().remove(currentEntry);
		}
		
		// trial for safety
		this.currentEntry = null;
		this.currentlyTransmitted = null;

		// rebirth
		if (priorityQueue.size() > 0) {
			Evt self = new Evt(e.getTimeNS(), this, this, 1, e);
			lwSimExperiment.manager.queueEvent(self);
		}

	}
	
	protected void receiveNack(Evt e) {
		if (e.getMessage()!= currentlyTransmitted) {
			throw new IllegalStateException();
		}
		nackReceived = true;	
		justNACKed = currentlyTransmitted;
		justNACKedEntry = this.currentEntry;
	}
	
	protected void sendMessage(Message toSend, Evt e) {
	/*	if (toSend.index == 0) {
			System.out.println("Nack_buffer:" + this.index + " pkt " + toSend.index + " sent from buffer at time " + e.getTimeNS());
		}	*/
		double startTime = e.getTimeNS();
		/*if (e.getTimeNS() <= busyUntil) {
			startTime = busyUntil;
		}*/
			
		justNACKed = null;
		justNACKedEntry = null;
		leastWaitTime  = 0;
		nackReceived = false;
		// mark sending time in packet
		toSend.incNumTrans();
		toSend.object = new Pair<Double, ACK_or_NACK_Receiver>(startTime, this);
		Evt next = new Evt(startTime, this, nextDest, /* test */ e);
		next.setMessage(toSend);
		defineSpinetMessage(next);
		lastPacketStart = startTime;
		lwSimExperiment.manager.queueEvent(next);	
		lwSimExperiment.packetTransmitted(toSend);
		
		busyUntil = getEOTTimeNS(toSend, startTime);	
		Evt self = new Evt(busyUntil, this, this, 2, e);	
		self.setMessage(toSend);
		lwSimExperiment.manager.queueEvent(self);			
	}

}
