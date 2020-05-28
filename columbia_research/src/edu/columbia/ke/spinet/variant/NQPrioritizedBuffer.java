package edu.columbia.ke.spinet.variant;

import java.util.Map.Entry;
import java.util.TreeMap;

import ch.epfl.general_libraries.simulation.SimulationException;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;

public class NQPrioritizedBuffer extends SpinnetBuffer {

	public NQPrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
	}
	
	protected int nbClient;

	protected TreeMap[] priorityQueue;
	
	protected double busyUntil = 0;
	
	protected boolean idle = true;	// don't change the value of idle in this class
	
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		nbClient = this.lwSimExperiment.getNumberOfClients();
		priorityQueue = new TreeMap[nbClient];
		for (int i = 0 ; i < this.nbClient; i++){
			priorityQueue[i] = new TreeMap();
		}
		return null;
	}

	@Override
	public int getSize() {
		int sum = 0;
		for (int i = 0; i < this.nbClient; i++) {
			sum += this.priorityQueue[i].size();
		}
		return sum;
	}
	
	protected void testAndPotentiallyBootstrapConsumetProcess(Evt e) {
		if (this.getSize() == 1) {
			// bootstrap the sending process with an event
			double startTime;
			if (busyUntil < e.getTimeNS()) {
				startTime = e.getTimeNS() + bufferLatency;
			} else {
				startTime = busyUntil;
			}
			Evt self = new Evt(startTime, this, this, 1);
			lwSimExperiment.manager.queueEvent(self);	
		}		
	}
	
	@Override
	protected void processProducerEvent(Evt e) {	
		Message m = e.getMessage();
		
		priorityQueue[m.dest].put(((SpinetMessage)m).getSpinetPriority(), m);
		
		if (priorityQueue[m.dest].size() > maxSize) {
			throw new SimulationException("Buffer overflow");
		}
		
		if (idle)
			testAndPotentiallyBootstrapConsumetProcess(e);					
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		// send and dump the first message because this buffer does not care
		// about reliable delivery
		pickAndConsume(e);
	}
	
	protected void pickAndConsume(Evt e) {
		/* find the VOQ that has the highest priority 
		 * 
		 */
		double prior = java.lang.Double.MAX_VALUE;
		int pIndex = -1;
		for (int i = 0; i < this.nbClient; i++) {
			if (this.priorityQueue[i].size() > 0
					&& (Double) this.priorityQueue[i].firstKey() < prior) {
				prior = (double)(Double) this.priorityQueue[i].firstKey();
				pIndex = i;
			}
		}

		if (pIndex != -1) {
			Entry first = priorityQueue[pIndex].pollFirstEntry();
			sendMessage(e, (Message) first.getValue());
		}
		// otherwise, all of the queues are empty
	}
	
	protected void sendMessage(Evt e, Message m) {
		m.setActive(true);
		Evt next = new Evt(e.getTimeNS(), this, nextDest);
		next.setMessage(m);
		defineSpinetMessage(next);		
		lwSimExperiment.manager.queueEvent(next);
	/*	if (m.index == 0) {
			System.out.println("Buffer: " + this.index + " pkt " + m.index + " sent at " + e.getTimeNS());
		}*/
		lastPacketStart = e.getTimeNS();
		busyUntil = getEOTTimeNS(m, e);	
		logEmission(e.getTimeNS(), m.sizeInBits, m.index, m);	
		if (this.getSize() > 0) {
			// schedule the next consumption
			Evt self = new Evt(busyUntil, this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
		}
	}
}
