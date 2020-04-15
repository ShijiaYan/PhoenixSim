package edu.columbia.ke.component.burst_assembly;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class TimeBasedFixedSizeBurstAssembler extends TimeBasedBurstAssembler {
	private int maxSize;

	public TimeBasedFixedSizeBurstAssembler(
			@ParamName(name = "max assemble window", default_ = "1000") double maxTime,
			@ParamName(name = "max burst size", default_ = "500000") int maxSize) {
		super(maxTime);
		this.maxSize = maxSize;
	}
	
	@Override
	public void addMessage(Evt e) {
		double now = e.getTimeNS();
		Message m = e.getMessage();
		if (this.burst == null)
			burst = new BurstMessage();

		if (this.burst.sizeInBits + m.sizeInBits <= this.maxSize) {
			this.burst.addMessage(m);
			if (updateAssembleEndTime(now, m)) {
				double emitTime = getAssembleEndTime();
				scheduleBurstEmission(emitTime);
			}
		} else {
			Evt next = new Evt(now, this, nextDest, e);
			next.setMessage(burst);
			lwSimExperiment.manager.queueEvent(next);
			burst = new BurstMessage();
			burst.addMessage(m);
		}
	}

}
