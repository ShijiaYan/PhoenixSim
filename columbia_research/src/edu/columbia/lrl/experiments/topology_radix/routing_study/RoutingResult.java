package edu.columbia.lrl.experiments.topology_radix.routing_study;

import ch.epfl.general_libraries.math.MooreBound;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution.IntegerDistribution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.ParetoPoint;
import edu.columbia.lrl.experiments.topology_radix.moorestudy.MooreBoundExperiment;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure.MODE;

public class RoutingResult extends ParetoPoint {
	private int nodeMult;
	private long totalSegments;
	private double totalRawTraffic;
	private double totalRoutedTraffic;
	private int totalConnectionsInTopo;
	private int totalSwitchesInTopo;
	private int usedSwitches;
	private long requiredCapUnitsMin;
	private long requiredCapUnitsOne;
	private long requiredCapUnitsMax;
	private long requiredCapUnitsDim;
	private Double[][] perDim;
	private int reqRadixMin;
	private int reqRadixOne;
	private int reqRadixDim;
	private int reqRadixMax;
	private int[] topoRadix;
	private StatisticalDistribution<Double> dist;
	private double criticalLoad;

	private DataPoint dp;
	private double[] trafFrom;
	private double[] trafTo;
	private int[][] radixes;
	MooreBoundExperiment.LocStruct locStruct;
	MooreBoundExperiment.LocStruct locStructNotRound;	
	MooreBoundExperiment.LocStruct optimalNotRoundLocStruct;
	MooreBoundExperiment.LocStruct optimalRoundLocStruct;
	
	StatisticalDistribution<Integer> distances;
	
	private long requiredCapUnits;
	private int reqRadix;
	private MODE mode;
	private MODE pareto = null;
	
	public RoutingResult(DataPoint dp,
						 int nodeMult,
						 long seg, 
						 double totalRawTraffic,
						 double totalRoutedTraffic, 
						 int totalConnectionsInTopo,
						 int totalSwitches,
						 int usedSwitches,
						 long requiredCapUnitsMin,
						 long requiredCapUnitsOne,
						 long requiredCapUnitsMax,
						 long requiredCapUnitsDim,
						 Double[][] perDim,
						 int[] maxConnectLinks,						 
						 int[][] radixes,
						 int[] topoRadix,
						 StatisticalDistribution<Double> dist,
						 double criticalLoad,
						 double[] trafFrom,
						 double[] trafTo,
						 MooreBoundExperiment.LocStruct locStruct,
						 MooreBoundExperiment.LocStruct locStructNotRound,
						 MooreBoundExperiment.LocStruct optimalNotRoundLocStruct,
						 MooreBoundExperiment.LocStruct optimalRoundLocStruct,
						 StatisticalDistribution<Integer> distances) {
		this.dp = dp;
		this.nodeMult = nodeMult;
		this.totalSegments = seg;
		this.totalRawTraffic = totalRawTraffic;
		this.totalRoutedTraffic = totalRoutedTraffic;
		this.totalConnectionsInTopo = totalConnectionsInTopo;
		this.totalSwitchesInTopo = totalSwitches;
		this.usedSwitches = usedSwitches;
		this.requiredCapUnitsMin = requiredCapUnitsMin;
		this.requiredCapUnitsOne = requiredCapUnitsOne;
		this.requiredCapUnitsMax = requiredCapUnitsMax;		
		this.requiredCapUnitsDim = requiredCapUnitsDim;
		this.perDim = perDim;
		this.reqRadixMin = MoreArrays.max(radixes[0]) + 0;
		this.reqRadixOne = MoreArrays.max(radixes[1]);	
		for (int i = 0 ; i < perDim[2].length ; i++) {
			this.reqRadixDim += Math.ceil(perDim[2][i]) * topoRadix[i];
			this.reqRadixMax = (int)Math.max(Math.ceil(perDim[2][i]) * topoRadix[i], reqRadixMax);
		}
		int conLinks = MoreArrays.max(maxConnectLinks);
		this.reqRadixMax = reqRadixMax* perDim[2].length + conLinks;
		this.reqRadixDim += conLinks;
		this.topoRadix = topoRadix;
		this.dist = dist;
		this.criticalLoad = criticalLoad;
		
		if (trafFrom != null) {
			this.radixes = radixes;
		}
		
		this.trafFrom = trafFrom;
		this.trafTo = trafTo;
		this.locStruct = locStruct;
		this.locStructNotRound = locStructNotRound;
		this.optimalNotRoundLocStruct = optimalNotRoundLocStruct;
		this.optimalRoundLocStruct = optimalRoundLocStruct;
		
		this.distances = distances;
	}
	
