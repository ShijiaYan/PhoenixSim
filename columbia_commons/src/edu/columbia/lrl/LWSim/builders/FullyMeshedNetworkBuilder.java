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
import edu.columbia.lrl.LWSim.TrafficTransitPoint;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.SmallElecSwitch;
import edu.columbia.lrl.LWSim.components.TransmissionLink;

public class FullyMeshedNetworkBuilder extends AbstractBandwidthSpecifiedNBClientBuilder {
	
	public static enum TYPE {
		LINK_BW,
		CLIENT_BW
	}
	
	boolean separatedTraffic;
	boolean usePriorities;
	boolean selfTraf;
	boolean useBuffers;
	TYPE type;
	int linkLatency;

	public FullyMeshedNetworkBuilder(
			@ParamName(name="Use separated traffic generators?", default_="false") boolean separatedTraffic, 
			@ParamName(name="Include self as traffic dest?", default_="true") boolean selfTraf, 
			@ParamName(name="Use priorities?", default_="false") boolean usePriorities,
			@ParamName(name="Use reference bandwidth as") TYPE type,
			@ParamName(name="Link latency in ns") int linkLatency ) {
		this.separatedTraffic = separatedTraffic;
		this.usePriorities = usePriorities;
		this.selfTraf = selfTraf;
		this.type = type;
		this.linkLatency = linkLatency;
		this.useBuffers = true;
	}
	
	public FullyMeshedNetworkBuilder(
			@ParamName(name="Use separated traffic generators?", default_="false") boolean separatedTraffic, 
			@ParamName(name="Include self as traffic dest?", default_="true") boolean selfTraf, 
			@ParamName(name="Use priorities?", default_="false") boolean usePriorities,
			@ParamName(name="Use buffer (false=no queuing)?", default_="true") boolean useBuffers,
			@ParamName(name="Use reference bandwidth as") TYPE type,
			@ParamName(name="Link latency in ns") int linkLatency ) {
		this.separatedTraffic = separatedTraffic;
		this.usePriorities = usePriorities;
		this.selfTraf = selfTraf;
		this.type = type;
		this.linkLatency = linkLatency;
		this.useBuffers = useBuffers;
	}	
	
	public TrafficTransitPoint getTrafficTransitPoint(int i, int j) {
		return new TransmissionLink(linkLatency);
	}

	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		
		Buffer[][] bufs = new Buffer[nbClients][nbClients];		
		TrafficTransitPoint[][] links = new TrafficTransitPoint[nbClients][nbClients];
		Receiver[] recs = new Receiver[nbClients];		
		
		for(int i = 0 ; i < nbClients ; i++) {	
			recs[i] = this.getReceiver(i);
			dests.add(recs[i]);
			

			for (int j = 0 ; j < nbClients ; j++) {
				if (selfTraf || i!=j) {
					if (useBuffers) {
						if (type == TYPE.LINK_BW) {
							bufs[i][j] = new Buffer(10000, 0, i+(nbClients*j), 1, usePriorities);
						} else {
							if (selfTraf)
								bufs[i][j] = new Buffer(10000, 0, i+(nbClients*j), 1d/(double)nbClients, usePriorities);
							else
								bufs[i][j] = new Buffer(10000, 0, i+(nbClients*j), 1d/(double)(nbClients-1), usePriorities);
						}
						dests.add(bufs[i][j]);
					}
					links[i][j] = getTrafficTransitPoint(i, j);
					dests.add(links[i][j]);
				}
			}
		}
		
		if (this.separatedTraffic) {
			AbstractTrafficGenerator[][] gens = new AbstractTrafficGenerator[nbClients][nbClients];	
		//	Rate perRouteRate;
			double coeff;
			if (selfTraf) {
				coeff = 1d/(double)nbClients;
			//	perRouteRate = lwSimExperiment.getLoadPerClient().divide(nbClients);
			} else {
				coeff = 1d/(double)(nbClients -1);
			//	perRouteRate = lwSimExperiment.getLoadPerClient().divide(nbClients-1);
			}
			for(int i = 0 ; i < nbClients ; i++) {	
				for (int j = 0 ; j < nbClients ; j++) {
					gens[i][j] = lwSimExperiment.getTrafficGenerator().getCopy(coeff, i);
					gens[i][j].setPossibleDestinationIndexes(new int[]{j});
					if (useBuffers) {
						gens[i][j].setTrafficDestination(bufs[i][j]);	
					} else {
						gens[i][j].setTrafficDestination(links[i][j]);
					}
					if (selfTraf || i != j) {
						dests.add(gens[i][j]);	
					}
				}
			}
		} else {
			SmallElecSwitch[] sws = new SmallElecSwitch[nbClients];
			
			AbstractTrafficGenerator[] gens = new AbstractTrafficGenerator[nbClients];
			for(int i = 0 ; i < nbClients ; i++) {
				sws[i] = new SmallElecSwitch(0, i);
				dests.add(sws[i]);
				gens[i] = lwSimExperiment.getTrafficGenerator().getCopy(1, i);
				dests.add(gens[i]);
				if (selfTraf) {
					gens[i].setPossibleDestinationIndexes__(MoreCollections.subsetOfN(0, nbClients -1));
				} else {
					gens[i].setPossibleDestinationIndexesExcludingOne(MoreCollections.subsetOfN(0, nbClients -1), i);					
				}
				gens[i].setTrafficDestination(sws[i]);
				for (int j = 0 ; j < nbClients ; j++) {	
					if (useBuffers) {
						sws[i].addDestination(bufs[i][j], j);
					} else {
						sws[i].addDestination(links[i][j], j);
					}
				}
			}
		}
		
		for(int i = 0 ; i < nbClients ; i++) {
			for (int j = 0 ; j < nbClients ; j++) {
				if (selfTraf || i != j) {	
					if (useBuffers) {
						bufs[i][j].setTrafficDestination(links[i][j]);
					}
					links[i][j].setTrafficDestination(recs[j]);						
				}	
			}
		}
		return null;
	}
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		int[] flatClients = MoreArrays.range(0, builder.getNumberOfClients()-1);
		return new int[][]{flatClients};
	}	

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("separated traffic", this.separatedTraffic + "",
				"include self as dest", this.selfTraf+"");
	}
	
	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public double getTotalInjectionBandwidthRatio() {
		int nbCli = builder.getNumberOfClients();
		if (type == TYPE.LINK_BW) {
			if (selfTraf) {
				return nbCli * nbCli;
			} else {
				return nbCli * (nbCli - 1);			
			}
		} else {
			if (selfTraf) {
				return nbCli;
			} else {
				return nbCli - 1;			
			}			
		}
	}		
		
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}

	public boolean usesInputQueues() {
		return useBuffers;
	}
}
