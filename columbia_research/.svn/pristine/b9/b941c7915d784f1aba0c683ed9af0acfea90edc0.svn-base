package edu.columbia.lrl.experiments.topology_radix;

import edu.columbia.lrl.experiments.topology_radix.HPCtopologyAnalysis.HPCDesignPoint;

public class HPCSolutionFilter {

	private int minClients;
	private int maxClients;
	private double minSupportedLoad;
	
	public HPCSolutionFilter() {
		this.minClients = 0;
		this.maxClients = Integer.MAX_VALUE;
		this.minSupportedLoad = 0;
	}
	
	public HPCSolutionFilter(int minClients, int maxClients, double minSupportedLoad) {
		this.minClients = minClients;
		this.maxClients = maxClients;
		this.minSupportedLoad = minSupportedLoad;
	}
	
	public boolean filter(HPCDesignPoint point) {
		return point.clients > minClients && point.clients < maxClients && point.maxSupportedLoad >= minSupportedLoad;
	}
}
