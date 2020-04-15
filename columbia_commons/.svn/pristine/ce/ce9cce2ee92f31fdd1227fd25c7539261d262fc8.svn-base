package edu.columbia.lrl.LWSim.analysers;

import edu.columbia.lrl.general.Message;

public class DeadlineAnalyserForBurst extends DeadLineAnalyser {

	public DeadlineAnalyserForBurst() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double getMsgDeadline(Message m) {
		double transmissionTime = this.lwSimExp.getReferenceBandwidth().getTime(m.sizeInBits).getNanoseconds();
		return m.timeEmitted + m.getInitTimeToLive() + transmissionTime;
	}
	
	

}
