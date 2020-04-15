package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.javanco.base.AbstractGraphHandler;
import javancox.topogen.AbstractTopologyGenerator;

public class TopologyBasedTrafficMatrix extends AbstractTrafficMatrix {
	
	private AbstractTopologyGenerator atg;
	
	private AbstractGraphHandler agh;
	
	private int[] degrees;
	
	public TopologyBasedTrafficMatrix(AbstractTopologyGenerator atg) {
		this.atg = atg;
		
		agh = atg.generate();
		
		degrees = new int[agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i < degrees.length ; i++) {
			degrees[i] = agh.getNodeContainer(i).getAllConnectedLinks().size();
		}
	}

	@Override
	public double getTraffic(int src, int dest) {
		if (src >= degrees.length || dest >= degrees.length) {
			return 0;
		}
		if (agh.getLinkContainer(src, dest, false) != null) {
			return 1d/(double)degrees[src];
		}
		return 0;
	}

	@Override
	public double getTrafficFrom(int src) {
		return agh.getNodeContainer(src).getAllConnectedLinks().size();
	}
	
	

}
