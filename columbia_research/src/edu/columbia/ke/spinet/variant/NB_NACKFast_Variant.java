package edu.columbia.ke.spinet.variant;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.experiments.spinet.variants.FastNackVariant;

public class NB_NACKFast_Variant extends FastNackVariant {

	public NB_NACKFast_Variant(
			@ParamName(name = "Switch NACK reaction time in ns", default_ = "1") double switchNACKReactionTime,
			@ParamName(name = "Maximum buffer depth (in packets)", default_ = "10000") int maxBufferSize,
			@ParamName(name = "Buffer zero-load latency in ns", default_ = "1") double bufferLatency,
			@ParamName(name = "Switching time in ns", default_ = "1") double switchingTime,
			@ParamName(name = "Allow double-way switches?", default_ = "true") boolean doubleWay) {
		super(switchNACKReactionTime, maxBufferSize, bufferLatency, switchingTime,
				doubleWay);
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public Receiver getExampleReceiver() {
		return new NonBlockingReceiver(-1);
	}*/

}
