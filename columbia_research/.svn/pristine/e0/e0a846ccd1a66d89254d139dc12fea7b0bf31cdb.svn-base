package edu.columbia.lrl.experiments.spinet;

import java.util.LinkedList;

import ch.epfl.general_libraries.simulation.SimulationException;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class ImprovedBuffer extends SpinnetBuffer {
	
	protected LinkedList<Message> fifo = new LinkedList<Message>();
	protected double busyUntil = -1;
	
	public ImprovedBuffer(int maxSize, double bufferLatency, double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
	}
	
	@Override
	public int getSize() {
		return fifo.size();
	}
	
	protected void testAndPotentiallyBootstrapConsumetProcess(Evt e) {
		if (fifo.size() == 1) {
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
		if (fifo.size() > maxSize) {
			throw new SimulationException("Buffer overflow");
		}
		fifo.add(e.getMessage());
	/*	if (e.getMessage().dest == 2 && e.getMessage().origin == 30 && (e.getMessage().index % 10 == 0)) {
			System.out.println(e.getMessage() + " received at buffer at time " + e.getTimeNS());
		}	*/	
		testAndPotentiallyBootstrapConsumetProcess(e);
		//logReception(e.getTimeNS());						
	}
	
	@Override
	protected void processConsumerEvent(Evt e) {
		sendMessage(e, fifo.poll());
	}
	
	protected void sendMessage(Evt e, Message m) {
		Evt next = new Evt(e.getTimeNS(), this, nextDest);
		next.setMessage(m);
		defineSpinetMessage(next);		
		lwSimExperiment.manager.queueEvent(next);
	/*	if (m.index == 0) {
			System.out.println("Buffer: " + this.index + " pkt " + m.index + " sent at " + e.getTimeNS());
		}*/
	//	e.setMessage(m);
		lastPacketStart = e.getTimeNS();
		busyUntil = getEOTTimeNS(lastPacketStart, m);	
		logEmission(e.getTimeNS(), m.sizeInBits, m.index, m);	
		if (fifo.size() > 0) {
			// schedule the next consumption
			Evt self = new Evt(busyUntil, this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
		}
	}
}
