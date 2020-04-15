package edu.columbia.ke.spinet.variant;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.variants.NACKFast_Buffer;
import edu.columbia.lrl.experiments.spinet.variants.NACKFast_SlottedBuffer;

public class NB_NACKFast_Backoff_Variant extends NB_NACKFast_Variant {
	
	private int waitTimeMin;
	private int waitTimeMax;
	
	protected double waitTimeRatioMin;
	protected double waitTimeRatioMax;
	
	private boolean ratioMode;
	protected PriorityMode priorityMode;
	

	public NB_NACKFast_Backoff_Variant(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Min"/*, default_ = "0"*/) int waitTimeMin,
			@ParamName(name = "Wait Time Max"/*, default_ = "256"*/) int waitTimeMax) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency, switchingTime,
				doubleWay);
		this.priorityMode = priorityMode;
		this.waitTimeMin = waitTimeMin;
		this.waitTimeMax = waitTimeMax;
		this.ratioMode = false;
	}
	
	public NB_NACKFast_Backoff_Variant(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Priority Mode") PriorityMode priorityMode,
			@ParamName(name = "Quenching Enabled", default_ = "false") boolean quenchEnabled,
			@ParamName(name = "Wait Time Ratio Min (1/x)"/*, default_ = "0"*/) double waitTimeRatioMin,
			@ParamName(name = "Wait Time Ratio Max (1/x)"/*, default_ = "256"*/) double waitTimeRatioMax) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency, switchingTime,
				doubleWay);
		this.priorityMode = priorityMode;
		this.waitTimeRatioMin = waitTimeRatioMin;
		this.waitTimeRatioMax = waitTimeRatioMax;
		this.ratioMode = true;
	}

	@Override
	public SpinnetBuffer getExampleBuffer(int index) {
		switch (this.priorityMode){
		case EDF_RETRAN_WAIT_TRUNCATED:
			if (this.ratioMode)
				return new NACKFast_Truncated_Backoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeRatioMin, waitTimeRatioMax);
			else
				return new NACKFast_Truncated_Backoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeMin, waitTimeMax);
		case EDF_RETRAN_WAIT_TRUNCATED_SMARTER:
			if (this.ratioMode)
				return new NACKFast_SmarterTruncatedBackoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeRatioMin, waitTimeRatioMax);
			else
				return new NACKFast_SmarterTruncatedBackoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeMin, waitTimeMax);
		case LEAST_WAIT_TIME:
			return new NACKFast_LeastWaitTime_Buffer(maxBufferSize, bufferLatency, switchingTime, index);
		case EDF_RETRAN_WAIT:
			return new NACKFast_Backoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index);
		case EDF_MILD:
			return new NACKFast_MILD_Backoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeRatioMin);
		case EDF_DIDD:
			return new NACKFast_DIDD_Backoff_Buffer(maxBufferSize, bufferLatency, switchingTime, index, waitTimeRatioMin);
		case Synchronized:
			return new NACKFast_SlottedBuffer(maxBufferSize, bufferLatency, switchingTime, 2200, index);
		case Slotted_ALOHA:
			return new slottedALOHABuffer(maxBufferSize, bufferLatency, switchingTime, 2200, index, waitTimeRatioMin);
		default:
			return new NACKFast_Buffer(maxBufferSize, bufferLatency, switchingTime, index);
		}
	}

	@Override
	public Map<String, String> getVariantSpecificParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = super.getVariantSpecificParameters();
		
		map.put("Priority Mode", priorityMode+"");
		
		if (this.ratioMode){
			map.put("Wait Time Ratio for Min", this.waitTimeRatioMin+"");
			map.put("Wait Time Ratio for Max", this.waitTimeRatioMax+"");
		}
		else {
			map.put("Wait Time Min", this.waitTimeMin+"");
			map.put("Wait Time Max", this.waitTimeMax+"");
		}
		return map;
	}
	
	

}
