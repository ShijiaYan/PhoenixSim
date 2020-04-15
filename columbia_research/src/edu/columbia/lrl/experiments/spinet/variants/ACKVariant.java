package edu.columbia.lrl.experiments.spinet.variants;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.TwoTwoSwitch;

public class ACKVariant extends SpinnetVariant {
	
	private double recReacTime;
	private double bufferTimeout;

	
	public ACKVariant(@ParamName(name="Receiver reaction time in ns(to send the ACK") double receiverReactionTime, 
					  @ParamName(name="Time-out time in ns") double bufferTimeout, 
					  @ParamName(name="Maximum buffer depth (in packets)") int maxBufferSize, 
					  @ParamName(name="Buffer zero-load latency in ns") double bufferLatency, 
					  @ParamName(name="Switching time in ns") double switchingTime,
					  @ParamName(name="Allow double-way switches?") boolean doubleWay) {
		super(maxBufferSize, bufferLatency, switchingTime, doubleWay);
		this.recReacTime = receiverReactionTime;
		this.bufferTimeout = bufferTimeout;
	}
	
	public Receiver getExampleReceiver() {
		ACK_Receiver rec = new ACK_Receiver(-1, recReacTime);
		return rec;
	}
	public TwoTwoSwitch getExampleSwitch(String id) {
		return new ACK_TwoTwoSwitch(id, switchingTime, doubleWay);
	}
	public SpinnetBuffer getExampleBuffer(int index) {
		ACK_Buffer buf = new ACK_Buffer(maxBufferSize, bufferLatency, switchingTime, index, bufferTimeout);
		return buf;
	}				  	
	
	public Map<String, String> getVariantSpecificParameters() {
		return SimpleMap.getMap("Receiver reaction time", recReacTime+"",
								"Time out duration", bufferTimeout +"");
	}
	public String getVariantName() {
		return "ACK";
	}

	@Override
	public double getMaxPacketDurationNS() {
		return Integer.MAX_VALUE;
	}	
	
}
