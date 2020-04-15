package edu.columbia.ke.circuit_oriented;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;

import edu.columbia.lrl.LWSim.AbstractTrafficOrigin;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.general.Evt;

public abstract class AbstractVOQ extends AbstractTrafficOrigin implements TrafficTransitPoint {
	
	public TreeMap<Integer, CircuitAvailLimitedBuffer> bufs;
	protected int nDest;
	protected LWSIMExperiment lwSimExperiment;
	protected boolean prior = false;
	public int index;
	protected PriorityQueue<Evt> evtQueue = new PriorityQueue<Evt>();
	protected int forwardedId=0;
	protected String voqName;
	
	protected ArrayList<Integer> destList = new ArrayList<Integer>();
	
	protected AbstractVOQ() {}

	public AbstractVOQ(int index, int nDest) {
		this.index = index;
		this.nDest = nDest;
		bufs = new TreeMap<Integer, CircuitAvailLimitedBuffer>();
	}
	
	public abstract void addDestinations(ArrayList<Integer> destList, ArrayList<LWSimComponent> dests);
	
	public abstract void addDestination(int dstId, ArrayList<LWSimComponent> dests);

	@Override
	public String toShortString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void processEvent(Evt e) {
		if (e.getsType() == null && e.getType() == 0) {
			processProducerEvent(e);
		} else {
			processConsumerEvent(e);
		}
	}
	
	protected Evt deque() {
		Evt nextInLine = evtQueue.poll();
		if (nextInLine == null) 
			throw new IllegalStateException("Empty Evt Queue!");
		
		this.forwardedId++;
		return nextInLine;
	}
	
	protected Evt forwardEvt(double time){
		Evt nextInLine = deque();
		int dest = nextInLine.getMessage().dest;
		Evt next = new Evt(time, this, bufs.get(dest), 0, nextInLine);
		lwSimExperiment.manager.queueEvent(next);
		bootStrap(time);
		return nextInLine;
	}
	
	protected void bootStrap(double time){
		Evt bootStrap = new Evt(time, this, this, "BOOTSTRAP");
		
		// cannot directly call like this
		// must call through queue of lwSimExperiment
		//processConsumerEvent(bootStrap);
		
		// new calling method
		lwSimExperiment.manager.queueEvent(bootStrap);
	}
	
	protected void processProducerEvent(Evt e) {
		this.evtQueue.add(e);
		if (evtQueue.size() == 1) {
			Evt bootStrap = new Evt(e.getTimeNS(), this, this, "BOOTSTRAP");
			processConsumerEvent(bootStrap);
		}	
	}
		
	protected abstract void processConsumerEvent(Evt e);

	@Override
	public void setTrafficOrigin(EventOrigin origin) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		return null;
	}



	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}
	
	public String getVOQName() {
		return this.voqName;
	}
}
