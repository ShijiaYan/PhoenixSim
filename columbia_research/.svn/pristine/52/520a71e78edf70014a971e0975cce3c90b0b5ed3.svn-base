package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public class NACKVariant extends SpinnetVariant {

	protected double switchNACKReactionTime;	
			
	public double getSwitchingTime() {
		return switchingTime;
	}

	public NACKVariant(@ParamName(name="Switch NACK reaction time in ns", default_="1") double switchNACKReactionTime, 
			           @ParamName(name="Maximum buffer depth (in packets)", default_="10000")  int maxBufferSize, 
					   @ParamName(name="Buffer zero-load latency in ns", default_="1") double bufferLatency, 
					   @ParamName(name="Switching time in ns", default_="1")  double switchingTime, 
					   @ParamName(name="Allow double-way switches?", default_="true") boolean doubleWay) {
		super(maxBufferSize, bufferLatency, switchingTime, doubleWay);
		this.switchNACKReactionTime = switchNACKReactionTime;
	}
	
	public Receiver getExampleReceiver() {
		return new Receiver(-1);
	}
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new NACK_TwoTwoSwitch(id, switchingTime, switchNACKReactionTime, doubleWay);
	}
	public SpinnetBuffer getExampleBuffer(int index) {
		return new NACK_Buffer(maxBufferSize, bufferLatency, switchingTime, index);
	}
	
	public Map<String, String> getVariantSpecificParameters() {
		return SimpleMap.getMap("Switch NACK reaction time", switchNACKReactionTime+"");
	}
	public String getVariantName() {
		return "NACK";
	}	
	
	@Override
	public double getMaxPacketDurationNS() {
		return Integer.MAX_VALUE;
	}	

}
