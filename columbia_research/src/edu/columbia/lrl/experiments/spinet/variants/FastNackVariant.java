package edu.columbia.lrl.experiments.spinet.variants;

import ch.epfl.general_libraries.clazzes.ParamName;

import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;

public class FastNackVariant extends NACKVariant {	
		
	public FastNackVariant(@ParamName(name="Switch NACK reaction time in ns", default_="1") double switchNACKReactionTime, 
						   @ParamName(name="Maximum buffer depth (in packets)", default_="10000") int maxBufferSize, 
						   @ParamName(name="Buffer zero-load latency in ns", default_="1") double bufferLatency, 
						   @ParamName(name="Switching time in ns", default_="1") double switchingTime, 
						   @ParamName(name="Allow double-way switches?", default_="true") boolean doubleWay) {
		super(switchNACKReactionTime,  maxBufferSize, bufferLatency, switchingTime, doubleWay);
	}

	public SpinnetBuffer getExampleBuffer(int index) {
		return new NACKFast_Buffer(maxBufferSize, bufferLatency, switchingTime, index);
	}
	
	public String getVariantName() {
		return "FastNACK";
	}
	
	
}
