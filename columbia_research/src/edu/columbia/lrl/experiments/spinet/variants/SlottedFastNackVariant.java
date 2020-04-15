package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SlottedFastNackVariant extends FastNackVariant {
	
	private double slotDuration;

	public SlottedFastNackVariant(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay,
			@ParamName(name = "Slot duration", default_ = "100") double slotDuration) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency,
				switchingTime, doubleWay);
		
		this.slotDuration = slotDuration;
		// TODO Auto-generated constructor stub
	}
	
	public SpinnetBuffer getExampleBuffer(int index) {
		return new NACKFast_SlottedBuffer(maxBufferSize, bufferLatency, switchingTime, slotDuration, index);
	}
	
	@Override
	public  Map<String,String> getVariantSpecificParameters() {
		SimpleMap<String, String> map = new SimpleMap<String, String>(1);
		map.put("slot duration", slotDuration+"");
		return map;
	}
	
	public String getVariantName() {
		return "Slotted_FastNACK";
	}	
	
	

}
