package edu.columbia.ke.builder;

import java.util.ArrayList;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.traffic.AbstractRandomTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.deadlines.AbstractTTLGenerator;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;

public class RandomTTL_FromArbitraryTopologyBuilder extends
		FromArbitraryTopologyBuilder {

	public RandomTTL_FromArbitraryTopologyBuilder(
			@ParamName(name = "SPInet plane generator") AbstractTopologyGenerator gen,
			@ParamName(name = "SPInet variant") SpinnetVariant variant,
			@ParamName(name = "Interconnection fiber length model") AbstractLinkDistanceModel linkDistMod,
			@ParamName(name = "Send traffic to self?") boolean trafToSelf,
			Rate refBW) {
		super(gen, variant, linkDistMod, trafToSelf, refBW);
		// TODO Auto-generated constructor stub
	}

	protected void setRandomTTL(AbstractTrafficGenerator gen, int index) {
		AbstractRandomTrafficGenerator rdGen = (AbstractRandomTrafficGenerator)gen;
		AbstractTTLGenerator ttlGen = rdGen.getTTLGen();
		double oldTTLMean = ttlGen.getMean();
		//double newTTLMean = getNewTTLMean(oldTTLMean);
		//ttlGen.setMean(newTTLMean);
		ttlGen.setMean((index % 9 * 0 + 1) * oldTTLMean);
	}

	@Override
	public InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment,
			ArrayList<LWSimComponent> dests) {
		super.buildSpinet(lwSimExperiment, dests);
		int clients = getNumberOfClients();
		for (int i = 0 ; i < clients ; i++) {
			// added for TTL variation
			setRandomTTL(gens[i], i);
		}
		return null;
	}
}
