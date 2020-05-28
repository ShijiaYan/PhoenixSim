package edu.columbia.ke.component.burst_assembly;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.general.Message;

public class DeadlineAwareTBBurstAssembler extends TimeBasedBurstAssembler {
	
	private int guardTime = 1000;

	public DeadlineAwareTBBurstAssembler(
			@ParamName(name = "max assemble window", default_ = "1000") double maxTime) {
		super(maxTime);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean updateAssembleEndTime(double now, Message m) {
		double newEndTime = this.burst.getDeadline() - this.guardTime;
		
		if (this.burst.size() == 1) 
			this.assembleEndTime = now + windowTime;
		
		if (newEndTime < this.assembleEndTime) {
			this.assembleEndTime = newEndTime;
		
			if (this.assembleEndTime < now)
				this.assembleEndTime = now;
			return true;
		}

        return this.burst.size() == 1;
	}
	
	@Override
	public String getBurstAssemblerName() {
		return "Deadline-aware BA";
	}

}
