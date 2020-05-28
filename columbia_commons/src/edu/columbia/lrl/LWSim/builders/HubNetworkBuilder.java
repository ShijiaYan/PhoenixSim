package edu.columbia.lrl.LWSim.builders;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.MoreCollections;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.ElecSwitch;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public class HubNetworkBuilder extends AbstractBandwidthSpecifiedNBClientBuilder {
	
	

	private boolean separatedTraffic;
	private boolean self;
	private int linkLength;
	private double switchLatency;
	private boolean prior;
	private double bufferLatency;

	public HubNetworkBuilder(@ParamName(name="Use separated traffic generators?", default_="false")boolean sep, 
							 @ParamName(name="Include self as traffic dest?", default_="true") boolean self, 
							 @ParamName(name="Link lengths", default_="200") int linkLength, 
							 @ParamName(name="Switch latency", default_="200") double switchLatency,
							 @ParamName(name="Buffer bootstrap latency", default_="0") double bufferLatency) {
		this.separatedTraffic = sep;
		this.self = self;
		this.linkLength = linkLength;
		this.switchLatency = switchLatency;
		this.bufferLatency = bufferLatency;
	}
	
	public HubNetworkBuilder(@ParamName(name="Use separated traffic generators?", default_="false")boolean sep, 
			 @ParamName(name="Include self as traffic dest?", default_="true")boolean self, 
			 @ParamName(name="Link lengths", default_="200") int linkLength, 
			 @ParamName(name="Switch latency", default_="200") double switchLatency,
			 @ParamName(name="Buffer bootstrap latency", default_="0") double bufferLatency,
			 @ParamName(name="Use priority buffer", default_="true") boolean prior) {
		this.separatedTraffic = sep;
		this.self = self;
		this.linkLength = linkLength;
		this.switchLatency = switchLatency;
		this.bufferLatency = bufferLatency;
		this.prior = prior;
}	

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		
		
		
		// note : dests is a collection in which we have to put all the objects we use
		// this collection is later used to initialise the objects


		// we create a structure for two sets of links (to and from the hub)
		TransmissionLink[][] links = new TransmissionLink[2][nbClients];
		// a structure for a set of receivers
		Receiver[] recs = new Receiver[nbClients];
		
		
		// create the switch
		ElecSwitch sw = null;
		
		sw = new ElecSwitch(nbClients, switchLatency, 1, prior, bufferLatency);
		dests.add(sw);
	
		Buffer[] bufs = new Buffer[nbClients];			

		for (int i = 0 ; i < nbClients ; i++) {
			// populate the buffers
			bufs[i] = new Buffer(100000, bufferLatency, i, 1/* this is the normalised output load of this buffer */, prior);
			dests.add(bufs[i]);	
			
			// create the links
			links[0][i] = new TransmissionLink(linkLength);
			dests.add(links[0][i]);
			links[1][i] = new TransmissionLink(linkLength);
			dests.add(links[1][i]);		
			
			
			// create the receivers
			recs[i] = this.getReceiver(i);
			dests.add(recs[i]);		
			
			// connect all these objects
			bufs[i].setTrafficDestination(links[0][i]);
			links[0][i].setTrafficDestination(sw);
			sw.addDestination(links[1][i], i);
			links[1][i].setTrafficDestination(recs[i]);			
		}
		
		if (!separatedTraffic) {

			// we create a structure for nbClients traffic generators, buffers
			AbstractTrafficGenerator[] gens = new AbstractTrafficGenerator[nbClients];
		
			for (int i = 0 ; i < nbClients ; i++) {
				// populate the generators
				gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
				// set the possible destination
				if (self) {
					gens[i].setPossibleDestinationIndexes__(MoreCollections.subsetOfN(0, nbClients -1));									
				} else {
					gens[i].setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), i);
				}
				dests.add(gens[i]);
				gens[i].setTrafficDestination(bufs[i]);
			}
		} else {			
			AbstractTrafficGenerator[][] gens = new AbstractTrafficGenerator[nbClients][nbClients];
			//	Rate perRouteRate;
			double coeff;
			if (self) {
				coeff = 1d/(double)nbClients;
			//	perRouteRate = lwSimExperiment.getLoadPerClient().divide(nbClients);
			} else {
				coeff = 1d/(double)(nbClients -1);
			//	perRouteRate = lwSimExperiment.getLoadPerClient().divide(nbClients-1);
			}			
			for (int i = 0 ; i < nbClients ; i++) {
				for (int j = 0 ; j < nbClients ; j++) {	
					// populate the generators
					gens[i][j] = lwSimExperiment.getTrafficGenerator().getCopy(coeff, i);
					// set the possible destination
					gens[i][j].setPossibleDestinationIndexes(new int[]{j});
					gens[i][j].setTrafficDestination(bufs[i]);
					
					if (self || i!=j) {
						dests.add(gens[i][j]);						
					}
				}
			}					
		}
		return null;
	}
	
	public double getZeroLoadLatency() {
		return switchLatency + bufferLatency;
	}
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		int[] flatClients = MoreArrays.range(0, builder.getNumberOfClients()-1);
		return new int[][]{flatClients};
	}	
	
	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}	

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = SimpleMap.getMap("separated traffic", this.separatedTraffic + "",
												 "include self as dest", this.self+"",
												 "switch latency", this.switchLatency + "",
												 "hub network link length", this.linkLength + "",
												 "Use priorities", prior + "");
		return m;
	}

	@Override
	public double getTotalInjectionBandwidthRatio() {
		int nbCli = builder.getNumberOfClients();
		return nbCli;
	}	
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}
}