	public void setMode(MODE mode) {
		this.mode = mode;
	}
	
	public void store(Execution execution) {
		int topRadixSum = MoreArrays.sum(topoRadix);
		int pRadixMult = 0;
		for (int i = 0 ; i < topoRadix.length ; i++) {
			pRadixMult += topoRadix[i]*Math.ceil(perDim[2][i]);
		}		
		
		DataPoint dp1 = dp.getDerivedDataPoint();
		if (dist != null)
			dist.storeDistribution("Link usages", execution, dp);

		if (mode == MODE.MIN) {
			requiredCapUnits = requiredCapUnitsMin;
			reqRadix = reqRadixMin;
			if (radixes != null) {
				IntegerDistribution id = new IntegerDistribution(radixes[0]);
				id.storeDistribution("RadixesMin", execution, dp);
			}
			dp1.addProperty("cap", "min");
		} else if (mode == MODE.ONE) {
			requiredCapUnits = requiredCapUnitsOne;
			reqRadix = reqRadixOne;
			if (radixes != null) {
				IntegerDistribution id = new IntegerDistribution(radixes[1]);
				id.storeDistribution("RadixesOne", execution, dp);
			}				
			dp1.addProperty("cap", "one");				
		} else if (mode == MODE.MAX) {
			requiredCapUnits = requiredCapUnitsMax;
			reqRadix = reqRadixMax;
			dp1.addProperty("cap", "max");
		} else if (mode == MODE.DIM) {
			requiredCapUnits = requiredCapUnitsDim;
			reqRadix = reqRadixDim;
			dp1.addProperty("cap", "dim");			
		} else {
			throw new IllegalStateException("Should not be there");
		}	
		dp1.addProperty("radix", getRadixCategory());
		DataPoint dp2 = dp1.getDerivedDataPoint();		

		if (trafFrom != null) {
			execution.addArrayResult("trafFrom", trafFrom, dp);
		}
		if (trafTo != null) {
			execution.addArrayResult("trafTo", trafTo, dp);
		}
		execution.addArrayResult("link per connection per dim", MoreArrays.todoubleArray(perDim[2]), dp);
		
		distances.storeDistribution("distances", execution, dp);

		if (locStruct != null) {
			double[] val = new double[]{
					getTotalRawTraffic(),
					optimalRoundLocStruct.trafficForOneNode*usedSwitches,
					optimalNotRoundLocStruct.trafficForOneNode*usedSwitches,
					locStruct.trafficForOneNode*usedSwitches,
					locStructNotRound.trafficForOneNode*usedSwitches,
					getTotalRoutedTraffic()
			};
			String[] desc = new String[]{
					"Raw",
					"ideal (round)",
					"ideal (unround)",
					"ideal (for radix - round)",
					"ideal (for radix - unround)",
					"practical"		
			};
			execution.addArrayResult("traffics analysis", val, desc, dp1);
			
			val = new double[]{
					Math.ceil(getTotalRawTraffic()),
					optimalRoundLocStruct.totalLinks,
					optimalNotRoundLocStruct.totalLinks,
					locStruct.totalLinks,
					locStructNotRound.totalLinks,
					getNumberOfUnitLinksRequired(),
			};
			execution.addArrayResult("required capacities analysis", val, desc, dp1);
			
			desc = new String[]{
					"Base",
					"Detour overhead",
					"Connectivity overhead",
					"% of detour in the overhead",
					"Total Overhead",
					"normalised overhead",
					"base + detour",
					"Grand total"
			};
			
			val = new double[]{
					getTotalRawTraffic(),
					getTopoDetourOverhead(),
					getTopoConnectivityOverhead(),
					getTopoDetourOverhead() / (getTopoDetourOverhead() + getTopoConnectivityOverhead()),
					getTopoDetourOverhead() + getTopoConnectivityOverhead(),
					(getTopoDetourOverhead() + getTopoConnectivityOverhead()) / getTotalRawTraffic(),
					getTotalRawTraffic() + getTopoDetourOverhead(),
					getTotalRawTraffic() + getTopoDetourOverhead() + getTopoConnectivityOverhead()
			};

			execution.addArrayResult("overhead analysis", val, desc, dp1);
			
			dp2.addResultProperty("ideal (with radix) average impact per src-dest with this radix", locStruct.trafficForOneNode / (double)(usedSwitches - 1));

			desc = new String[]{
					"practical required radix",
					"practical topological radix",
					"practical radix incl mult",
					"ideal radix round",
					"ideal radix unround",
					"ideal total radix round"
			};
			
			val = new double[]{
					getRequiredRadix(),
					topRadixSum,
					pRadixMult,
					optimalRoundLocStruct.radix,
					optimalNotRoundLocStruct.radix,
					optimalRoundLocStruct.radix * optimalRoundLocStruct.linkMult + nodeMult
					
			};
			
			execution.addArrayResult("radixes analysis", val, desc, dp1);
			
			desc = new String[]{
					"effect of integral links",
					"effect of not-ideal radix",
					"effect of not-ideal topo",
					"distance from bound",
					"detour overhead - ideal(round)",
					"normalised detour - ideal",
					"normalised distance from bound",
					"normalised not-ideal radix effect",
					"normalised not-ideal topo effect",
					
					
			};
			
			val = new double[]{
					optimalRoundLocStruct.totalLinks - Math.ceil(getTotalRawTraffic()),
					locStruct.totalLinks - optimalRoundLocStruct.totalLinks,
					getNumberOfUnitLinksRequired() - locStruct.totalLinks,
					getNumberOfUnitLinksRequired() - optimalRoundLocStruct.totalLinks,
					getTotalRawTraffic() + getTopoDetourOverhead() - optimalRoundLocStruct.totalLinks,
					(getTotalRawTraffic() + getTopoDetourOverhead() - optimalRoundLocStruct.totalLinks)/optimalRoundLocStruct.totalLinks,
					(getNumberOfUnitLinksRequired() - optimalRoundLocStruct.totalLinks)/optimalRoundLocStruct.totalLinks,
					(locStruct.totalLinks - optimalRoundLocStruct.totalLinks)/optimalRoundLocStruct.totalLinks,
					(getNumberOfUnitLinksRequired() - locStruct.totalLinks)/optimalRoundLocStruct.totalLinks
			};
			
			execution.addArrayResult("bound analysis", val, desc, dp1);
			
			
			dp2.addResultProperty("ideal diameter not round", locStructNotRound.diameter);
			dp2.addResultProperty("ideal diameter round", locStruct.diameter);
		}
		
		dp2.addResultProperty("total links (=cap)", getNumberOfUnitLinksRequired());
		dp2.addResultProperty("critical load", criticalLoad);
		
		dp2.addResultProperty("required radix", getRequiredRadix());
		dp2.addResultProperty("average path length in topo", getAveragePathLength());
		dp2.addResultProperty("average hops of traffic", getAverageHopsOfTraffic());			
		dp2.addResultProperty("average impact per src-dest", getSrcDestImpact());
		dp2.addResultProperty("traffic for one node", getTotalRoutedTraffic()/(double)usedSwitches);
		
		dp2.addResultProperty("total routed traffic", getTotalRoutedTraffic());
		dp2.addResultProperty("total raw traffic", getTotalRawTraffic());
		if (dist != null) {
			dp2.addResultProperty("average traffic per link (observed)", getObservedLinkAverageTraffic());
		}
		dp2.addProperty("pareto opt", pareto+"");
		dp2.addResultProperty("average traffic per link (predicted)", getPredictedLinkAverageTraffic());		
		dp2.addResultProperty("total switches", getTotalSwitches());
		dp2.addResultProperty("used switches", usedSwitches);
		dp2.addResultProperty("total connections", getTotalConnectionsInTopo());
		dp2.addResultProperty("per endpoint links", getLinksPerEndpoint());
		dp2.addResultProperty("per endpoint connections", getConnectionsPerEndpoint());
		dp2.addResultProperty("per connection switches", getSwitchesPerConnection());
		dp2.addResultProperty("links per connection", getLinksPerConnection() );
		dp2.addResultProperty("utilization", getUtilization());
//		dp2.addResultProperty("traffic connectivity overhead", getTrafConnectivityOverhead());
//		dp2.addResultProperty("traffic detour overhead", getTrafDetourOverhead());
//		dp2.addResultProperty("traffic total overhead", getTrafConnectivityOverhead() + getTrafDetourOverhead());			
//		dp2.addResultProperty("topo connectivity overhead", getTopoConnectivityOverhead());
//		dp2.addResultProperty("topo detour overhead", getTopoDetourOverhead());
//		dp2.addResultProperty("topo total overhead", getTopoConnectivityOverhead() + getTopoDetourOverhead());
		dp2.addResultProperty("norm topo total overhead", (getTopoConnectivityOverhead() + getTopoDetourOverhead())/getTotalRawTraffic());
		dp2.addResultProperty("norm topo detour overhead", getTopoDetourOverhead()/getTotalRawTraffic());
		dp2.addResultProperty("norm topo connectivity overhead", getTopoConnectivityOverhead()/getTotalRawTraffic());
		dp2.addResultProperty("topological radix", topRadixSum);
		dp2.addResultProperty("corresponding moore bound", MooreBound.getMinimalDistance(topRadixSum, getTotalSwitches()));
		dp2.addResultProperty("% away from the lower bound", (getAveragePathLength() / MooreBound.getMinimalDistance(topRadixSum, usedSwitches) - 1)*100d);
		
		execution.addDataPoint(dp2);
	}
	
/*	private double getTrafDetourOverhead() {
		return getTotalRawTraffic() * (getAverageHopsOfTraffic() - 1);
	}

	private double getTrafConnectivityOverhead() {
		return getNumberOfUnitLinksRequired() - getTotalRawTraffic()*getAverageHopsOfTraffic();
	}*/

