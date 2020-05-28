package edu.columbia.lrl.experiments.topology_radix.locality;


import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.experiments.topology_radix.moorestudy.LocalityModel;
import edu.columbia.lrl.experiments.topology_radix.moorestudy.MooreBoundExperiment;
import edu.columbia.lrl.experiments.topology_radix.moorestudy.MooreBoundExperiment.LocStruct;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class LocalityModelBasedTrafficMatrix extends AbstractTrafficMatrix {

	double[][] traffic;
	

	int mult;
	private LocalityModel locMod;
	private int mode;
	private boolean withLocal;
	
	public LocalityModelBasedTrafficMatrix(LocalityModel locmod, 
			@ParamName(name="Mode", default_="-1") int mode, 
			@ParamName(name="Use local traffic", default_="false") boolean withLocal) {
		this.locMod = locmod;
		this.mode = mode;
		this.withLocal = withLocal;
	}
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, gs);		
		int size = gs.getUsedNodes();		
		this.mult = gs.getMultiplicity();
		int maxDist = 0;
		short[][] distances = new short[size][size];
		traffic = new double[size][size];
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
				int dist = gs.getNumberOfHops(i, j);
				if (dist > 322767) throw new IllegalStateException("Does not support larger dists");
				distances[i][j] = (short)dist;
				maxDist = Math.max(maxDist, dist);
			}
		}
		int[][] distOccurences = new int[size][maxDist+1];
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size; j++) {
				distOccurences[i][distances[i][j]]++;
			}
		}
		if (mode <= 0) {
			for (int i = 0 ; i < size ; i++) {
				int var = withLocal ? 0 : -1;
				// some nodes might have their "valence shells" not filled, so we need to identify
				// the largest index of distOccurences that is positive
				int reach = 0;
				int[] distsForI = distOccurences[i];
				for (int j = 0 ; j < distsForI.length ; j++) {
					if (distsForI[j] > 0) {
						reach = j+1;
					}
				}
				double[] rep = locMod.calculate(reach + var);
				for (int j = 0 ; j < size ; j++) {
					if (!withLocal && i == j) continue;
					double traf = 1.0;
					int dist = distances[i][j];
					traf *= rep[dist+var];
					if (mode == 0) {
						traf /= distsForI[dist];
					}
					traffic[i][j] = traf;
				}
				if (mode == -1) {
					traffic[i] = MoreArrays.normalize(traffic[i]);
				}
				MoreArrays.productInSameArray(traffic[i], normLoad);
			}
		}
	}	
	
	@Override
	public double getTraffic(int src, int dest) {
		int s = src / mult;
		int d = dest / mult;
		return traffic[s][d]/ (double)mult;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad*mult;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("locality", locMod.getLocality()+"");
		m.put("locality mode", mode+"");
		m.put("with local", withLocal+"");
		return m;
	}

	public LocStruct getLocStruct(int usedSwitches, int sum, int connections) {
		return new MooreBoundExperiment(usedSwitches, 
				mult,
				 sum, 
				 normLoad,
				 locMod, 
				 mode, 
				 true, 
				 withLocal).calculateWithSpecificLinks(connections);
	}
	
	public LocStruct getLocStructNotRound(int usedSwitches, int sum, int connections) {
		return new MooreBoundExperiment(usedSwitches, 
				mult,
				 sum, 
				 normLoad,
				 locMod, 
				 mode, 
				 false, 
				 withLocal).calculateWithSpecificLinks(connections);
	}	
	
	public LocStruct getOptimalNotRoundLocStruct(int usedSwitched) {
		return new MooreBoundExperiment(locMod, usedSwitched, mult, normLoad, mode, false, withLocal).optimizeRadix();
	}
	
	public LocStruct getOptimalRoundLocStruct(int usedSwitched) {
		return new MooreBoundExperiment(locMod, usedSwitched, mult, normLoad, mode, true, withLocal).optimizeRadix();
	}	

}
