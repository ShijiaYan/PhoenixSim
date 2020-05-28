package edu.columbia.lrl.experiments.spinet;

import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class SpinnetBuffer extends Buffer implements SpinetComponent {
	
	protected double spinetSwitchingTimeNS;
	protected AbstractSpinetBuilder builder;
	
	private double headerTime;
	private double minTransmissionTime;

	public SpinnetBuffer(int maxSize, double bufferLatency, double spinetSwitchingTimeNS, int index) {
		super(maxSize, bufferLatency, index);
		this.spinetSwitchingTimeNS = spinetSwitchingTimeNS;
		// TODO Auto-generated constructor stub
	}

	protected double getEOTTimeNS(Message toSend, Evt e) {
		double packet = lwSimExperiment.getReferenceBandwidth().getTime(toSend.sizeInBits).getNanoseconds()+ headerTime;
		double duration = Math.max(minTransmissionTime, packet);
		toSend.lastDuration = duration;
		return duration+e.getTimeNS();
	}
	
	protected double getEOTTimeNS(Message toSend, double startTime) {
		double min = builder.getLinkToSwitchLatency()*2 + builder.getMaxNumberOf2by2SwitchStages()*spinetSwitchingTimeNS*2;
		double packet = lwSimExperiment.getReferenceBandwidth().getTime(toSend.sizeInBits).getNanoseconds()+ builder.getMaxNumberOf2by2SwitchStages()*spinetSwitchingTimeNS;
		double duration = Math.max(min, packet);
		toSend.lastDuration = duration;
		return duration+startTime;
	}

	@Override
	public void setSpinetBuilder(AbstractSpinetBuilder builder) {
		this.builder = builder;	
		headerTime = builder.getMaxNumberOf2by2SwitchStages()*spinetSwitchingTimeNS;
		minTransmissionTime = builder.getLinkToSwitchLatency()*2 + 2*headerTime;
	}
	
	// if
	protected void processConsumerEvent(Evt e) {
		defineSpinetMessage(e);
		super.processConsumerEvent(e);
	}
	
	protected SpinetMessage defineSpinetMessage(Evt e) {
		SpinetMessage msg = (SpinetMessage)e.getMessage();
		defineSpinetMessage(msg);
		return msg;
	}
	
	protected void defineSpinetMessage(SpinetMessage msg) {
		msg.setNumberOfHeaderOffset(builder.getMaxNumberOf2by2SwitchStages());
		msg.setOffsetDurationNS(spinetSwitchingTimeNS);
		msg.setTransmissionTime(lwSimExperiment.getReferenceBandwidth().getTime(msg.sizeInBits).getNanoseconds());		
		msg.clearOccupyResource();
		
		if (!msg.deadlineSet){
			double deadline = msg.timeEmitted + msg.getTransmissionTimeNS() + msg.getInitTimeToLive();
			msg.setDeadline(deadline);
		}
	}
}
