package edu.columbia.ke.spinet.variant;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public class NB_NACKFast_Variant_WithDeadline_Prioritized extends NB_NACKFast_Backoff_Variant {
	
	private boolean quenchEnabled;
	private boolean deadlineAware;
	private boolean deadlineBasedBackoff;
	private boolean slotted;

	public NB_NACKFast_Variant_WithDeadline_Prioritized(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Ratio Min (1/x)") double waitTimeRatioMin,
			@ParamName(name = "Wait Time Ratio Max (1/x)") double waitTimeRatioMax,
			@ParamName(name = "Deadline Awareness", default_ = "true") boolean deadlineAware,
			@ParamName(name = "Deadline Based Backoff", default_ = "false") boolean deadlineBasedBackoff, 
			@ParamName(name = "Slotted", default_ = "false") boolean slotted) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency, switchingTime,
				doubleWay, priorityMode, quenchEnabled, waitTimeRatioMin,
				waitTimeRatioMax);
		this.quenchEnabled = quenchEnabled;
		this.deadlineAware = deadlineAware;
		this.deadlineBasedBackoff = deadlineBasedBackoff;
		this.slotted = slotted;
	}

	public NB_NACKFast_Variant_WithDeadline_Prioritized(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Min") int waitTimeMin,
			@ParamName(name = "Wait Time Max") int waitTimeMax, 
			@ParamName(name = "Deadline Awareness", default_ = "true") boolean deadlineAware,
			@ParamName(name = "Deadline Based Backoff", default_ = "false") boolean deadlineBasedBackoff, 
			@ParamName(name = "Slotted", default_ = "false") boolean slotted) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency, switchingTime,
				doubleWay, priorityMode, quenchEnabled, waitTimeMin, waitTimeMax);
		this.quenchEnabled = quenchEnabled;
		this.deadlineAware = deadlineAware;
		this.deadlineBasedBackoff = deadlineBasedBackoff;
		this.slotted = slotted;
	}

	@Override
	public SpinnetBuffer getExampleBuffer(int index) {
		
		if (slotted) {
			/* change the slotDuration based on packet size, line rate and RTT
			 * 
			 */
			int slotDuration = 18;	
			if (this.priorityMode == PriorityMode.OBLIVIOUS) {
				return new Slotted_NACKFast_Quenchable_Buffer(maxBufferSize,
						bufferLatency, switchingTime, slotDuration, quenchEnabled, index);
			}

			if (this.priorityMode == PriorityMode.EDF)
				return new Slotted_New_DeadlinePrioritized_Buffer(maxBufferSize,
						bufferLatency, switchingTime, index, priorityMode,
						quenchEnabled, deadlineBasedBackoff, slotDuration);

			return new Slotted_NACKFast_Deadline_NQPBuffer(maxBufferSize,
					bufferLatency, switchingTime, index, priorityMode,
					quenchEnabled, deadlineBasedBackoff, slotDuration);
		}
		
		if (this.priorityMode == PriorityMode.OBLIVIOUS) {
				return new NACKFast_Quenchable_Buffer(maxBufferSize, bufferLatency,
						switchingTime, quenchEnabled, index);
		}
		
		if (this.priorityMode == PriorityMode.EDF)
			return new New_DeadlinePrioritized_Buffer(maxBufferSize,
				bufferLatency, switchingTime, index, priorityMode, quenchEnabled, deadlineBasedBackoff);
		
		if (this.priorityMode == PriorityMode.NQPrioritized_OCF)
			return new NACKFast_OCF_NQPBuffer(maxBufferSize, bufferLatency,
					switchingTime, index, priorityMode);
		
		return new NACKFast_Deadline_NQPBuffer(maxBufferSize,
				bufferLatency, switchingTime, index, priorityMode, quenchEnabled, deadlineBasedBackoff);
		
		
		
		/*if (!this.deadlineAware) {
			return super.getExampleBuffer(index);
		} else {
			if (this.priorityMode == PriorityMode.OBLIVIOUS)
				return new NACKFast_Buffer(maxBufferSize, bufferLatency,
						switchingTime, index);
			else
				return new NACKFast_Deadline_PrioritizedBuffer(maxBufferSize,
						bufferLatency, switchingTime, index, priorityMode,
						quenchEnabled, waitTimeRatioMin, waitTimeRatioMax, deadlineBasedBackoff);
		}*/
	}

	@Override
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new DA_NACK_TwoTwoSwitch(id, switchingTime, switchNACKReactionTime, doubleWay);
	}
	
	@Override
	public Receiver getExampleReceiver() {
		return new Receiver(-1);
	}

	@Override
	public Map<String, String> getVariantSpecificParameters() {
		Map<String, String> map =  super.getVariantSpecificParameters();
		map.put("Quench Enabled", this.quenchEnabled+"");
		map.put("Deadline Awareness", this.deadlineAware+"");
		map.put("Deadline Based Backoff?", this.deadlineBasedBackoff+"");
		map.put("Slotted", this.slotted+"");
		return map;
	}
	
	@Override
	public String getVariantName() {
		return "LWTAB";
	}	

	
	

}
