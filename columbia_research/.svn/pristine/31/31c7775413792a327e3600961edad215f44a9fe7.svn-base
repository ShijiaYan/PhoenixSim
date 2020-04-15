package edu.columbia.ke.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
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

public class FlattenedButterflyBuilder extends AbstractSpinetBuilder {
	
	AbstractLinkDistanceModel linkDistMod;
	SpinnetVariant variant;
	private int nbSwitch;
	private int nbClientPerSw;
		
	public int getNbClientPerSw() {
		return nbClientPerSw;
	}

	public FlattenedButterflyBuilder(
		@ParamName(name="FB switches", default_="4") int nbSwitch,
		@ParamName(name="clients per switches", default_="4") int nbClientPerSw,
		@ParamName(name="SPInet variant") SpinnetVariant variant,
		@ParamName(name="Interconnection fiber length model") AbstractLinkDistanceModel linkDistMod,
		@ParamName(name="Send traffic to self?") boolean trafToSelf,
		Rate refBW) {
		super(linkDistMod, trafToSelf, refBW);
		this.nbSwitch = nbSwitch;
		this.nbClientPerSw = nbClientPerSw;
		this.variant = variant;
		this.linkDistMod = linkDistMod;
	}
	
	public Map<String, String> getAllParameters() {
		int clients = getNumberOfClients();
		Map<String, String> map = SimpleMap.getMap(	"clients", clients+"", 
													"Sending trafc to self?", trafToSelf+"");
		map.put("FB switches", this.nbSwitch+"");
		map.put("clients per switches", this.nbClientPerSw+"");
		map.putAll(linkDistMod.getAllParameters());
		map.putAll(variant.getAllParameters());
		return map;
	}	
	
	public int getNumberOfClients() {
		return this.nbClientPerSw * this.nbSwitch;
	}
	
	public int getNumberofNodes(){
		return this.getNumberOfClients()+this.nbSwitch;
	}
	
	public int getMaxNumberOf2by2SwitchStages() {
		return 2;
	}
	
	protected int[] getClientsMapping(LWSIMExperiment lwSimExperiment) {
		return AbstractExperimentBlock.getIntArray(0,getNumberOfClients()-1);		
	}
	
	protected AbstractTrafficGenerator[] gens;
	
	@Override
	public InitFeedback buildSpinet(LWSIMExperiment lwSimExperiment, ArrayList<LWSimComponent> dests) {
		int nbClients = getNumberOfClients();
		
		gens = new AbstractTrafficGenerator[nbClients];
		SpinnetBuffer[] bufs = new SpinnetBuffer[nbClients];	
		Receiver[] recs = new Receiver[nbClients];
		TransmissionLink[][] links = new TransmissionLink[2][nbClients];	
		
		for (int i = 0 ; i < links[0].length ; i++) {
			for (int j = 0 ; j < 2; j++) {
				links[j][i] = new TransmissionLink(linkDistMod.getLinkLatency(i,nbClients));
				dests.add(links[j][i]);
			}
		}

		for (int i = 0 ; i < nbClients ; i++) {
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1,i);
			List<Integer> destsIndexes = MoreCollections.subsetOfN(0, nbClients -1);
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
		for (int i = 0 ; i < nbClients ; i++) {
			gens[i].setTrafficDestination(bufs[mapping[i]]);
			bufs[i].setTrafficDestination(links[0][i]);
			links[1][i].setTrafficDestination(recs[i]);
		}
		
		
		// build topology (single path)
		/*FlattenedButterfly fbGen = new FlattenedButterfly(2, this.nbSwitch);
		AbstractGraphHandler graph = Javanco.getDefaultGraphHandler(false);
		fbGen.generate(graph);
		boolean[][] m = graph.getEditedLayer().getIncidenceMatrix(false);*/
		
		int nbNodes = this.getNumberofNodes();
		boolean[][] m = new boolean[nbNodes][nbNodes];;
		generateFBMatrix(m);

		ShortestPathAlgorithm alg = new ShortestPathAlgorithm(m, true);
		alg.computeAll();
		int[][] n = alg.getSuccessors();

		NACK_HighRadixSwitch[] sw = new NACK_HighRadixSwitch[this.nbSwitch];
		for (int i = 0; i < nbSwitch; i++) {
			// create switch 
			// id = i + nbClients
			sw[i] = new NACK_HighRadixSwitch(i+nbClients, nbClients,
					this.variant.getSwitchingTime(),
					this.linkDistMod.getMaxLinkLatency(), 0);
			dests.add(sw[i]);
		}

		for (int i = 0; i < nbSwitch; i++) {
			int mySwID = i + nbClients;
			
			// connect to my own clients
			for (int j = 0; j < this.nbClientPerSw; j++){
				int cltID = i * nbClientPerSw + j;
				sw[i].addSuccessor(cltID, links[1][cltID] /*recs[cltID]*/); 
			}
			
			// connect to adjacent switches (all to all)
			for (int j = 0; j < this.nbSwitch; j++) {
				if (!(i==j))	
				sw[i].addSuccessor(nbClients + j, sw[j]);
			}

			// connect generator to switch
			for (int j = 0; j < this.nbClientPerSw; j++){
				links[0][i * nbClientPerSw + j].setTrafficDestination(sw[i]);
			}

			// fill in routing table, do not route to myself in this case
			for (int j = 0; j < nbClients; j++) {
				sw[i].addRoutingEntry(j, n[mySwID][j], 0);
			}
		}
		// build topology (single path) end
		
		return null;
	}

	private void generateFBMatrix(boolean[][] m) {
		int n = this.getNumberofNodes();
		int nbClients = this.getNumberOfClients();
		
		// connect sw and clients
		for (int i = 0; i < this.nbSwitch; i++){
			int swID = nbClients + i;
			for (int j = 0; j < this.nbClientPerSw; j++){
				int cltID = i* this.nbClientPerSw + j;
				m[swID][cltID] = true;
				m[cltID][swID] = true;
			}
		}
		
		// connect switches (all to all)
		for (int i = nbClients; i < n; i++ ){
			for (int j = nbClients; j < n; j++ ){
				if (i==j) 
					continue;
				else {
					m[i][j] = true;
					m[j][i] = true;
				}
					
			}
		}
		
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
