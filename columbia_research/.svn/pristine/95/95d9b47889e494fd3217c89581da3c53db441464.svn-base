package edu.columbia.ke.builder;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.SmallElecSwitch;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public class FullyMeshedNetworkBuilder extends
		AbstractNumberOfClientConfigurableBuilder {
	
	private int genMode;
	private int SmallElecSw_process_delay;
	private boolean prior = false;

	public FullyMeshedNetworkBuilder(@ParamName(name="Traffic Generator Mode (Dedicated?)") int trafficGenMode) {
		this.genMode = trafficGenMode;
	}

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {

		// note : dests is a collection in which we have to put all the objects we use
		// this collection is later used to initialise the objects

		// we create a structure for nbClients traffic generators, buffers
		AbstractTrafficGenerator[] gens_per_node;
		AbstractTrafficGenerator[][] gens_per_pair;
		
		//if (this.genMode == 0){
			gens_per_node = new AbstractTrafficGenerator[nbClients];
		//} else {
			gens_per_pair = new AbstractTrafficGenerator[nbClients][nbClients];
		//}
		
		Buffer[] bufs_per_node = new Buffer[nbClients];	
		Buffer[][] bufs_per_pair = new Buffer[nbClients][nbClients];	
		
		// we create a structure for two sets of links (to and from the hub)
		TransmissionLink[][] links = new TransmissionLink[nbClients][nbClients];
		// a structure for a set of receivers
		Receiver[] recs = new Receiver[nbClients];
		
		SmallElecSwitch[] sesw = new SmallElecSwitch[nbClients];
		SmallElecSw_process_delay = 1;
			
		for (int i = 0 ; i < nbClients ; i++) {	
			// create the links
			for (int j = 0; j < nbClients; j++) {
				links[i][j] = new TransmissionLink(5);
				dests.add(links[i][j]);
			}
			
			// create the receivers
			recs[i] = this.getReceiver(i);
			dests.add(recs[i]);
			
		}

		if (this.genMode == 0) {
			for (int i = 0 ; i < nbClients ; i++) {
				// populate the generators
				gens_per_node[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
				// set the possible destination
				gens_per_node[i].setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), i);
				dests.add(gens_per_node[i]);
				
				// populate the buffers
				bufs_per_node[i] = new Buffer(100000, 10, i, 1/* this is the normalised output load of this buffer */, prior);
				dests.add(bufs_per_node[i]);
				
				// create the small elec sw's
				sesw[i] = new SmallElecSwitch(SmallElecSw_process_delay, nbClients);
				dests.add(sesw[i]);
				
				gens_per_node[i].setTrafficDestination(bufs_per_node[i]);
				bufs_per_node[i].setTrafficDestination(sesw[i]);
				
				for (int j = 0; j < nbClients; j++){
					sesw[i].addDestination(links[i][j], j);
					links[i][j].setTrafficDestination(recs[j]);
				}
			}
		} else {
			for (int i = 0 ; i < nbClients ; i++) {
				for (int j = 0; j < nbClients; j++) {
					// populate the generators
					gens_per_pair[i][j] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
					// set the possible destination
					int[] outputDest = {j};
					gens_per_pair[i][j].setPossibleDestinationIndexes(outputDest);
					
					// populate the buffers
					bufs_per_pair[i][j] = new Buffer(100000, 10, i, 1/* this is the normalised output load of this buffer */, prior);
					
					if (i!=j){
						dests.add(gens_per_pair[i][j]);
						dests.add(bufs_per_pair[i][j]);
						gens_per_pair[i][j].setTrafficDestination(bufs_per_pair[i][j]);
						bufs_per_pair[i][j].setTrafficDestination(links[i][j]);
						links[i][j].setTrafficDestination(recs[j]);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = SimpleMap.getMap();
		m.put("Traffic Generator Mode (Dedicated?)",  genMode+"");
		return m;
	}

	@Override
	public int getMaxPacketSizeInBits() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
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


