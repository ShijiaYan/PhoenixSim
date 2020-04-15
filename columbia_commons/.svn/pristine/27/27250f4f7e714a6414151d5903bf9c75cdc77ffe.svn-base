package edu.columbia.lrl.LWSim.builders;

import java.util.ArrayList;
import java.util.Map;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Receiver;

public abstract class AbstractNumberOfClientConfigurableBuilder {
	
	protected NumberOfClientBasedBuilder builder;
	protected LWSIMExperiment lwSimExperiment;
	
	public abstract InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients);
	
	public InitFeedback build(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests, int nbClients) {
		this.lwSimExperiment = lwSimExperiment;
		return buildSubBuilder(dests, nbClients);
	}
	
	public abstract Map<String, String> getAllParameters();

	public abstract int getMaxPacketSizeInBits();	
	public abstract double getTotalInjectionBandwidthRatio();
	public abstract int[][] getNeighborhood(int fromAnode);
	public abstract void notifyEnd(double clock, int status);
		
	public void setBuilder(NumberOfClientBasedBuilder builder) {
		this.builder = builder;
	}
	
	public Receiver getReceiver(int index) {
		return builder.getReceiver(index);
	}

	public Receiver getReceiver(int index, Receiver example) {
		return builder.getReceiver(index, example);
	}






	

}
