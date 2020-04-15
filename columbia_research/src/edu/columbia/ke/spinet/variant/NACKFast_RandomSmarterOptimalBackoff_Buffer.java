package edu.columbia.ke.spinet.variant;

import java.util.Random;

import edu.columbia.lrl.general.Evt;

public class NACKFast_RandomSmarterOptimalBackoff_Buffer extends
		NACKFast_Smarter_Optimal_Backoff_Buffer {

	public NACKFast_RandomSmarterOptimalBackoff_Buffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index,
			double w) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, w);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double getNextWaitTime(Evt e) {
		if (wcWaitTime <= 0)
			this.resetWCWaitTime();
		else if (wcWaitTime < waitTimeLower){
			waitTime = wcWaitTime;
			wcWaitTime = 0;
			return waitTime;
		}
		
		waitTime = x * wcWaitTime;
		// waitTime = new Random().nextInt((int)Math.ceil(wcWaitTime - waitTime + 1)) + waitTime;
		double u = new Random().nextDouble();
		double v = ((Math.log(1-u))/(double)(-1)) / 4;
		if (v > 1)
			v = 1;
		waitTime += v * (wcWaitTime - waitTime);
		wcWaitTime -= waitTime;
		return waitTime;
	}
	
	

}
