package edu.columbia.ke.spinet.variant;

import java.util.Random;

import edu.columbia.lrl.experiments.spinet.SpinetMessage;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;

public class SpinnetBuffer_WithDeadline extends SpinnetBuffer {

	public SpinnetBuffer_WithDeadline(int maxSize, double bufferLatency,
			double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, spinetSwitchingTimeNS, index);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void defineSpinetMessage(SpinetMessage msg) {
		// TODO Auto-generated method stub
		super.defineSpinetMessage(msg);
		double randomDeadline = msg.timeEmitted + msg.getTransmissionTimeNS() + randomUniformTimeMargin(100, 1000);
		msg.setDeadline(randomDeadline);
		msg.setSpinetPriority( (int) msg.getDeadline()) ;
	}
	
	public double randomUniformTimeMargin(double min, double max){
		return min + (max - min) * new Random().nextDouble();
	}
}
