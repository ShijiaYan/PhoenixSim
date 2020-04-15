package edu.columbia.lrl.LWSim.components;

import java.awt.Color;
import java.util.PriorityQueue;

import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;


public class Buffer extends AbstractTrafficOrigin implements TrafficTransitPoint {
	
	// definitions
	protected int maxSize;	
	protected int index;
	protected LWSIMExperiment lwSimExperiment;
	protected double bufferLatency;
	protected double outputRateCoeff;
	protected EventOrigin trafficOrigin;
	protected TimeLine timeline;
	protected boolean usePriorities;
	// later fixed
	protected Rate outputRate;
	
	public final static Color PACKET_BLUE = new Color(62, 47, 255);
	
	// state variable
	private PriorityQueue[] queues;
	protected int size;
	protected double lastPacketStart;
	
	protected boolean transmitting;
	
	public boolean isTransmitting() {
		return transmitting;
	}

	public Buffer(int maxSize, double bufferLatency, int index, double outputRateCoeff, boolean usePriorities) {
		this.maxSize = maxSize;
		if (bufferLatency < 0) throw new IllegalStateException("Latency must be positive");
		this.bufferLatency = bufferLatency;
		this.size = 0;
		this.index = index;
		this.outputRateCoeff = outputRateCoeff;
		this.usePriorities = usePriorities;
	}
	
	public Buffer(int maxSize, double bufferLatency, int index) {
		this(maxSize, bufferLatency, index, 1, false);
	}
	
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		if (lwSimExperiment.isWithTimeLine()) {
			timeline = new TimeLine();	
		}
		this.lwSimExperiment = lwSimExperiment;
		this.outputRate = lwSimExperiment.getReferenceBandwidth().multiply(outputRateCoeff);	
		return null;
	}
	
	public TimeLine getTimeLine() {
		return timeline;
	}	
	
	public int getSize() {
		return size;
	}
	
	public void processEvent(Evt e) {
		if (e.getType() == 0) {
			processProducerEvent(e);
		} else {
			processConsumerEvent(e);
		}
	}
	
	public double getEOTTimeNS(double startTimeNS, Message msg) {
		double transTime = outputRate.getTime(msg.sizeInBits).getNanoseconds();
		msg.lastDuration = transTime;
		return startTimeNS+transTime;
	}
	
	protected void processProducerEvent(Evt e) {
		size++;
		if (!transmitting) {
			transmit(e.getTimeNS() + bufferLatency, e.getMessage());
		} else {
			enqueue(e);
		}
	}
	
	protected void processConsumerEvent(Evt e) {
		Evt nextInLine = pollEvt();
		double t = e.getTimeNS();
		if (nextInLine != null) {
			transmit(t, nextInLine.getMessage());
		} else {
			transmitting = false;
		}
	}
	
	protected void enqueue(Evt e) {
		PriorityQueue<Evt> q;
		if (usePriorities) {
			q = getQueue(e.getMessage().applicationPriority);
		} else {
			q = getQueue(0);
		}
		q.add(e);
	}
	
	protected Evt pollEvt() {
		if (queues != null && queues.length >= 2 && queues[0] != null && queues[1] != null) {
			if (queues[0].size() > 0 && queues[1].size() > 1) {
				queues[1].peek();
				queues[0].peek();
			}
		}
		
		if (queues == null) return null;
		for (int i = queues.length - 1 ; i >= 0  ; i--) {
			@SuppressWarnings("unchecked")
			PriorityQueue<Evt> q = queues[i];
			if (q != null && q.size() > 0) {
				return q.poll();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private PriorityQueue<Evt> getQueue(int priority) {
		if (queues == null) {
			queues = new PriorityQueue[priority + 1];
		}
		if (queues.length <= priority) {
			PriorityQueue[] newA = new PriorityQueue[priority + 1];
			System.arraycopy(queues, 0, newA, 0, queues.length);
			queues = newA;
		}
		if (queues[priority] == null)
			queues[priority] = new PriorityQueue<Evt>();
		return (PriorityQueue<Evt>)queues[priority];
	}
	
	protected void transmit(double startTime, Message msg) {
		if (nextDest == null) throw new IllegalStateException("Next destination of a buffer (id: " + this.index + ") not set");
		Evt next = new Evt(startTime, this, nextDest, 0, msg);
		lwSimExperiment.manager.queueEvent(next);
		size--;
		lastPacketStart = startTime;		
		logEmission(startTime, msg.sizeInBits, msg.index, msg);
		scheduleEOTEvt(startTime, msg);
		transmitting = true;
	}
	
	protected void scheduleEOTEvt(double startTimeNS, Message msg) {
		Evt self = new Evt(getEOTTimeNS(startTimeNS, msg), this, this, 1, msg);
		lwSimExperiment.manager.queueEvent(self);		
	}
	
	
	
	protected void logEmission(double time, int bits, int val, Message m) {
		double tranTime = outputRate.getTime(bits).getNanoseconds();
		if (lwSimExperiment.isWithTimeLine())
			timeline.addJobPhase(time, time+tranTime, "m:" + m.index + "\r\n->" + m.dest, PACKET_BLUE);		
	}
	
	@Override
	public void notifyEnd(double ref, double status) {
		if (timeline != null)
			lwSimExperiment.addTimeLine(timeline);
	}	
	
	public String toShortString() {
		return "Buffer (" + size + "/" + maxSize + ")";
	}

	@Override
	public void setTrafficOrigin(EventOrigin origin) {
		this.trafficOrigin = origin;	
	}
}
