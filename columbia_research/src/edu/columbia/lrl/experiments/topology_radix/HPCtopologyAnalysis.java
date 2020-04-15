package edu.columbia.lrl.experiments.topology_radix;

import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractNormalisedTrafficVectorGenerator;
import edu.columbia.lrl.experiments.topology_radix.topogen.AnalysableTopologyGenerator;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.ParetoPoint;


public abstract class HPCtopologyAnalysis implements Experiment {
	
	protected AnalysableTopologyGenerator atopogen;
	protected AbstractNormalisedTrafficVectorGenerator localityVec;
	protected int radix;
	protected HPCSolutionFilter filter;
	
	public HPCtopologyAnalysis(AnalysableTopologyGenerator atopogen, AbstractNormalisedTrafficVectorGenerator localityVec, int radix,
			HPCSolutionFilter filter) {
		this.atopogen = atopogen;
		this.localityVec = localityVec;
		this.radix = radix;
		this.filter = filter;
	}

	public static class HPCDesignPoint extends ParetoPoint {
		
		int radix;
		int nbSwitches;
		int clients;
		double maxSupportedLoad;
		int nbLinks;
		int M;
		int m;
		double perNodeLoad;
		double averageTopologicalHops;
		String definingString;
		Map<String, String> params;
		
		double clientPerSwitch;
		double linksPerClient;
		
		public HPCDesignPoint(int radix, 
							  int nbSwitches, 
							  int clients, 
							  double maxSupportedLoad, 
							  int nbLinks, 
							  int m, 
							  int M, 
							  double alpha, 
							  double averageTopologicalHops, 
							  String definingString,
							  Map<String, String> params) {
			this.radix = radix;
			this.nbSwitches = nbSwitches;
			this.clients = clients;
			this.maxSupportedLoad = maxSupportedLoad;
			this.nbLinks = nbLinks;
			this.M = M;
			this.m = m;	
			this.perNodeLoad = alpha;
			this.averageTopologicalHops = averageTopologicalHops;
			this.definingString = definingString;
			this.params = params;
			this.clientPerSwitch = (double)clients/(double)nbSwitches;
			this.linksPerClient = (double)nbLinks/(double)clients;
		}

		public DataPoint addProperties(DataPoint derivedDataPoint) {
			derivedDataPoint.addProperties(params);
			derivedDataPoint.addProperty("Radix", radix);
			derivedDataPoint.addResultProperty("Nb switches", nbSwitches);
			derivedDataPoint.addProperty("Supported clients", clients);
			derivedDataPoint.addResultProperty("Max supported load", maxSupportedLoad);
			derivedDataPoint.addResultProperty("Nb links", nbLinks);
			derivedDataPoint.addProperty("Node multiplicity", M);			
			derivedDataPoint.addProperty("Link multiplity", m);
			derivedDataPoint.addResultProperty("Average hops", perNodeLoad);
			derivedDataPoint.addResultProperty("Average topological hops", averageTopologicalHops);
			derivedDataPoint.addResultProperty("Clients per switch", clientPerSwitch);
			derivedDataPoint.addResultProperty("Links per clients", linksPerClient);
			derivedDataPoint.addProperty("description", definingString);
			
			return derivedDataPoint;
		}

		@Override
		public double getValueOfDimensionN(int n) {
			switch (n) {
			case 0:
				// nb:links
				return nbLinks;
			case 1:
				// nb:switches
				return nbSwitches;
			case 2:
				// max supported load
				return maxSupportedLoad;
			case 3:
				// average hops
				return perNodeLoad;	
			}
			throw new IllegalStateException();
		}
		
		private static boolean[] dirs = new boolean[]{false, false, true, false};

		@Override
		public boolean isDimensionNtheHigherTheBetter(int n) {
			return dirs[n];
		}

		@Override
		public int getDimensions() {
			return 4;
		}
	}

}
