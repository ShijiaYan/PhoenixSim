package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public abstract class SpinnetVariant {
	
	public abstract Receiver getExampleReceiver();
	public abstract TwoTwoSwitch getExampleSwitch(String id);
	public abstract SpinnetBuffer getExampleBuffer(int index);
	public abstract double getMaxPacketDurationNS();
	
	public abstract Map<String, String> getVariantSpecificParameters();
	public abstract String getVariantName();	
	
	public final static String SPINNET_VARIANT = "Spinnet variant";	
		
	protected final int maxBufferSize;
	protected final double bufferLatency;
	protected final double switchingTime;		
	protected final boolean doubleWay;
	
	protected SpinnetVariant(@ParamName(name="Maximum buffer depth (in packets)") int maxBufferSize, 
					  @ParamName(name="Buffer zero-load latency in ns") double bufferLatency, 
					  @ParamName(name="Switching time in ns") double switchingTime,
					  @ParamName(name="Allow double-way switches?") boolean doubleWay) {
		this.bufferLatency = bufferLatency;
		this.switchingTime = switchingTime;
		this.doubleWay = doubleWay;
		this.maxBufferSize = maxBufferSize;
	}
	
	public final Map<String,String> getAllParameters() {
		Map<String, String> map = getVariantSpecificParameters();
		map.put(SPINNET_VARIANT, getVariantName());
		map.put("Switching time", switchingTime+"");
		map.put("Buffer size", maxBufferSize +"");
		map.put("Buffer latency", bufferLatency +"");
		map.put("Double way switches", doubleWay + "");
		
		return map;
	}
	
	public double getSwitchingTime() {
		return switchingTime;
	}

	

	
}
