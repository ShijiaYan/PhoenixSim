package edu.columbia.ke.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.path.BFSEnumeratedPathSet;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import edu.columbia.ke.component.NACK_HighRadixSwitch;
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

public class FromArbitraryTopologyBuilder extends AbstractSpinetBuilder {
	
	AbstractTopologyGenerator gen;
	AbstractLinkDistanceModel linkDistMod;
	SpinnetVariant variant;
		
	public FromArbitraryTopologyBuilder(
		@ParamName(name="SPInet plane generator") AbstractTopologyGenerator gen,
		@ParamName(name="SPInet variant") SpinnetVariant variant,
		@ParamName(name="Interconnection fiber length model") AbstractLinkDistanceModel linkDistMod,
		@ParamName(name="Send traffic to self?") boolean trafToSelf,
		Rate refBW) {
		super(linkDistMod, trafToSelf, refBW);
		this.gen = gen;
		this.variant = variant;
		this.linkDistMod = linkDistMod;
	}
	
	public Map<String, String> getAllParameters() {
		int clients = getNumberOfClients();
		Map<String, String> map = SimpleMap.getMap(	"clients", clients+"", 
													"Sending trafc to self?", trafToSelf+"");
		map.put("Switch architecture", gen.getClass().getSimpleName());
		map.putAll(gen.getGeneratorParameters());
		map.putAll(linkDistMod.getAllParameters());
		map.putAll(variant.getAllParameters());
		return map;
	}	
	
	public int getNumberOfClients() {
		return gen.getNumberOfNodes();
	}
	
	public int getMaxNumberOf2by2SwitchStages() {
		return ((int)Math.sqrt((double)getNumberOfClients())-1)*2;
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
			for (int j = 0 ; j < 1 /*2*/; j++) {
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
		
		// build topology (single path)
		/*AbstractGraphHandler graph = Javanco.getDefaultGraphHandler(false);
		gen.generate(graph);
		boolean[][] m = graph.getEditedLayer().getIncidenceMatrix(true);
		
		ShortestPathAlgorithm alg = new ShortestPathAlgorithm(m, true);
		alg.computeAll();
		int[][] n = alg.getSuccessors();	
		
		NACK_HighRadixSwitch[] sw = new NACK_HighRadixSwitch[nbNode];
		for (int i = 0 ; i < nbNode; i++) {
			// create switch
			sw[i] = new NACK_HighRadixSwitch(i, nbNode, this.variant.getSwitchingTime(), this.linkDistMod.getMaxLinkLatency(), 0);
			dests.add(sw[i]);
		}
		
		for (int i = 0 ; i < nbNode; i++) {
			// add successors to the switch
			sw[i].addSuccessor(i, links[1][i]);	//connect to my own receiver
			for (int j = 0; j < nbNode; j++){
				if (m[i][j])
					sw[i].addSuccessor(j, sw[j]);
			}
			
			// connect generator to switch
			links[0][i].setTrafficDestination(sw[i]);
			
			// fill in routing table, including to myself
			for (int j = 0; j < nbNode; j++){
				sw[i].addRoutingEntry(j, n[i][j], 0);
			}
		}*/
		// build topology (single path)
		
		// build topology (multi path)
		AbstractGraphHandler graph = Javanco.getDefaultGraphHandler(false);
		gen.generate(graph);
		// true: directed; false: undirected (bidirectional edges)
		double[][] m = graph.getEditedLayer().getIncidenceMatrixDouble(false);

		BFSEnumeratedPathSet alg = new BFSEnumeratedPathSet(m, 10);
		alg.enumerateAndStoreAll();
		
		/* added for defletion buffer 
		SpinnetBuffer[] dBufs = new SpinnetBuffer[nbNode];	
		for (int i = 0 ; i < nbNode ; i++) {
			dBufs[i] = variant.getExampleBuffer(-i-1);
			dBufs[i].setSpinetBuilder(this);
			dests.add(dBufs[i]);
		}
		/* added for defletion buffer */

		NACK_HighRadixSwitch[] sw = new NACK_HighRadixSwitch[nbNode];
		for (int i = 0; i < nbNode; i++) {
			// create switch
			sw[i] = new NACK_HighRadixSwitch(i, nbNode,
					this.variant.getSwitchingTime(),
					this.linkDistMod.getMaxLinkLatency(),
					0);
			dests.add(sw[i]);
			//dBufs[i].setTrafficDestination(sw[i]);
		}

		for (int i = 0; i < nbNode; i++) {
			// add successors to the switch
			//sw[i].addSuccessor(i, links[1][i]); // connect to my own receiver (assuming there is only one receiver)
			sw[i].addSuccessor(i, recs[i]);	// connect to my own receiver (assuming virtual output queue -- multiple PD, should not use NB-receiver)
			for (int j = 0; j < nbNode; j++) {
				if (m[i][j] > 0)
					sw[i].addSuccessor(j, sw[j]);
			}

			// connect generator to switch
			links[0][i].setTrafficDestination(sw[i]);

			// fill in routing table, including to myself
			sw[i].addRoutingEntry(i, i, 0);
			
			for (int j = 0; j < nbNode; j++) {
				if (i == j) continue;
				Set<Path> s = alg.getPaths(i,j);
				int count = 0;
				int shortestHops = 0;
				for (Path p : s){
					int nbHops = p.getNumberOfHops();
					if (count == 0) {
						shortestHops = nbHops;
					} else {
						if (nbHops > shortestHops) {
							break;
						}
					}
					count++;
					p.removeFirst();
					int nextHop = p.getFirst();
					sw[i].addRoutingEntry(j, nextHop, nbHops);
				}
			}
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
