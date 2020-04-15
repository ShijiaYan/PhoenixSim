package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.general.Evt;

public class NACKFast_SmarterTruncatedBackoff_Buffer extends
		NACKFast_Truncated_Backoff_Buffer {
	
	private EventOrigin oldNackOrigin = null;
	protected double wcWaitTime;
	protected double leastWaitTime;

	public NACKFast_SmarterTruncatedBackoff_Buffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index,
			int waitTimeLower, int waitTimeMax) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index,
				waitTimeLower, waitTimeMax);
		// TODO Auto-generated constructor stub
	}

	public NACKFast_SmarterTruncatedBackoff_Buffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index,
			double waitTimeRatioMin, double waitTimeRatioMax) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index,
				waitTimeRatioMin, waitTimeRatioMax);
		// TODO Auto-generated constructor stub
	}
	
	/*@Override
	public void setLWSIMExperiment(LWSIMExperiment lwSimExperiment) {
		// TODO Auto-generated method stub
		super.setLWSIMExperiment(lwSimExperiment);
		this.resetWCWaitTime();
	}
	
	@Override
	protected void resetWaitTime() {
		this.oldNackOrigin = null;
		super.resetWaitTime();
		resetWCWaitTime();
	}
	
	protected void resetWCWaitTime(){
		wcWaitTime = this.packetTransTime;
	}*/

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
		// TODO Auto-generated method stub
		double tmp = super.getNextWaitTime(e);
		double RTT = e.getTimeNS() - this.lastPacketStart;
		
		if (RTT < 0){
			throw new IllegalStateException("RTT < 0");
		}
		
		double guardTime = 1;
		
		if (tmp < this.leastWaitTime - RTT + guardTime)
			return this.leastWaitTime - RTT + guardTime;
		else 
			return tmp;
	}

	/*@Override
	protected double getNextWaitTime() {
		
		// method 1
		if (this.nbNACKForCurrentMsg == 0) {
			super.getNextWaitTime();
			return this.wcWaitTime/3;
		}
		else
			return super.getNextWaitTime();
		
		// method 2
		if (this.wcWaitTime <= 0)
			return super.getNextWaitTime();
		
		double simpleWT = super.getNextWaitTime();
		double rv;
		if (simpleWT > this.wcWaitTime) 
			rv = this.wcWaitTime;
		else
			rv = simpleWT;
		this.wcWaitTime -= rv;
		return rv;
	}*/
	
	
	
	

}
