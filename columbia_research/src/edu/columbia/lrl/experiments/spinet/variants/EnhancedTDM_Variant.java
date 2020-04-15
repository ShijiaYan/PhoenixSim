package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public class EnhancedTDM_Variant extends TDM_Variant {
	
	private double switchNACKReactionTime;	
	private boolean noLongPackets;
	private boolean ETDM_ACK_MODE;

	public EnhancedTDM_Variant(@ParamName(name="Maximum buffer depth (in packets)", default_="10000") int maxBufferSize, 
								@ParamName(name="NACK reaction time", default_="0") double switchNACKReactionTime, 
								@ParamName(name="Buffer zero-load latency in ns", default_="0") double bufferLatency,
								@ParamName(name="Switching time in ns", default_="1") double switchingTime,  
								@ParamName(name="Slot duration") double slotDuration) {
		super(maxBufferSize, bufferLatency, switchingTime, slotDuration);
		this.switchNACKReactionTime = switchNACKReactionTime;
		this.noLongPackets = true;
		ETDM_ACK_MODE = false;
	}
	
	public EnhancedTDM_Variant(
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "NACK reaction time", default_ = "0") double switchNACKReactionTime,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "0") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Slot duration") double slotDuration,
			@ParamName(name = "Forbid longer packets?") boolean noLongPackets) {
		super(maxBufferSize, bufferLatency, switchingTime, slotDuration);
		this.switchNACKReactionTime = switchNACKReactionTime;
		this.noLongPackets = noLongPackets;
		ETDM_ACK_MODE = false;
	}

	public EnhancedTDM_Variant(
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "NACK reaction time", default_ = "0") double switchNACKReactionTime,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "0") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Slot duration") double slotDuration,
			@ParamName(name = "Forbid longer packets?") boolean noLongPackets,
			@ParamName(name = "ACK mode?") boolean ACK_Mode) {
		super(maxBufferSize, bufferLatency, switchingTime, slotDuration);
		this.switchNACKReactionTime = switchNACKReactionTime;
		this.noLongPackets = noLongPackets;
		this.ETDM_ACK_MODE = ACK_Mode;
	}
	
	@Override
	public Receiver getExampleReceiver() {
		return new PriorityReceiver(-1);
	}	
	
	@Override
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new PriorityAwareTwoTwoSwitch(id, switchingTime, switchNACKReactionTime, doubleWay);
	}
	
	@Override
	public SpinnetBuffer getExampleBuffer(int index) {
		EnhancedTDMSpinnetBuffer buf = new EnhancedTDMSpinnetBuffer(maxBufferSize, bufferLatency, switchingTime, slotDuration, index, noLongPackets, ETDM_ACK_MODE);
		bufferMap.put(index, buf);
		return buf;
	}
	
	@Override
	public double getMaxPacketDurationNS() {
		return slotDuration-this.switchNACKReactionTime;
	}
	
	@Override
	public String getVariantName() {
		return "Enhanced TDM variant";
	}	
	
	@Override
	public Map<String, String> getVariantSpecificParameters() {
		Map<String, String> map =  super.getVariantSpecificParameters();
		map.put("ACK Mode", this.ETDM_ACK_MODE+"");
		return map;
	}

}