	private double getAverageHopsOfTraffic() {
		return totalRoutedTraffic/totalRawTraffic;
	}

	private double getTopoDetourOverhead() {
		return getTotalRawTraffic() * (getAveragePathLength() - 1);
	}

	private double getTopoConnectivityOverhead() {
		return getNumberOfUnitLinksRequired() - getTotalRawTraffic()*getAveragePathLength();
	}
	
	

	private static double[] RADICES = new double[]{8,16,24,32,48,64,80,96,112,128,144,160,176,192,208,224,240,256, 512, 1024};
	
	private double getRadixCategory() {
        for (double radice : RADICES) {
            if (reqRadix <= radice) {
                return radice;
            }
        }
		return reqRadix;
	}

	public double getLinksPerConnection() {
		return (double)requiredCapUnits/(double)totalConnectionsInTopo;
	}

	public double getSwitchesPerConnection() {
		return (double)totalSwitchesInTopo/(double)totalConnectionsInTopo;
	}

	public double getConnectionsPerEndpoint() {
		return (double)totalConnectionsInTopo/(double)(usedSwitches*nodeMult);
	}

	public double getLinksPerEndpoint() {
		return (double)requiredCapUnits/(double)(usedSwitches*nodeMult);
	}

	public int getNodeMultiplicity() {
		return nodeMult;
	}
	
