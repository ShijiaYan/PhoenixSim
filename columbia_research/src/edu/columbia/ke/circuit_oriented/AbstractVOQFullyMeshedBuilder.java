package edu.columbia.ke.circuit_oriented;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.graphics.timeline.TimeLineGUI;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.ke.circuit_oriented.in_and_out_limited.ReuseAwareReceiver;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractBandwidthSpecifiedNBClientBuilder;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public abstract class AbstractVOQFullyMeshedBuilder extends
	AbstractBandwidthSpecifiedNBClientBuilder {

	protected double circuitSetupLatency;
	private boolean self;
	protected AbstractCircuitLimitedVOQ voqTemplate;
	protected int circuitPerNode;
	protected double maxVacantTime;
	
	protected boolean sdTimeline;
	protected boolean srcTimeline;

	protected AbstractVOQFullyMeshedBuilder(
			AbstractCircuitLimitedVOQ voqTemplate,
			int circuitPerNode,
			double circuitSetupLatency, 
			double maxVacantTime, 
			boolean self,
			boolean sdTimeline,
			boolean srcTimeline) {
		this.circuitPerNode = circuitPerNode;
		this.voqTemplate = voqTemplate;
		this.circuitSetupLatency = circuitSetupLatency;
		this.self = self;
		this.maxVacantTime = maxVacantTime;
		
		this.sdTimeline = sdTimeline;
		this.srcTimeline = srcTimeline;
		
		voqTemplate.setMaxVacantTime(maxVacantTime);
		voqTemplate.setCircuitSetupLatency(circuitSetupLatency);
		
		// default
	}
	
	protected int nClient;
	protected AbstractCircuitLimitedVOQ[] voq;
	protected Receiver[] recs;
	
	protected ArrayList<LWSimComponent> dests;

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		this.nClient = nbClients;
		this.dests = dests;
		
		// note : dests is a collection in which we have to put all the objects we use
		// this collection is later used to initialise the objects

		// we create a structure for nbClients traffic generators, buffers
		
		buildVoqArray();	
		
		// we create a structure for two sets of links (to and from the hub)
		TransmissionLink[][] links = new TransmissionLink[nbClients][nbClients];
		// a structure for a set of receivers
		recs = new Receiver[nbClients];
		
		for (int i = 0 ; i < nbClients ; i++) {	
			// create the links
			for (int j = 0; j < nbClients; j++) {
				links[i][j] = new TransmissionLink(5);
				dests.add(links[i][j]);
			}
			
			// create the receivers
			recs[i] = this.getReceiver(i, voqTemplate.getAssociatedReceiver(nbClients, circuitPerNode));
			dests.add(recs[i]);
			if (recs[i] instanceof ReuseAwareReceiver) {
				((ReuseAwareReceiver)recs[i]).setVOQ(voq);
			}
			
		}
		
		buildVOQs(dests);
		
		connectVoqToLinks(links);
		
		for (int i = 0; i < nbClients; i++){
			for (int j = 0; j < nbClients; j++) {
				links[i][j].setTrafficDestination(recs[j]);
			}
		}
		
		/*
		 * build traffic gen (new)
		 * using TraceTrafficGenClonable
		 */
		AbstractTrafficGenerator[] gens = new AbstractTrafficGenerator[nbClients];
		for (int i = 0; i < nbClients; i++) {
			// populate the generators
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
			// set the possible destination
			if (self) {
				gens[i].setPossibleDestinationIndexes__(MoreCollections
						.subsetOfN(0, nbClients - 1));
			} else {
				gens[i].setPossibleDestinationIndexesExcludingOne(
						MoreCollections.subsetOfN(0, nbClients - 1), i);
			}
			dests.add(gens[i]);
			connectGen2Voq(gens[i], i);
		}
		//if (traceFlag)
		//	dests.add(lwSimExperiment.getTrafficGenerator());
		return null;
	}
	
	protected void buildVoqArray() {
		voq = new AbstractCircuitLimitedVOQ[this.nClient];	
	}
	
	protected void connectVoqToLinks(TransmissionLink[][] links) {
		for (int i = 0; i < this.nClient; i++){
			for (int j = 0; j < this.nClient; j++) {
				voq[i].bufs.get(j).setTrafficDestination(links[i][j]);
			}
		}
	}
	
	protected void connectGen2Voq(AbstractTrafficGenerator gen, int src) {
		gen.setTrafficDestination(voq[src]);
	}
	
	protected void buildVOQs(ArrayList<LWSimComponent> dests){
		int nDestPerVoq = nClient;
		
		// prepare destinations
		ArrayList<Integer> dList = new ArrayList<>();
		for (int i = 0 ; i < nClient; i++){
			dList.add(i);
		}
		
		// populate the VOQs
		for (int i = 0; i < nClient; i++){
			instantiateVoq(i, i, nDestPerVoq, circuitPerNode);
			dests.add(voq[i]);
			
			// add buffers to voq
			voq[i].addDestinations(dList, dests);
		}
	}
	
	protected void instantiateVoq(int i, int parentID, int nDestPerVoq, int circuitPerVoq){	
		voq[i] = voqTemplate.getCopy(parentID, nDestPerVoq, circuitPerVoq);
		voq[i].setTimeline(sdTimeline, srcTimeline);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = SimpleMap.getMap();
		m.put("Circuit setup latency",  circuitSetupLatency+"");
		m.put("VOQ type", voqTemplate.getClass().getSimpleName());
		m.put("Circuits Per Node", this.circuitPerNode+"");
		m.put("Max vacant time", maxVacantTime+"");
		m.putAll(voqTemplate.getAllParameters());
		return m;
	}

	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int[][] getNeighborhood(int fromAnode) {
		return null;
	}

	@Override
	public String toString() {
		return voqTemplate.getClass().getSimpleName() + "_" + circuitPerNode;
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		return nClient * circuitPerNode;
	}	
	
	@Override	
	public void notifyEnd(double clock, int status) {
		// transmitting timeline to lwSimExp	
		if (srcTimeline) {
			TimeLineSet set = new TimeLineSet();			
			for (AbstractCircuitLimitedVOQ v : this.voq) {
				set.addAll(v.srcTimelines);
			}
			new TimeLineGUI(set);
		}
	}
}


