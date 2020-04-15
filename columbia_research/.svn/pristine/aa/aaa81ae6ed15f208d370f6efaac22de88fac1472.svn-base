package edu.columbia.lrl.experiments.lwsim_generic;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.components.Broadcaster;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.Selecter;

public class StarCouplerTopologyBuilder extends AbstractNumberOfClientConfigurableBuilder {
	
	private int nbClients;
	private double outputRate;
	private boolean usePriorities;

	public StarCouplerTopologyBuilder(double outputRate, boolean usePriorities) {
		this.outputRate = outputRate;
		this.usePriorities = usePriorities;
	}

	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		AbstractTrafficGenerator[] gens = new AbstractTrafficGenerator[nbClients];
		Buffer[] bufs = new Buffer[nbClients];
		Broadcaster[] broad = new Broadcaster[nbClients];
		Selecter[] filter = new Selecter[nbClients];
		Receiver[] rec = new Receiver[nbClients];
		Buffer[] output = new Buffer[nbClients];
		
		for (int i = 0 ; i < nbClients ; i++) {
			gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
			gens[i].setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), i);			
			dests.add(gens[i]);
			bufs[i] = new Buffer(10000, 2, i); // put this parameters somewhere else later
			dests.add(bufs[i]);
			broad[i] = new Broadcaster();
			dests.add(broad[i]);
			filter[i] = new Selecter(i);
			dests.add(filter[i]);
			output[i] = new Buffer(10000, 2, i, outputRate, usePriorities);
			dests.add(output[i]);
			rec[i] = this.getReceiver(i);
			dests.add(rec[i]);
		}
		
		for (int i = 0 ; i < nbClients ; i++) {
			gens[i].setTrafficDestination(bufs[i]);
			bufs[i].setTrafficDestination(broad[i]);
			for (int j = 0 ; j < nbClients ; j++) {
				broad[i].addDestination(filter[j]);
			}
			filter[i].setTrafficDestination(output[i]);
			output[i].setTrafficDestination(rec[i]);
		}
		
		return null;
	}
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		int[] flatClients = MoreArrays.range(0, this.getNumberOfClients()-1);
		return new int[][]{flatClients};
	}	
	
	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}	

	public int getNumberOfClients() {
		return nbClients;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Receiver rate", outputRate+"");
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		return nbClients;
	}		
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}
}
