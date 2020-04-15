package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class NACKFast_Truncated_Backoff_Buffer extends NACKFast_Backoff_Buffer {

	private int waitTimeMax;
	private boolean ratioMode;
	
	private double waitTimeRatioMin;
	private double waitTimeRatioMax;
	
	public NACKFast_Truncated_Backoff_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index, int waitTimeLower, int waitTimeMax) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.waitTimeLower = waitTimeLower;
		this.waitTimeMax = waitTimeMax;
		resetWaitTime();
	}

	public NACKFast_Truncated_Backoff_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index,
			double waitTimeRatioMin, double waitTimeRatioMax) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		this.ratioMode = true;
		this.waitTimeRatioMin = waitTimeRatioMin;
		this.waitTimeRatioMax = waitTimeRatioMax;
	}

	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		if (this.ratioMode){
			this.waitTimeLower = (int)(1/waitTimeRatioMin * packetTransTime);
			this.waitTimeMax = (int)(1/waitTimeRatioMax * packetTransTime);
			resetWaitTime();
		}
		return null;
	}

	@Override
	protected void incWaitTimeUpper() {
		// TODO Auto-generated method stub
		super.incWaitTimeUpper();
		if (this.waitTimeUpper > waitTimeMax)
			this.waitTimeUpper = waitTimeMax;
	}
	
	

}
