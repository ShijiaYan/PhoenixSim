package edu.columbia.ke.spinet.variant;

import java.util.Map.Entry;
import java.util.TreeMap;

import ch.epfl.general_libraries.simulation.SimulationException;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;

public class PrioritizedBuffer extends SpinnetBuffer {

	public PrioritizedBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		// TODO Auto-generated constructor stub
	}

	protected TreeMap<Integer, Message> priorityQueue = new TreeMap<>();
	
	protected double busyUntil = 0;
	
	protected boolean idle = true;	// don't change the value of idle in this class
	
	@Override
	public int getSize() {
		return priorityQueue.size();
	}
	
	protected void testAndPotentiallyBootstrapConsumetProcess(Evt e) {
		if (priorityQueue.size() == 1) {
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
		if (priorityQueue.size() > maxSize) {
			throw new SimulationException("Buffer overflow");
		}
		
		// defineSpinetMessage(e);
		Message m = e.getMessage();
		priorityQueue.put(((SpinetMessage)m).getSpinetPriority(), m);
		
		if (idle)
			testAndPotentiallyBootstrapConsumetProcess(e);					
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		// send and dump the first message because this buffer does not care about reliable delivery
		if (priorityQueue.size() > 0) {
			Entry<Integer, Message> first = priorityQueue.pollFirstEntry();
			sendMessage(e, first.getValue());
		}
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
		if (priorityQueue.size() > 0) {
			// schedule the next consumption
			Evt self = new Evt(busyUntil, this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
		}
	}
}
