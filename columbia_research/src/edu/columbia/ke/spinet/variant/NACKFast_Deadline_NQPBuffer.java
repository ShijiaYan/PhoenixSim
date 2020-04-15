package edu.columbia.ke.spinet.variant;

import java.util.Map.Entry;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class NACKFast_Deadline_NQPBuffer extends NACKFast_NQPrioritizedBuffer {
	
	private double packetTransTime;
	private boolean quenchEnabled;
	private boolean deadlineBasedBackoff;
//	protected double lamda;

	public NACKFast_Deadline_NQPBuffer(int maxSize, double bufferLatency,
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
		// normalize load
		//lamda = this.lwSimExperiment.getNormalisedLoad();
		return null;
	}
	
	@Override
	protected void processProducerEvent(Evt e) {	
		SpinetMessage msg = defineSpinetMessage(e);
		
		// use deadline as priority
		msg.setSpinetPriority((int)(msg.getDeadline()));
		
		super.processProducerEvent(e);				
	}
	
	protected void quenchQueue(double currentTime){
		if (this.quenchEnabled) {
			// delete expired msg before sending msgs out from the priority queue				
			double addTime = 0;
			for (int i = 0; i < this.nbClient; i++) {
				while (priorityQueue[i].size() > 0) {
					Message first = (Message) priorityQueue[i].firstEntry()
							.getValue();

					if (first.getDeadline() < currentTime + addTime
							+ packetTransTime) {
						// dropped due to expiration, use dropType = 2
						lwSimExperiment.packetDropped(first, toString(), this,
								2);
						priorityQueue[i].pollFirstEntry();
					} else
						break;
				}
			}
		}
	}
	
	/*protected void quenchQueue(double currentTime){
		if (this.quenchEnabled) {
			// delete expired msg before sending msgs out from the priority queue	
			double timeLeftThreshold = packetTransTime;
			
			if (this.totalSuccess > 3) {
				double expServiceTime = currentTime/this.totalSuccess;
				double mu = 1/expServiceTime;
				double pQuench = 0.1;
				timeLeftThreshold = - Math.log(1-pQuench) / mu;
				if (timeLeftThreshold < this.packetTransTime)
					timeLeftThreshold = packetTransTime;
			}
			
			double acceptedDeadline = currentTime + timeLeftThreshold;
			
			for (int i = 0; i < this.nbClient; i++) {
				while (priorityQueue[i].size() > 0) {
					Message first = (Message) priorityQueue[i].firstEntry()
							.getValue();

					if (first.getDeadline() < acceptedDeadline) {
						// dropped due to expiration, use dropType = 2
						lwSimExperiment.packetDropped(first, toString(), this,
								2);
						priorityQueue[i].pollFirstEntry();
					} else
						break;
				}
			}
		}
	}*/
	
	protected double getSlotDuration(){
		return packetTransTime;
	}
	
	protected double getNoArrivalProbPoisson(double lamda, double timeInNS){
		double normalizedTime = timeInNS/packetTransTime;
		if (normalizedTime > 1) 
			normalizedTime = 1;
		return Math.exp(-lamda * normalizedTime);
	}
	
	protected double getScore(double lax){
		double normalizedLax = lax/packetTransTime;
		return Math.exp(-0.01*normalizedLax);
	}

	@Override
	protected void pickAndConsume(Evt e) {
		double currentTime = e.getTimeNS();
		this.quenchQueue(currentTime);

		// adapted from super
		int feasibleED = java.lang.Integer.MAX_VALUE;
		int feasibleEDIndex = -1;
		
		int ED = java.lang.Integer.MAX_VALUE;
		int EDIndex = -1;
		
		double TWness = java.lang.Double.MAX_VALUE;
		double maxScore = java.lang.Double.MIN_VALUE;
		int scoreBasedOptIndex = -1;
		
		double nearestWaitUntil = java.lang.Double.MAX_VALUE;
		
		double expServiceTime = currentTime/this.totalSuccess;
		double mu = 1/expServiceTime;
		
		for (int i = 0; i < this.nbClient; i++) {
			// find the nearest waitUntil time in future
			if (this.waitUntil[i] <= nearestWaitUntil && this.waitUntil[i] > currentTime)
				nearestWaitUntil = this.waitUntil[i];
			
			// find the EDF packet
			if (this.priorityQueue[i].size() > 0 
					&& (int)(Integer)  this.priorityQueue[i].firstKey() < ED){
				ED = (int)(Integer)  this.priorityQueue[i].firstKey();
				EDIndex = i;
			}
			
			// find the FEDF packet
			if (this.priorityQueue[i].size() > 0
					&& (int)(Integer)  this.priorityQueue[i].firstKey() < feasibleED
					&& this.waitUntil[i] <= currentTime
					) {
				feasibleED = (int)(Integer)  this.priorityQueue[i].firstKey();
				feasibleEDIndex = i;
			}
		}
		
		
		///// TODO TODO TODO TODO
		// In general network element cannot guess what is the load, this is something that should be removed
		double lamda = 0;//lwSimExperiment.getNormalisedLoad();
		if (true)
			throw new IllegalStateException("Problem to be fixed");
		double probAvail = 0.9;
		double thresholdInSlot = Math.log(1 / probAvail) / lamda;
		double threshold = thresholdInSlot * getSlotDuration();
			
		/* score based election */
		if (this.priorityMode == PriorityMode.NQPrioritized_ExpScoreAndProb) {
			for (int i = 0; i < this.nbClient; i++) {
				if (this.priorityQueue[i].size() > 0
						&& this.waitUntil[i] <= currentTime) {
					double lax = (int)(Integer)  this.priorityQueue[i].firstKey() - currentTime;
					double passedTime = currentTime - this.waitUntil[i];
					double expectedScore = this.getScore(lax) * this.getNoArrivalProbPoisson(lamda, passedTime);
					if (expectedScore > maxScore) {
						maxScore = expectedScore;
						scoreBasedOptIndex = i;
					}
				}
			}
		} else {
			for (int i = 0; i < this.nbClient; i++) {
				if (this.priorityQueue[i].size() > 0
						&& (int)(Integer)  this.priorityQueue[i].firstKey() < TWness
						&& this.waitUntil[i] <= currentTime
						&& currentTime - this.waitUntil[i] < threshold) {
					TWness = (int)(Integer)  this.priorityQueue[i].firstKey();
					scoreBasedOptIndex = i;
				}
				
				// find the most trust-worthy feasible packet
				/*if (this.priorityQueue[i].size() > 0
						&& this.waitUntil[i] <= currentTime
						&& currentTime - this.waitUntil[i] < TWness) {
					TWness = currentTime - this.waitUntil[i];
					TWFeasibleIndex = i;
				}*/
			}

		}
		
		/* added for implementing benefit-cost policy 
		 * consider whether wait to save the EDF packet or not
		 */
		// this part for the variants of NQPrioritized
		// basically considering the non-work-conserving case
		if (this.priorityMode != PriorityMode.NQPrioritized) {
			// if it is still possible to save this packet
			boolean considerWait = false;
			boolean waitForED = false;
			
			// variant 1
			if (this.priorityMode == PriorityMode.NQPrioritized_variant1) {
				considerWait = (EDIndex != -1
						&& feasibleEDIndex != -1
						&& this.waitUntil[EDIndex] > this.waitUntil[feasibleEDIndex]
						&& feasibleEDIndex != EDIndex
						/*&& ED >= currentTime + packetTransTime*/);
			/*else if (this.priorityMode == PriorityMode.NQPrioritized_variant2){
				considerWait = (EDIndex != -1
						&& feasibleEDIndex != -1
						&& this.waitUntil[EDIndex] > this.waitUntil[feasibleEDIndex]
						&& feasibleEDIndex != EDIndex
						&& ED - this.packetTransTime < this.waitUntil[feasibleEDIndex]
								+ this.packetTransTime);
			} else 
				considerWait = false;*/
			
				if (considerWait) {
					// success rate
					double p;
					if (this.totalTran == 0)
						p = 1;
					else
						p = 1 - (double) this.totalNACK / this.totalTran;

					double lostPackets = lamda
							* (this.waitUntil[EDIndex] - this.waitUntil[feasibleEDIndex])
							/ this.packetTransTime;

					if (lostPackets < p)
						waitForED = true;
				}
			} 
			// variant 3
			else if (this.priorityMode == PriorityMode.NQPrioritized_variant3) {
				double expStartTime = ED - expServiceTime;
				waitForED = (EDIndex != -1
						&& feasibleEDIndex != -1
						&& feasibleEDIndex != EDIndex
						&& ED > currentTime + packetTransTime
						&& this.waitUntil[EDIndex] > currentTime
						&& (expStartTime < currentTime + packetTransTime));
				
				if (waitForED) {
					
					double deltaEmptyTime = this.waitUntil[EDIndex]- currentTime;
					double missingTHP = mu * deltaEmptyTime;
				
					/*double slackTimeShort = ED - (currentTime + packetTransTime);
					double slackTimeLong = ED - this.waitUntil[EDIndex];
					
					double successProbGain = cumulativeExpDist(mu, slackTimeLong) - cumulativeExpDist(mu, slackTimeShort);
					
					waitForED = waitForED && (successProbGain > 2*missingTHP);*/
					
					waitForED = waitForED && (missingTHP < 1);
				} 
			} else {}

			if (waitForED) {
				Evt self = new Evt(this.waitUntil[EDIndex], this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
				return;
			}
		}
		/* added for implementing benefit-cost policy */
		

		// found an eligible packet
		/* score based methods: scoreBasedOptIndex >> feasibleEDIndex */
		if (this.priorityMode == PriorityMode.NQPrioritized_ExpScoreAndProb
				|| this.priorityMode == PriorityMode.NQPrioritized_variant2
				|| this.priorityMode == PriorityMode.NQPrioritized_variant4) {
			if (this.priorityMode == PriorityMode.NQPrioritized_variant4) {
				double expStartTime = feasibleED - expServiceTime;
				if (expStartTime < currentTime
						&& feasibleED > currentTime + this.packetTransTime)
					scoreBasedOptIndex = -1;
			}

			if (scoreBasedOptIndex != -1) {
				Entry first = priorityQueue[scoreBasedOptIndex].pollFirstEntry();
				sendMessage(e, (Message) (first.getValue()));
			} else if (feasibleEDIndex != -1) {
				Entry first = priorityQueue[feasibleEDIndex].pollFirstEntry();
				sendMessage(e, (Message) (first.getValue()));
			} else { // no eligible packet, wait till the nearest waitUntil time
				if (this.getSize() > 0) {
					Evt self = new Evt(nearestWaitUntil, this, this, 1);
					lwSimExperiment.manager.queueEvent(self);
				}
			}
		} else {	// directly FEDF
			if (feasibleEDIndex != -1) {
				Entry first = priorityQueue[feasibleEDIndex].pollFirstEntry();
				sendMessage(e, (Message) (first.getValue()));
			} else { // no eligible packet, wait till the nearest waitUntil time
				if (this.getSize() > 0) {
					Evt self = new Evt(nearestWaitUntil, this, this, 1);
					lwSimExperiment.manager.queueEvent(self);
				}
			}
		}
		
	}
	
	private double cumulativeExpDist(double mu, double x){
		return 1-Math.exp(-mu*x);
	}

	
}
