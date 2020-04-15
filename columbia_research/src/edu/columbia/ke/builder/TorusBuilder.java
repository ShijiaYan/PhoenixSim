package edu.columbia.ke.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.ke.component.IndexTable;
import edu.columbia.ke.component.NACK_TorusSwitch;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.experiments.spinet.AbstractSpinetBuilder;
import edu.columbia.lrl.experiments.spinet.SpinnetBuffer;
import edu.columbia.lrl.experiments.spinet.link_model.AbstractLinkDistanceModel;
import edu.columbia.lrl.experiments.spinet.variants.SpinnetVariant;

public class TorusBuilder extends AbstractSpinetBuilder {
	
	AbstractLinkDistanceModel linkDistMod;
	SpinnetVariant variant;
	int dim;
		
	public TorusBuilder(
		@ParamName(name="Torus size per dimension", default_="4") int dim,
		@ParamName(name="SPInet variant") SpinnetVariant variant,
		@ParamName(name="Interconnection fiber length model") AbstractLinkDistanceModel linkDistMod,
		@ParamName(name="Send traffic to self?") boolean trafToSelf,
		Rate refBW) {
		super(linkDistMod, trafToSelf, refBW);
		this.dim = dim;
		this.variant = variant;
		this.linkDistMod = linkDistMod;
	}
	
	public Map<String, String> getAllParameters() {
		int clients = getNumberOfClients();
		Map<String, String> map = SimpleMap.getMap(	"clients", clients+"", 
													"Sending trafc to self?", trafToSelf+"");
		map.put("Torus Dim", this.dim+"");
		map.putAll(linkDistMod.getAllParameters());
		map.putAll(variant.getAllParameters());
		return map;
	}	
	
	public int getNumberOfClients() {
		return dim * dim * dim;
	}
	
	public int getMaxNumberOf2by2SwitchStages() {
		// return ((int)Math.sqrt((double)getNumberOfClients())-1)*2;
		return (dim - 1) * 3;
	}
	
	protected int[] getClientsMapping(LWSIMExperiment lwSimExperiment) {
		return AbstractExperimentBlock.getIntArray(0,getNumberOfClients()-1);		
	}
	
	protected AbstractTrafficGenerator[] gens;
	
	@Override
	public InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests) {
		int nbNode = getNumberOfClients();
		
		gens = new AbstractTrafficGenerator[nbNode];
		SpinnetBuffer[] bufs = new SpinnetBuffer[nbNode];	
		Receiver[] recs = new Receiver[nbNode];
		TransmissionLink[][] links = new TransmissionLink[2][nbNode];	
		
		for (int i = 0 ; i < links[0].length ; i++) {
			for (int j = 0 ; j < 1/*2*/; j++) {
				links[j][i] = new TransmissionLink(linkDistMod.getLinkLatency(i,nbNode));
				dests.add(links[j][i]);
			}
		}

		
		
		for (int i = 0 ; i < nbNode ; i++) {
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1,i);
			List<Integer> destsIndexes = MoreCollections.subsetOfN(0, nbNode -1);
			if (trafToSelf) {
				gens[i].setPossibleDestinationIndexes__(destsIndexes);
			} else {
				gens[i].setPossibleDestinationIndexesExcludingOne(destsIndexes, i);
			}
			
			dests.add(gens[i]);
			bufs[i] = variant.getExampleBuffer(i);
			bufs[i].setSpinetBuilder(this);
			dests.add(bufs[i]);
			recs[i] = super.getReceiver(i, variant.getExampleReceiver());
			dests.add(recs[i]);
		}
		
		int[] mapping = getClientsMapping(lwSimExperiment);
		
		// building the connections between the traffic generators and the input buffers, between the inputs buffers and the links,
		// and between the final links and the receivers
		for (int i = 0 ; i < nbNode ; i++) {
			gens[i].setTrafficDestination(bufs[mapping[i]]);
			bufs[i].setTrafficDestination(links[0][i]);
			//links[1][i].setTrafficDestination(recs[i]);
		}
		
		// build topology (multi path)
		/* added for defletion buffer 
		SpinnetBuffer[] dBufs = new SpinnetBuffer[nbNode];	
		for (int i = 0 ; i < nbNode ; i++) {
			dBufs[i] = variant.getExampleBuffer(-i-1);
			dBufs[i].setSpinetBuilder(this);
			dests.add(dBufs[i]);
		}
		/* added for defletion buffer */

		IndexTable iTable = new IndexTable(dim, dim, dim);
		
		NACK_TorusSwitch[] sw = new NACK_TorusSwitch[nbNode];
		for (int i = 0; i < nbNode; i++) {
			// create switch
			sw[i] = new NACK_TorusSwitch(i, nbNode,
					this.variant.getSwitchingTime(),
					this.linkDistMod.getMaxLinkLatency(),
					0, 
					dim, dim, dim, iTable);
			dests.add(sw[i]);
			//dBufs[i].setTrafficDestination(sw[i]);
		}

		for (int i = 0; i < nbNode; i++) {
			// add receiver to the switch
			//sw[i].addSuccessor(i, links[1][i]);
			sw[i].addSuccessor(i, recs[i]);	// connect to my own receiver (assuming virtual output queue -- multiple PD, should not use NB-receiver)
			
			// connect to neighbors
			sw[i].selfBuildTopology(sw);

			// connect generator to switch
			links[0][i].setTrafficDestination(sw[i]);
		}
		return null;		
		
	}

	@Override
	public int getMaxPacketSizeInBits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getNeighborhood(int fromAnode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		throw new IllegalStateException("getBisectionalBandwidth not implemented");
	}	
	@Override
	public void notifyEnd(double clock, int status) {	
	}
}
