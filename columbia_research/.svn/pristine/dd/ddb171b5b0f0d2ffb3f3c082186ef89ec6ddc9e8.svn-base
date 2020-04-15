package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.ArrayList;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class StructIntenseTraffic extends AbstractTrafficMatrix {
	
	ArrayList<ArrayList<Integer>> dests;
	int mult;
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		dests = new ArrayList<ArrayList<Integer>>();
		super.init(clients, gs);
		this.mult = gs.getMultiplicity();
		int maxDist = 0;
		for (int i = 0 ; i < gs.getUsedNodes() ; i++) {
			for (int j = 0 ; j < gs.getUsedNodes() ; j++) {
				maxDist = Math.max(maxDist, gs.getNumberOfHops(i, j));
			}
		}
		for (int i = 0 ; i < gs.getUsedNodes() ; i++) {
			ArrayList<Integer> listI = new ArrayList<Integer>();
			for (int j = 0 ; j < gs.getUsedNodes() ; j++) {
				if (gs.getNumberOfHops(i, j) == maxDist) {
					listI.add(j);
				}
			}
			dests.add(listI);
		}
	}

	@Override
	public double getTraffic(int src, int dest) {
		int s = src / mult;
		int d = dest / mult;
		ArrayList<Integer> f = dests.get(s);
		if (f.contains(d)) {
			return normLoad/(double)(f.size()*mult);
		} else {
			return 0;
		}
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad*mult;
	}

}
