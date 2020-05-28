package edu.columbia.ke.spinet.variant;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

/**
 * @author Ke Wen
 * This class is based on the general prioritized NACKFAST buffers
 * It specifically uses the deadline info of the message as the priority (see processProducerEvent)
 * It allows for the EDF / EDF_RETRAN_YIELD policy with quenching enabled/disabled
 */

public class NACKFast_Deadline_PrioritizedBuffer extends
		NACKFast_PrioritizedBuffer {

	private boolean quenchEnabled;
	protected double packetTransTime;
	
	private boolean frozen;
	private double minLinearDecUnit;
	
	private double waitTimeRatioMin;
	private double waitTimeRatioMax;
	private int waitTimeMax;
	private boolean deadlineBasedBackoff;
	
	
	public NACKFast_Deadline_PrioritizedBuffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index, PriorityMode pm, boolean quenchEnabled, double waitTimeRatioMin, double waitTimeRatioMax, boolean deadlineBasedBackoff) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, pm);
		this.deadlineBasedBackoff = deadlineBasedBackoff;
		this.quenchEnabled = quenchEnabled;
		this.waitTimeRatioMin = waitTimeRatioMin;
		this.waitTimeRatioMax = waitTimeRatioMax;
		minLinearDecUnit = 1/waitTimeRatioMin;
		if (this.priorityMode == PriorityMode.EDF_MILD)
			this.waitTimeExpBase = 2;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		// int size = lwSimExperiment.getBuilder().getExampleTrafficGenerator().getAveragePacketSize();
		int size = lwSimExperiment.getTrafficGenerator().getAveragePacketSize();
		this.packetTransTime = lwSimExperiment.getReferenceBandwidth().getTime(size).getNanoseconds();	
		this.waitTimeMax = (int)(1/waitTimeRatioMax * packetTransTime);
		if (this.priorityMode == PriorityMode.EDF_MILD || this.priorityMode == PriorityMode.EDF_DIDD )
			this.waitTimeLower = 0;
		else
			this.waitTimeLower = (int)(1/waitTimeRatioMin * packetTransTime);
		
		resetWaitTime();
		return null;
	}
	
	// only called in the beginnning
	protected void resetWaitTime(){

		if (this.waitTimeLower == 0)
			this.waitTimeUpper = 1;
		else
			this.waitTimeUpper = waitTimeLower;
	}

	@Override
	protected void processProducerEvent(Evt e) {	
		SpinetMessage msg = defineSpinetMessage(e);
		
		// use deadline as priority
		msg.setSpinetPriority((int) msg.getDeadline());
		
		super.processProducerEvent(e);				
	}
	
	protected void quenchQueue(double currentTime){
		if (this.quenchEnabled) {
			// delete expired msg before sending msgs out from the priority queue				
			double addTime;
			while (priorityQueue.size() > 0) {								
				Message first = priorityQueue.firstEntry()
						.getValue();
				
				addTime = 0;
				
				// assume you don't have this info
				/*if (first == this.justNACKed)
					addTime = this.leastWaitTime;
				*/
				
				if (first.getDeadline() <= currentTime + addTime + packetTransTime) {
					this.packetDropped(first);
					if (first == this.justNACKed) {
						this.justNACKed = null;
						this.justNACKedEntry = null;
						this.leastWaitTime = 0;
					}
					priorityQueue.pollFirstEntry();
				} else
					break;
			}	
			this.currentEntry = null;
			this.currentlyTransmitted = null;
		}
	}
	
	private void rebirth(double currentTime){
		// wait
		int waitTime = 0;
		
		if (this.priorityMode != PriorityMode.EDF) {
			if (this.waitTimeLower == this.waitTimeUpper)
				waitTime = (int) waitTimeLower;
			else
				waitTime = new Random().nextInt((int) Math
						.ceil(this.waitTimeUpper - this.waitTimeLower))
						+ (int) this.waitTimeLower;
		}
		
		Evt self;
		frozen = false;
		
		switch(this.priorityMode){
		case EDF_MILD:
			frozen = true;
			self = new Evt(currentTime + waitTime, this, this, 10);
			break;
		default:
			self = new Evt(currentTime + waitTime, this, this, 1);
			break;
		}
		
		lwSimExperiment.manager.queueEvent(self);
	}	
	
	private double imminence;
	private double updateImminence(double currentTime){
		imminence = 0;
		if (this.priorityQueue.size() > 0){
			Message first = this.priorityQueue.firstEntry().getValue();
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
	
	private void changeWaitTimeAfterNACK(double currentTime) {
		// this.waitTimeUpper *= this.waitTimeExpBase;
		if (!this.deadlineBasedBackoff)
			this.waitTimeUpper *= this.waitTimeExpBase;
		else {
			switch(this.priorityMode){
			case EDF_MILD:
				double d = this.updateImminence(currentTime);
				double alpha = waitTimeExpBase + 1 - Math.pow(waitTimeExpBase, d);
				this.waitTimeUpper *= alpha;
				break;
			default:
				this.waitTimeUpper *= this.waitTimeExpBase;
				break;
			}
		}
	}

	protected void changeWaitTimeAfterSuccess(double currentTime) {
		switch(this.priorityMode){
		case EDF_MILD:
			if (this.deadlineBasedBackoff) {
				double d = this.updateImminence(currentTime);
				this.waitTimeUpper -= minLinearDecUnit * (1+100*d) * this.packetTransTime;
			} else 
				this.waitTimeUpper -= minLinearDecUnit * this.packetTransTime;
			
			if (waitTimeUpper < 1)
				this.waitTimeUpper = 1;
			break;
		default:
			this.waitTimeUpper = 1;
			break;
		}

	}

	@Override
	protected void doAfterEndofTrans(Evt e) {
		this.quenchQueue(e.getTimeNS());
		this.changeWaitTimeAfterSuccess(e.getTimeNS());
		this.rebirth(e.getTimeNS());
	}

	@Override
	protected void doAfterNACK(Evt e) {
		this.quenchQueue(e.getTimeNS());
		this.changeWaitTimeAfterNACK(e.getTimeNS());
		this.rebirth(e.getTimeNS());
	}

	@Override
	protected void processConsumerEvent(Evt e) {
		if (e.getType() == 10) {
			frozen = false;
			// rebirth
			Evt self = new Evt(e.getTimeNS(), this, this, 1);
			lwSimExperiment.manager.queueEvent(self);
			
		}
		else {
			if (!(frozen && e.getType() == 1))
				super.processConsumerEvent(e);
		}
	}
	
	private boolean getTransDecision_(double currentTime) {
		switch (this.priorityMode) {
		case EDF_RETRAN_DEADLINE_AWARE_BACKOFF:
			return this.probBasedGetTransDecision(currentTime);
			
		default:
			return true;
		}
	}
	
	private boolean probBasedGetTransDecision(double currentTime){
		if (priorityQueue.size() == 0)
			return true;
		else {
			double ps = 0;
			double psThreshold = 0.3;
			boolean kickHead = true;
			while (kickHead && priorityQueue.size() > 0) {

				double backlogPressure = 0;
				double imminence = 0;
				int n = 4;
				if (priorityQueue.size() < n)
					n = priorityQueue.size();

				Iterator currentIterator;
				currentIterator = priorityQueue.entrySet().iterator();

				for (int i = 0; i < n; i++) {
					Map.Entry firstEntry = (Map.Entry) currentIterator
							.next();
					Message first = (Message) firstEntry.getValue();
					double ttl = first.getDeadline() - currentTime;
					double tmpImminence = ttl / packetTransTime; 
					if (i == 0)
						imminence = tmpImminence;
					
					if (tmpImminence <= 0)
						backlogPressure += 10;
					else
						backlogPressure += 1/tmpImminence;
				}

				// determine p based on imminence and backlogPressure
				ps = getTransProb(imminence, backlogPressure);
				
				kickHead = false;
				if (ps < psThreshold && imminence < 3) {
					if (this.priorityQueue.firstEntry() == this.justNACKedEntry) {
						this.justNACKed = null;
						this.justNACKedEntry = null;
					}
					this.currentEntry = null;
					this.currentlyTransmitted = null;
					this.priorityQueue.pollFirstEntry();
					kickHead = true;
				}
			}
			
			if (priorityQueue.size() == 0)
				return true;
			// otherwise, throw a dice
			double rdtmp = new Random().nextDouble();
            return rdtmp < ps;
		}
	}
	
	
	protected double getTransProb(double imminence, double backlogPressure){
		double ps;
		
		// exp function
		/*if (imminence <= 1 || backlogPressure > 0.2)
			ps = 1;
		else
			ps = 1 - Math.exp((imminence - 20)/(double)10);
		*/
		
		// cos function
		if (imminence <= 1 || imminence >= 20)
			ps = 0;
		else if (imminence > 1 && imminence <= 3)
			ps = 0.5 - 0.5 * Math.cos((imminence - 1) / 2 * Math.PI);
		else
			ps = 0.5 + 0.5 * Math.cos((imminence - 3) / 17 * Math.PI);
		
		if (ps < 0)
			ps = 0;
		if (ps > 1)
			ps = 1;
		
		return ps;
	}

	protected void packetDropped(Message m) {
		// dropped due to expiration, use dropType = 2
		lwSimExperiment.packetDropped(m, toString(), this, 2);
	}
	
	/*@Override
	protected double getNextWaitTime(double currentTime) {
		double rv;
		
		quenchQueue(currentTime);
		
		switch (this.priorityMode) {
		case EDF_RETRAN_DEADLINE_AWARE_BACKOFF:
			if (priorityQueue.size() == 0)
				rv = 0;
			else {
				double backlogPressure = 0;
				double imminence = 0;
				int n = 4;
				if (priorityQueue.size() < n)
					n = priorityQueue.size();
				
				Iterator currentIterator;
				currentIterator = priorityQueue.entrySet().iterator();
				
				for (int i = 0; i < n; i++) {
					Map.Entry firstEntry = (Map.Entry) currentIterator.next();
					Message first = (Message) firstEntry.getValue();
					double ttl = first.getDeadline() - currentTime;
					double tmpImminence = (ttl - this.leastWaitTime)/packetTransTime;	// the smaller, the more imminent
					if (i == 0)
						imminence = tmpImminence;
					backlogPressure += tmpImminence;
				}
				
				if (imminence < 50 || backlogPressure < n * 50)
					rv = leastWaitTime;
				else {
					int coin = new Random().nextInt() % 2;
					if (coin == 0)
						rv = leastWaitTime;
					else {
						int tmp = new Random().nextInt((int)( 8 * packetTransTime - leastWaitTime));
						rv = (leastWaitTime + tmp);
					}
				}
			}
			break;
		case EDF_RETRAN_WAIT:
			rv = new Random().nextInt(this.waitTimeUpper);
			break;
		default:
			rv = this.leastWaitTime;
			break;
		}
		
		return rv;
	}*/
	
	
}
