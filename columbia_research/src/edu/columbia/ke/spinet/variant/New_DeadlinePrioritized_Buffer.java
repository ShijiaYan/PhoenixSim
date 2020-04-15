package edu.columbia.ke.spinet.variant;

import java.util.Iterator;
import java.util.Map;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class New_DeadlinePrioritized_Buffer extends
		New_NACKFast_PrioritizedBuffer {
	
	private double packetTransTime;
	private boolean quenchEnabled;
	private boolean deadlineBasedBackoff;

	public New_DeadlinePrioritized_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm, boolean quenchEnabled, boolean deadlineBasedBackoff) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, pm);
		this.quenchEnabled = quenchEnabled;
		this.deadlineBasedBackoff = deadlineBasedBackoff;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		// int size = lwSimExperiment.getBuilder().getExampleTrafficGenerator().getAveragePacketSize();
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();	
		return null;
	}
	
	@Override
	protected void processProducerEvent(Evt e) {	
		SpinetMessage msg = defineSpinetMessage(e);
		
		// use deadline as priority
		msg.setSpinetPriority((int)(msg.getDeadline()));
		
		super.processProducerEvent(e);				
	}
	
	private double imminence;
	private double updateImminence(double currentTime){
		imminence = 0;
		if (this.priorityQueue.size() > 0){
			Message first = (Message)this.priorityQueue.firstEntry().getValue();
			double ttl = first.getDeadline() - currentTime;
			if (ttl < packetTransTime)
				ttl = packetTransTime;
			imminence = packetTransTime / ttl;
		}
		return imminence;
	}
	
	private double backlog;
	private double updateBacklog(double currentTime){
		backlog = 0;
		int n = 4;
		if (priorityQueue.size() < n)
			n = priorityQueue.size();
		
		if (n == 0)
			return 0;

		Iterator currentIterator;
		currentIterator = priorityQueue.entrySet().iterator();

		for (int i = 0; i < n; i++) {
			Map.Entry firstEntry = (Map.Entry) currentIterator
					.next();
			Message first = (Message) firstEntry.getValue();
			double ttl = first.getDeadline() - currentTime;
			if (ttl < packetTransTime)
				ttl = packetTransTime;
			double tmpImminence = packetTransTime / ttl; 
			backlog += tmpImminence;	
		}
		return backlog;
	}
	
	protected void quenchQueue(double currentTime){
		if (this.quenchEnabled) {
			// delete expired msg before sending msgs out from the priority queue				
			double addTime = 0;
			while (priorityQueue.size() > 0) {								
				Message first = (Message) priorityQueue.firstEntry()
						.getValue();
				
				// assume you don't have this info
				/*if (first == this.justNACKed)
					addTime = this.leastWaitTime;
				*/
				
				if (first.getDeadline() <= currentTime + addTime + packetTransTime) {
					// dropped due to expiration, use dropType = 2
					lwSimExperiment.packetDropped(first, toString(), this, 2);
					priorityQueue.pollFirstEntry();
				} else
					break;
			}	
			
		}
	}

	@Override
	protected void backInQueue(Message m, double currentTime) {
		if (!(quenchEnabled && m.getDeadline() <= currentTime + packetTransTime))
			super.backInQueue(m, currentTime);
	}

	@Override
	protected void doAfterEndofTrans(Evt e) {
		quenchQueue(e.getTimeNS());
		super.doAfterEndofTrans(e);
	}

	@Override
	protected void doAfterNACK(Evt e) {
		quenchQueue(e.getTimeNS());
		super.doAfterNACK(e);
	}
	
	
	
	

}
