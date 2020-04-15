package edu.columbia.ke.component;



import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.components.AbstractSwitch;
import edu.columbia.lrl.experiments.spinet.AbstractSpinetBuilder;
import edu.columbia.lrl.experiments.spinet.SpinetComponent;
import edu.columbia.lrl.experiments.spinet.SpinetMessage;

public class HighRadixSwitch extends AbstractSwitch implements SpinetComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractSpinetBuilder builder;
	protected LWSIMExperiment lwSimExperiment;
	private TreeMap<Integer, Float> routingTable[];
	private int maxAdaptRoutingDegree;
	protected TreeMap<Integer, Double> busyUntil;
	protected double switchingTime;
	private TreeMap<Integer, LWSimComponent> outputDest;
	protected int id;
	protected double linkLatency;
	private int nbNode;
	protected double packetTransTime;
	protected double lastDeadline;
	private TreeMap<Integer, Message> OutputOccupyingMsgs;
	
	public HighRadixSwitch(int id, int nbNode, double swi, double linkLatency) {
		this.id = id;
		this.nbNode = nbNode;
		this.maxAdaptRoutingDegree = 3;
		this.switchingTime = swi;
		this.linkLatency = linkLatency;
		this.busyUntil = new TreeMap<Integer, Double>();
		this.outputDest = new TreeMap<Integer, LWSimComponent>();
		this.OutputOccupyingMsgs = new TreeMap<Integer, Message>();
		this.initRoutingTable();
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();
		return null;
	}
	
	private void initRoutingTable(){
		if (this.nbNode <= 0)
			throw new IllegalStateException("number of clients <= 0");
		
		this.routingTable = new TreeMap [this.nbNode];
		for (int i = 0 ; i < this.nbNode; i++){
			this.routingTable[i] = new TreeMap<Integer, Float> ();
		}
		
		// route packet destined for myself to myself
		// routingTable[id].put(id, 0);
	}
	
	public void addSuccessor(int tid, LWSimComponent successor){
		this.busyUntil.put(tid, 0d);
		this.outputDest.put(tid, successor);
		this.OutputOccupyingMsgs.put(tid, null);
	}
	
	public void addRoutingEntry(int dest, int nextHop, float distance){
		TreeMap<Integer, Float> nextHopList = routingTable[dest];
		if (nextHopList.containsKey(nextHop)){
			float oldDistance = (float)nextHopList.get(nextHop);
			if (oldDistance <= distance) return;
		}
		nextHopList.put(nextHop, distance);
	}
	
	// get nextHop or smallestLWT
	protected Pair<Integer, Double> getNextHop(int dest, double currentTime) {
		int nextHop = -1;
		TreeMap<Integer, Float> nextHopList = routingTable[dest];
		Iterator itr = nextHopList.entrySet().iterator();
		double smallestLWT = this.packetTransTime;
		while (itr.hasNext()) {
			Map.Entry tmp = (Map.Entry) itr.next();
			int tmpNextHop = (int) (Integer)tmp.getKey();
			double tmpWaitTime = (double) busyUntil.get(tmpNextHop) - currentTime;
			if (tmpWaitTime <= 0) {
				nextHop = tmpNextHop;
				break;
			} else {
				if (tmpWaitTime < smallestLWT)
					smallestLWT = tmpWaitTime;
			}
		}
		return new Pair<Integer, Double> (nextHop, smallestLWT);
	}

	@Override
	public void processEvent(Evt e) {
		//release switch
		if (e.getType()==9)	{
			releasePort(e);
			// check if switch state is up-to-date
			// this.packetExistenceCheck(e);
			return;
		}
				
		Message m = e.getMessage();
		double currentTime = e.getTimeNS();
		int dest = m.dest;
		
		Pair<Integer, Double> nextHopPair = getNextHop(dest, currentTime);
		
		int nextHop = nextHopPair.getFirst();
				
		if (nextHop < 0) {
			// cannot find available port
			// dropped due to contention, use type = 1
			double smallestLWT = nextHopPair.getSecond();
			packetDropped(m, e, currentTime, 1, smallestLWT);
		}
		else {
			//if (!outputDest.containsKey(nextHop))
				
			SpinetMessage msg = (SpinetMessage)m;
			// here the switch decrease the offset only if accepted
			msg.decrementOffset();
			msg.enqueOccupyResource(this);
			if (msg.getNumberOfHeaderOffset() < 0) {
				this.lwSimExperiment.packetDropped(m, "", this, 2);
				return;
			}
			
			Evt next;
			next = new Evt(currentTime + switchingTime + linkLatency, this, (LWSimComponent) outputDest.get(nextHop), e);
			lwSimExperiment.manager.queueEvent(next);
			busyUntil.put(nextHop, currentTime + switchingTime + msg.getTransmissionTimeNS());
			this.OutputOccupyingMsgs.put(nextHop, msg);
		}
		
		this.lastDeadline = e.getMessage().getDeadline();
	}
	
	private void releasePort(Evt e) {
		Message m = e.getMessage();
		if (m==null)
			throw new IllegalStateException("Releasing msg == null");
		Iterator itr = this.OutputOccupyingMsgs.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry tmp = (Map.Entry) itr.next();
			if (m==(Message)tmp.getValue()){
				tmp.setValue(null);
				busyUntil.put((int)(Integer)tmp.getKey(), e.getTimeNS());
				break;
			}
		}
	}
	
	protected void packetDropped(Message m, Evt e, double time, int type, double smallestLWT) {
		// by default, the drops at the switch are due to contention
		// dropType = 1
		lwSimExperiment.packetDropped(m, toString(), this, type);
	}

	@Override
	public String toShortString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSpinetBuilder(AbstractSpinetBuilder builder) {
		this.builder = builder;
		
	}

	@Override
	public void notifyEnd(double ref, double status) {
		// TODO Auto-generated method stub
		
	}

}
