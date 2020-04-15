package edu.columbia.ke.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.ke.component.burst_assembly.AbstractBurstAssembler;
import edu.columbia.ke.component.burst_assembly.BurstAssemblerGroup;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.experiments.spinet.FromGeneratorSpinetTopologyBuilder;
import edu.columbia.lrl.experiments.spinet.SpinetSwitch;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;
import edu.columbia.lrl.experiments.spinet.variants.TDM_Variant;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;

public class BurstAssembledSpinetBuilder extends
		FromGeneratorSpinetTopologyBuilder {
	
	private AbstractBurstAssembler exampleBstAssblr;

	public BurstAssembledSpinetBuilder(
			@ParamName(name = "SPInet plane generator") AbstractSwitchArchitectureGenerator gen,
			@ParamName(name = "Burst Assembler") AbstractBurstAssembler exampleBstAssblr,
			@ParamName(name = "SPInet variant") SpinnetVariant variant,
			@ParamName(name = "Interconnection fiber length model") AbstractLinkDistanceModel linkDistMod,
			@ParamName(name = "Send traffic to self?") boolean trafToSelf,
			Rate refBW) {
		super(gen, variant, linkDistMod, trafToSelf, refBW);
		this.exampleBstAssblr = exampleBstAssblr;
	}
	
	@Override
	public InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests) {
		int clients = getNumberOfClients();
		
		gens = new AbstractTrafficGenerator[clients];
		SpinnetBuffer[] bufs = new SpinnetBuffer[clients];	
		BurstAssemblerGroup[] baGroup = new BurstAssemblerGroup[clients];	
		Receiver[] recs = new Receiver[clients];
		TransmissionLink[][] links = new TransmissionLink[2][clients];	
		
		for (int i = 0 ; i < links[0].length ; i++) {
			for (int j = 0 ; j < 2 ; j++) {
				links[j][i] = new TransmissionLink(linkDistMod.getLinkLatency(i,clients));
				dests.add(links[j][i]);
			}
		}
		
		for (int i = 0 ; i < clients ; i++) {
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1,i);
			List<Integer> destsIndexes = MoreCollections.subsetOfN(0, clients -1);
			if (trafToSelf) {
				gens[i].setPossibleDestinationIndexes__(destsIndexes);
			} else {
				gens[i].setPossibleDestinationIndexesExcludingOne(destsIndexes, i);
			}
			
			dests.add(gens[i]);
			
			baGroup[i] = new BurstAssemblerGroup(clients, exampleBstAssblr, dests);
			dests.add(baGroup[i]);
			
			bufs[i] = variant.getExampleBuffer(i);
			bufs[i].setSpinetBuilder(this);
			dests.add(bufs[i]);
			recs[i] = super.getReceiver(i, variant.getExampleReceiver());
			dests.add(recs[i]);
		//	return null;
		}
		
		int[] mapping = getClientsMapping(lwSimExperiment);
		
		// building the connections between the traffic generators and the input buffers, between the inputs buffers and the links,
		// and between the final links and the receivers
		for (int i = 0 ; i < clients ; i++) {
			// add burst assembler between gen and buf
			gens[i].setTrafficDestination(baGroup[i]);
			baGroup[i].setTrafficDestination(bufs[mapping[i]]);
			bufs[i].setTrafficDestination(links[0][i]);
			links[1][i].setTrafficDestination(recs[i]);
		}		
		
		SpinetSwitch spinetSwitch = new SpinetSwitch(gen, variant);
		
		spinetSwitch.build(lwSimExperiment, dests, links[0], links[1], null);		
		

		
		// connecting the traffic generators
		// this occurs AFTER computing the routing decisions to do not interfere with the topology
		for (Integer input : gen.getInputNodesIndexes()) {
			NodeContainer entryPoint = spinetSwitch.getagh().getNodeContainer(input);
			entryPoint.setNode(gens[entryPoint.attribute("input").intValue()]);
		}	
		
		//////////////////// REMOVE THIS AS SOON AS POSSIBLE (for ICON 2013) ////////////
		if (variant instanceof TDM_Variant)
			((TDM_Variant)variant).configureTDM(this, gen);
		
		return null;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("Switch architecture", gen.getClass().getSimpleName());
		map.putAll(gen.getGeneratorParameters());
		map.putAll(linkDistMod.getAllParameters());
		map.putAll(variant.getAllParameters());
		map.putAll(this.exampleBstAssblr.getAllParameters());
		return map;
	}	
	
}
