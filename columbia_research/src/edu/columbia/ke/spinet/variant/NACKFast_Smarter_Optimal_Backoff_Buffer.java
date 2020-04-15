package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;

public class NACKFast_Smarter_Optimal_Backoff_Buffer extends
		NACKFast_Optimal_Backoff_Buffer {
	
	private EventOrigin oldNackOrigin = null;

	public NACKFast_Smarter_Optimal_Backoff_Buffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index,
			double w) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, w);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void resetWaitTime() {
		this.oldNackOrigin = null;
		super.resetWaitTime();
	}

	@Override
	protected void receiveNack(Evt e) {
		EventOrigin newOrigin = e.getOrigin();
		if (newOrigin == null)
			throw new IllegalStateException("Null NACK origin.");
		else{
			if (this.oldNackOrigin != newOrigin){
				this.resetWCWaitTime();
				this.oldNackOrigin = newOrigin;
			}
		}
		super.receiveNack(e);
	}
	
	

}
