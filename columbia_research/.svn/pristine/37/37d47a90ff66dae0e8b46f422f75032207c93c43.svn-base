package edu.columbia.ke.spinet.variant;

import java.util.Map.Entry;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class NACKFast_OCF_NQPBuffer extends NACKFast_NQPrioritizedBuffer {

	public NACKFast_OCF_NQPBuffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, PriorityMode pm) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, pm);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void processProducerEvent(Evt e) {	
		SpinetMessage msg = defineSpinetMessage(e);
		/* use emission time as priority */
		msg.setSpinetPriority((int)(msg.timeEmitted));
		
		super.processProducerEvent(e);				
	}
	
	@Override
	protected void pickAndConsume(Evt e) {
		double currentTime = e.getTimeNS();
		
		/*
		 * pick a feasible and oldest cell
		 */

		// adapted from super
		int feasibleOldestEmissionTime = java.lang.Integer.MAX_VALUE;
		int feasibleOC = -1;
		
		double nearestWaitUntil = java.lang.Double.MAX_VALUE;
		
		for (int i = 0; i < this.nbClient; i++) {
			// find the nearest waitUntil time in future
			if (this.waitUntil[i] <= nearestWaitUntil && this.waitUntil[i] > currentTime)
				nearestWaitUntil = this.waitUntil[i];
			
			// find the Feasible OC packet
			if (this.priorityQueue[i].size() > 0
					&& (int)(Integer)  this.priorityQueue[i].firstKey() < feasibleOldestEmissionTime
					&& this.waitUntil[i] <= currentTime
					) {
				feasibleOldestEmissionTime = (int)(Integer) this.priorityQueue[i].firstKey();
				feasibleOC = i;
			}
		}

		// if found an eligible packet
		if (feasibleOC != -1) {
			Entry first = priorityQueue[feasibleOC].pollFirstEntry();
			sendMessage(e, (Message) (first.getValue()));
		} else { // no eligible packet, wait till the nearest waitUntil time
			if (this.getSize() > 0) {
				Evt self = new Evt(nearestWaitUntil, this, this, 1);
				lwSimExperiment.manager.queueEvent(self);
			}
		}		
	}

}
