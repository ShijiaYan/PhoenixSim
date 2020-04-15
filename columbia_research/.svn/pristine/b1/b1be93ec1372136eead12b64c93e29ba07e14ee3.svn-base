package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class NACKFast_Optimal_Backoff_Buffer extends NACKFast_Backoff_Buffer {

	private double w;
	protected double x;
	protected double waitTime;
	protected double wcWaitTime;
	
	public NACKFast_Optimal_Backoff_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, double w) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.w = w;
		this.x = (double)2/(1+Math.sqrt(w));
	}

	@Override
	protected double getNextWaitTime(Evt e) {
		if (wcWaitTime < waitTimeLower){
			return wcWaitTime;
		}
		
		waitTime = x * wcWaitTime;
		wcWaitTime *= (1-x);
		return waitTime;
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		this.resetWCWaitTime();
		return null;
	}

	@Override
	protected void resetWaitTime() {
		super.resetWaitTime();
		this.resetWCWaitTime();
	}
	
	protected void resetWCWaitTime(){
		wcWaitTime = this.packetTransTime * 1.5;
		this.waitTimeLower = (int)(this.packetTransTime/2);
	}

	
}
