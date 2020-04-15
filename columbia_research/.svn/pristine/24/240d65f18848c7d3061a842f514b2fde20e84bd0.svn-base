package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.ke.component.burst_assembly.BurstReceiver;
import edu.columbia.lrl.LWSim.components.Receiver;

public class BA_Variant_WithDeadline_Prioritized extends
		NB_NACKFast_Variant_WithDeadline_Prioritized {

	public BA_Variant_WithDeadline_Prioritized(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Ratio Min (1/x)", default_ = "1") double waitTimeRatioMin,
			@ParamName(name = "Wait Time Ratio Max (1/x)", default_ = "1") double waitTimeRatioMax,
			@ParamName(name = "Deadline Awareness", default_ = "true") boolean deadlineAware,
			@ParamName(name = "Deadline Based Backoff", default_ = "false") boolean deadlineBasedBackoff,
			@ParamName(name = "Slotted", default_ = "false") boolean slotted) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency,
				switchingTime, doubleWay, priorityMode, quenchEnabled,
				waitTimeRatioMin, waitTimeRatioMax, deadlineAware,
				deadlineBasedBackoff, slotted);
		// TODO Auto-generated constructor stub
	}

	public BA_Variant_WithDeadline_Prioritized(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Min", default_ = "1") int waitTimeMin,
			@ParamName(name = "Wait Time Max", default_ = "1") int waitTimeMax,
			@ParamName(name = "Deadline Awareness", default_ = "true") boolean deadlineAware,
			@ParamName(name = "Deadline Based Backoff", default_ = "false") boolean deadlineBasedBackoff,
			@ParamName(name = "Slotted", default_ = "false") boolean slotted) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency,
				switchingTime, doubleWay, priorityMode, quenchEnabled,
				waitTimeMin, waitTimeMax, deadlineAware, deadlineBasedBackoff,
				slotted);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Receiver getExampleReceiver() {
		// TODO Auto-generated method stub
		return new BurstReceiver(-1);
	}
	
	

}
