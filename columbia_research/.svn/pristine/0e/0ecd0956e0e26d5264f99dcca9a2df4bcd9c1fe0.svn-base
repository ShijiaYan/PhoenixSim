package edu.columbia.lrl.experiments.spinet.variants;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;

public class EnhancedTDMSpinnetBuffer extends TDMSpinnetBuffer implements ACK_or_NACK_Receiver {
	
	// property
	private boolean noLongPackets;
	
	private double globalHorizon;
	
	private boolean dying;
	
	private int slotId;
	
	private HashMap<Integer, LinkedList<SpinetMessage>> queues;
	private LinkedList<SpinetMessage> mainQueue;
	
	private SpinetMessage currentlyOptimisticallyTransmitted;


	private boolean nackReceived;
	private boolean ACKMode;

	public EnhancedTDMSpinnetBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, double slotDuration, int index, boolean noLongPackets) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, slotDuration,
				index);
		
		ACKMode = false;
		mainQueue = new LinkedList<SpinetMessage>();
		queues = new HashMap<Integer, LinkedList<SpinetMessage>>();
		
		slotId = 0;
		nackReceived = false;
		this.noLongPackets = noLongPackets;
	}
	
	public EnhancedTDMSpinnetBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, double slotDuration, int index, boolean noLongPackets, boolean ACK_Mode) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, slotDuration,
				index);
		
		ACKMode = ACK_Mode;
		mainQueue = new LinkedList<SpinetMessage>();
		queues = new HashMap<Integer, LinkedList<SpinetMessage>>();
		
		slotId = 0;
		nackReceived = false;
		this.noLongPackets = noLongPackets;
	}
	
	@Override 
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		Evt e = new Evt(0, this, this, 2);
		lwSimExperiment.manager.queueEvent(e);
		return super.initComponent(lwSimExperiment);
	}
	
	private LinkedList<SpinetMessage> getQueue(int dest) {
		LinkedList<SpinetMessage> q = queues.get(dest);
		if (q == null) {
			q = new LinkedList<SpinetMessage>();
			queues.put(dest, q);
		}
		return q;
	}
	
	public void processEvent(Evt e) {
		if (e.getType() == 0) {
			SpinetMessage m = defineSpinetMessage(e);
			if (noLongPackets && m.getTransmissionTimeNS() > slotDuration)
				throw new IllegalStateException("Trying to sent a packet longer than the slot (slot:" + slotDuration + " - packet: " + m.getTransmissionTimeNS() +")");
		
			getQueue(m.dest).addLast(m);
			mainQueue.addLast(m);
		} else if (e.getType() == 2){
			// slot start
			potentiallySend(e);
		} else {
			receiveNack(e);
		}
	}
	
	public String toString() {
		return "enb" + index;
	}
	
	private double packetDuration;
	
	public void potentiallySend(Evt e) {
		try {
			// schedule next potentiallySend
			if (!dying) {
				Evt self = new Evt(e.getTimeNS()+slotDuration, this, this, 2);
				lwSimExperiment.manager.queueEvent(self);
			}
			
			// first, check if current packet is still being sent. if yes, do nothing
			if (e.getTimeNS() < globalHorizon) return;
			if (mainQueue.size() == 0) return;
			
			// if currentlyoptimistically transmitted is not null, and NACKreceived is false, packet is fine
			if (currentlyOptimisticallyTransmitted != null && nackReceived == false) {
				LinkedList<SpinetMessage> correspondingToCurrentlty = getQueue(currentlyOptimisticallyTransmitted.dest);
				if (correspondingToCurrentlty.pollFirst() != currentlyOptimisticallyTransmitted) {
					throw new IllegalStateException("Another message is expected here");
				}
				if (lwSimExperiment.isWithTimeLine()) {
					@SuppressWarnings("unchecked")
					double time = ((Pair<Double, ?>)currentlyOptimisticallyTransmitted.object).getFirst();
					timeline.addJobPhase(time, time + currentlyOptimisticallyTransmitted.getTransmissionTimeNS(), "m:" + currentlyOptimisticallyTransmitted.index + "\r\n->" + currentlyOptimisticallyTransmitted.dest, Color.BLUE);
				}
				currentlyOptimisticallyTransmitted = null;
			}
			
			// check in priority the queue corresponding to current slot
			LinkedList<SpinetMessage> correspondingQueue = null;
			Integer dest = null;
			if (slotId%slotNumber < destinationForSlot.size())
				dest = destinationForSlot.get(slotId%slotNumber);
			else
				throw new IllegalStateException("");
			if (dest != null) { // slot can be unassigned
				correspondingQueue = getQueue(dest);
			}	
			SpinetMessage toSend;
			if (correspondingQueue != null && correspondingQueue.size() > 0) {
				
				toSend = correspondingQueue.pollFirst();
				mainQueue.remove(toSend);
				toSend.spinetPriority = 1;
				
				if (lwSimExperiment.isWithTimeLine())
					timeline.addJobPhase(e.getTimeNS(), e.getTimeNS() + toSend.getTransmissionTimeNS(), "m:" + toSend.index  + "\r\n->" + toSend.dest, Color.GREEN);
			//	System.out.println(index + "-->" + toSend.dest + " SENDING " + toSend  + " " + e.getTimeNS());
			} else {
				// optimistically send the first message of the fifo
				toSend = mainQueue.pollFirst();
				toSend.spinetPriority = 0;
				currentlyOptimisticallyTransmitted = toSend;
			//	System.out.println(index + "-->" + toSend.dest + " sending " + toSend  + " " + e.getTimeNS());
			}
			nackReceived = false;
			toSend.dropped = false;
			toSend.object = new Pair<Double, ACK_or_NACK_Receiver>(e.getTimeNS(), this);			
			Evt next = new Evt(e.getTimeNS(), this, nextDest);
			lastPacketStart = e.getTimeNS();
			next.setMessage(toSend);
			lwSimExperiment.manager.queueEvent(next);
			globalHorizon = e.getTimeNS() + toSend.getTransmissionTimeNS();	
			
			packetDuration = lwSimExperiment.getReferenceBandwidth().getTime(toSend.sizeInBits).getNanoseconds()+(builder.getMaxNumberOf2by2SwitchStages()*spinetSwitchingTimeNS);
			// report transmission time
			lwSimExperiment.reportTransTime(index, packetDuration, toSend);
		}
		catch (Exception eg) {
			throw new IllegalStateException("Error in e tdm", eg);
		}
		finally {
			slotId++;
		}	
	}
	
	private void receiveNack(Evt e) {
		// the nack can be already received, in this case currentlyOPtimi is null. So this case must be excluded
		if (!nackReceived && e.getMessage()!= currentlyOptimisticallyTransmitted) {
			System.out.println("error");
		//	throw new IllegalStateException("A nack corresponding to a message that isn't the one in the buffer has been received. This means that something is wrong (at time " + e.getTimeNS() + ")");
		}
		
		if (currentlyOptimisticallyTransmitted ==  null)
			return;

		if (nackReceived == false) {
			if (lwSimExperiment.isWithTimeLine())
				timeline.addJobPhase(lastPacketStart, e.getTimeNS(), currentlyOptimisticallyTransmitted.origin + "->" + currentlyOptimisticallyTransmitted.dest + "\n" + currentlyOptimisticallyTransmitted.index + ":NACK", Color.RED);		
			// immediately resend			
			nackReceived = true;
			// correct transmission time in the case of NACK Mode
			if (!this.ACKMode)
				lwSimExperiment.reportTransTime(index, e.getTimeNS() - lastPacketStart - packetDuration, currentlyOptimisticallyTransmitted);
			
			SpinetMessage dupl = currentlyOptimisticallyTransmitted.getCopy();
			getQueue(dupl.dest).remove(currentlyOptimisticallyTransmitted);
			getQueue(dupl.dest).addFirst(dupl);
			mainQueue.addFirst(dupl);
			defineSpinetMessage(dupl);
			lwSimExperiment.packetRetransmitted(dupl);
			currentlyOptimisticallyTransmitted = null;
		}		
	}
	
	@Override
	public void notifyEnd(double ref, double status) {
		super.notifyEnd(ref, status);
		dying = true;
	}	

	
	
	
	

}
