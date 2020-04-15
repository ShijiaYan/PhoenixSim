package edu.columbia.lrl.LWSim.builders;

import java.util.ArrayList;
import java.util.HashMap;
import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.utils.Pair;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.general.Message;

public abstract class AbstractTopologyBuilder implements ExperimentBlock {
	
	protected LWSIMExperiment lwSimExp;
	
	private ArrayList<LWSimComponent> elements;
	// model part
	private HashMap<Integer, Receiver> receivers = new HashMap<Integer, Receiver>();
	
	
	public AbstractTopologyBuilder() {}
	
	public Pair<InitFeedback, ArrayList<LWSimComponent>> buildAbstract(LWSIMExperiment lwSimExperiment) {
		this.lwSimExp = lwSimExperiment;
		elements = new ArrayList<LWSimComponent>();
		InitFeedback failure = build(lwSimExperiment, elements);
		Pair<InitFeedback, ArrayList<LWSimComponent>> pair = new Pair<InitFeedback, ArrayList<LWSimComponent>>(failure, elements);
		return pair;
	}
	
	public ArrayList<LWSimComponent> getModelElements() {
		return elements;
	}
	
	public Receiver getReceiver(int index) {
		return getReceiver(index, new Receiver(index));
	}
	
	/**
	 * 
	 * @param The global address of this receiver
	 * @param An example receiver whose type will be taken to create a new one
	 * @return A registered receiver
	 */
	public Receiver getReceiver(int index, Receiver example) {
		Receiver toRet = receivers.get(index);
		if (toRet != null) return toRet;
		Receiver rec = example.getReceiverCopy(index);
		receivers.put(index, rec);
		return rec;
	}
	
	
	public AbstractTrafficGenerator getExampleGeneratorCopy(double loadCoeff, int index) {
		return lwSimExp.getTrafficGenerator().getCopy(loadCoeff, index);
	}
	
	public abstract InitFeedback build(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests);
	public abstract int getNumberOfClients();
	public abstract int getMaxPacketSizeInBits();
	public abstract double getTotalInjectionBandwidthRatio();
	public abstract void notifyEnd(double clock, int status);
	/**
	 * Sub-classes must provide returning a template of the message to be used
	 * @return
	 */
	public abstract Message getMessageToUse();

	public abstract int[][] getNeighborhood(int fromAnode);



}
