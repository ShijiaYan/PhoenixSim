package edu.columbia.ke.spinet.variant;

import edu.columbia.lrl.general.Evt;

public class Slotted_NACKFast_Deadline_NQPBuffer extends
		NACKFast_Deadline_NQPBuffer {
	
	protected double slotDuration;

	public Slotted_NACKFast_Deadline_NQPBuffer(int maxSize,
			double bufferLatency, double spinetSwitchingTimeNS, int index,
			PriorityMode pm, boolean quenchEnabled, boolean deadlineBasedBackoff, double slotDuration) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index, pm,
				quenchEnabled, deadlineBasedBackoff);
		this.slotDuration = slotDuration;
	}
	
	@Override
	protected double getDepartureTime(Evt e) {
		double mod = e.getTimeNS() % slotDuration;
		double slotId = (e.getTimeNS() - mod)/slotDuration;
		double nextTime = (slotId+1)*slotDuration;
		return nextTime;
	}

}