	public int getTotalConnectionsInTopo() {
		return totalConnectionsInTopo;
	}
	
	public int getTotalSwitches() {
		return totalSwitchesInTopo;
	}
	
	public double getUtilization() {
		return getTotalRoutedTraffic()/(double)getNumberOfUnitLinksRequired();
	}
	
	public double getObservedLinkAverageTraffic() {
		return dist.getMean();
	}

	public double getPredictedLinkAverageTraffic() {
		return getTotalRoutedTraffic()/(double)(totalConnectionsInTopo*2);
	}

	public double getSrcDestImpact() {
		return getTotalRoutedTraffic() / ((double)usedSwitches*(double)(usedSwitches-1));
	}
	
	public double getAveragePathLength() {
		return (double)this.getTotalSegments()/((double)usedSwitches*(double)(usedSwitches-1));
	}
	
	public int getRequiredRadix() {
		return reqRadix;
	}
	
	public int getNumberOfUnitLinksRequired() {
		return (int)requiredCapUnits;
	}
	
	public long getTotalSegments() {
		return totalSegments;
	}
	
	public double getTotalRoutedTraffic() {
		return totalRoutedTraffic;
	}
	
	public double getTotalRawTraffic() {
		return totalRawTraffic;
	}

	@Override
	public double getValueOfDimensionN(int n) {
		switch(n) {
		case 0:	
			// radix
			if (mode == MODE.MIN)
				return reqRadixMin;
			else
				return reqRadixOne;
		case 1:
			// total links
			if (mode == MODE.MIN)
				return requiredCapUnitsMin;
			if (mode == MODE.ONE)
				return requiredCapUnitsOne;
			if (mode == MODE.DIM)
				return requiredCapUnitsDim;
			else
				return requiredCapUnitsMax;
		case 2:
			// total switches
			return totalSwitchesInTopo;
		case 3:
			// hops
			return getAverageHopsOfTraffic();
		default:
				throw new IllegalStateException();
		}
	}

	@Override
	public boolean isDimensionNtheHigherTheBetter(int n) {
		// all criteria are to be minimized
		return false;
	}

	@Override
	public int getDimensions() {
		return 4;
	}

	public void setPareto(MODE m) {
		this.pareto = m;
	}
}