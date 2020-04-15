package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.ImprovedBuffer;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public class BlockingVariant extends SpinnetVariant {
	
	private boolean useImprovedBuffer = false;
	
	@ConstructorDef(ignore=true)
	public BlockingVariant(boolean useImprovedBuffer, 
						   int maxBufferSize, 
						   double bufferLatency, 
						   double switchingTime, 
						   boolean doubleWay) {
		super(maxBufferSize, bufferLatency, switchingTime, doubleWay);
		this.useImprovedBuffer = useImprovedBuffer;
	}
	
	public BlockingVariant(@ParamName(name="Maximum buffer depth (in packets)") int maxBufferSize, 
						   @ParamName(name="Buffer processing delay in ns") double bufferLatency, 
						   @ParamName(name="Switching time in ns") double switchingTime, 
						   @ParamName(name="Allow double-way switches?") boolean doubleWay) {
		super(maxBufferSize, bufferLatency, switchingTime, doubleWay);
		this.useImprovedBuffer = true;
	}	
	
	public Receiver getExampleReceiver() {
		return new Receiver(-1);
	}
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new TwoTwoSwitch(id, switchingTime, doubleWay);
	}
	public SpinnetBuffer getExampleBuffer(int index) {
		if (useImprovedBuffer)
			return new ImprovedBuffer(maxBufferSize, bufferLatency, switchingTime, index);
		else
			return new SpinnetBuffer(maxBufferSize, bufferLatency, switchingTime, index);
	}	
	
	public Map<String, String> getVariantSpecificParameters() {
		String buf;
		if (useImprovedBuffer)
			buf = "Improved_buffer";
		else
			buf = "Basic_buffer";
		return SimpleMap.getMap("Buffer type", buf);
	}
	public String getVariantName() {
		return "Blocking";
	}
	
	@Override
	public double getMaxPacketDurationNS() {
		return Integer.MAX_VALUE;
	}	
}
