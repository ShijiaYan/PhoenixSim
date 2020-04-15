package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;

public class NACKFast_LeastWaitTime_Buffer extends NACKFast_Backoff_Buffer {
	
	protected double leastWaitTime;

	public NACKFast_LeastWaitTime_Buffer(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void receiveNack(Evt e) {
		EventOrigin newOrigin = e.getOrigin();
		if (newOrigin == null)
			throw new IllegalStateException("Null NACK origin.");
		else{
			this.leastWaitTime = e.getMessage().getLeastTimeToWait();
			if (this.leastWaitTime < 0)
				throw new IllegalStateException("leastWaitTime < 0");
		}
		super.receiveNack(e);
	}
	
	@Override
	protected double getNextWaitTime(Evt e) {
		return this.leastWaitTime;
	}

}
