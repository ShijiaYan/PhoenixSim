package edu.columbia.lrl.experiments.spinet;

import java.util.ArrayList;

import ch.epfl.general_libraries.traffic.Rate;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractTopologyBuilder;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.general.Message;

public abstract class AbstractSpinetBuilder extends AbstractTopologyBuilder {

	AbstractLinkDistanceModel linkDistMod;
	public boolean trafToSelf;
	private Rate refBW;
	
	public AbstractSpinetBuilder(AbstractLinkDistanceModel linkDistMod, boolean trafToSelf, Rate refBW) {
		super();
		this.linkDistMod = linkDistMod;
		this.trafToSelf = trafToSelf;
		this.refBW = refBW;
	}
	
	public AbstractSpinetBuilder(AbstractLinkDistanceModel linkDistMod, Rate refBW) {
		super();
		this.linkDistMod = linkDistMod;		
		this.refBW = refBW;		
	}
	
	public abstract int getMaxNumberOf2by2SwitchStages();
	

	public double getLinkToSwitchLatency() {
		return linkDistMod.getMaxLinkLatency();
	}
	
	public abstract InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests);
	
	/**
	 * Is final because setting the builder to the components is mandatory.
	 * Override buildSPinet for adding functionalities in sub-classes
	 */
	public final InitFeedback build(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests) {
		lwSimExperiment.setReferenceBandwidth(refBW);
		InitFeedback failure = buildSpinet(lwSimExperiment, dests);
		if (failure != null) return failure;
		for (LWSimComponent comp : dests) {
			if (comp instanceof SpinetComponent) {
				((SpinetComponent)comp).setSpinetBuilder(this);
			}
		}
		
		return null;
	}
	
	public Message getMessageToUse() {
		return new SpinetMessage();
	}

}
