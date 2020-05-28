package edu.columbia.lrl.experiments.topology_radix.routing_study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.ParetoSet;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.routing_study.RoutingResult;
import edu.columbia.lrl.experiments.topology_radix.routing_study.structures.AbstractAxisStructure;

public class AdvancedRoutingExperiment extends AbstractRoutingExperiment {
	
	private Iterable<String> types;
	private int targetNodes;
	private int maxNodesInOneDimension;
	private int maxMultiplicity;
	private boolean compact;
	private boolean paretoOnly;
	
//	private ArrayList<RoutingResult> results = new ArrayList<RoutingResult>();
	
	public AdvancedRoutingExperiment(@ParamName(name="Endpoints") int targetNodes, 
									 @ParamName(name="Max nodes in one dimensions") int maxNodesInOneDimension, 
									 @ParamName(name="Max multiplicity") int maxMultiplicity,
									 @ParamName(name="Compact nodes") boolean compact,
									 @ParamName(name="Pareto only") boolean paretoOnly,
									 @ParamName(name="With extra data?", default_="false") boolean extraData,			 
									 AbstractTrafficMatrix trafMat,
									 @ParamName(name="Traffic load") double load,
									 GlobalStructure.MODE[] modes,
									 String ... types) {
		super(trafMat, load,modes, extraData);
		this.types = Arrays.asList(types);
		this.targetNodes = targetNodes;
		this.maxNodesInOneDimension = maxNodesInOneDimension;
		this.maxMultiplicity = maxMultiplicity;
		this.compact = compact;
		this.paretoOnly = paretoOnly;
	}
	
	public AdvancedRoutingExperiment(@ParamName(name="Endpoints") int targetNodes, 
			 @ParamName(name="Max nodes in one dimensions") int maxNodesInOneDimension, 
			 @ParamName(name="Max multiplicity") int maxMultiplicity,
			 @ParamName(name="Compact nodes") boolean compact,
			 @ParamName(name="Pareto only") boolean paretoOnly,
			 @ParamName(name="With extra data?", default_="false") boolean extraData,			 
			 AbstractTrafficMatrix trafMat,
			 @ParamName(name="Traffic load") double load,	
			 GlobalStructure.MODE[] modes,
			 TypeEnumerator types) {
		super(trafMat, load, modes, extraData);
		this.types = types;
		this.targetNodes = targetNodes;
		this.maxNodesInOneDimension = maxNodesInOneDimension;
		this.maxMultiplicity = maxMultiplicity;
		this.compact = compact;		
		this.paretoOnly = paretoOnly;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		
	//	ArrayList<RoutingResult> results = new ArrayList<RoutingResult>();
		
		ArrayList<ParetoSet<RoutingResult>> paretoSetList = new ArrayList<>();
		for (int i = 0 ; i < modes.length ; i++) {
			paretoSetList.add(new ParetoSet<>(4));
		}
		
		ArrayList<RoutingResult> results = new ArrayList<>();
		
		Execution ex = new Execution();		
		
		for (String ty : types) {
			for (GlobalStructure gs : getPossibleGlobalStructures(ty)) {
				trafMat.init(targetNodes, gs);
				if(gs.getMinDimSize() < 3) continue;
				System.out.println(gs.getUsedNodes() + " : " + Arrays.toString(gs.getSizes()) + " = " + MoreMaths.product(gs.getSizes()));
	
				RoutingResult rr = gs.routeTraffic(trafMat, withDetails);
				if (paretoOnly) {
					for (int i = 0 ; i < modes.length ; i++) {
						rr.setMode(modes[i]);
						paretoSetList.get(i).addCandidate(rr);
					}
				} /*else {
					results.add(e)
					for (GlobalStructure.MODE m : modes) {
						rr.setMode(m);
						rr.store(ex);
					}
				}*/
				results.add(rr);
			}
		}
		
		if (paretoOnly) {
			for (int i = 0 ; i < modes.length ; i++) {
				for (RoutingResult rr : paretoSetList.get(i)) {
					rr.setPareto(modes[i]);
				}
			}
		}
        for (GlobalStructure.MODE mode : modes) {
            for (RoutingResult rr : results) {
                rr.setMode(mode);
                rr.store(ex);
            }
        }
			
		man.addExecution(ex);
	}
	
	private int[] getDimensionSizesForMultAndImposed(Class[] types, int nodeMult) {
		int dimensions = types.length;
		int[] sizes = new int[dimensions];
		boolean[] definedFlag = new boolean[dimensions];
		int switches = MoreMaths.ceilDiv(targetNodes, nodeMult);
		// see the ideal factorization
		int[] factors = MoreMaths.factorize(switches, dimensions);
		
		int defined = 0;
		int definedFactor = 1;		
		for (int i = 0 ; i < dimensions ; i++) {
			@SuppressWarnings("unchecked")
			int[] imp = MoreArrays.toIntArray(AbstractAxisStructure.getImposedSizes(types[i]));
			if (imp.length > 0) {
				int index = MoreArrays.findSmallerLargerThan(MoreArrays.mean(factors), imp);
				if (index >= 0) {
					sizes[i] = imp[index];
					defined++;
					definedFactor *= sizes[i];
					definedFlag[i] = true;
				} else {
					return null;
				}
			} else {
				definedFlag[i] = false;
			}
		}	
		int switchesReducedByImp = MoreMaths.ceilDiv(switches, definedFactor);
		factors = MoreMaths.factorize(switchesReducedByImp, dimensions - defined);
		int index = 0;
		for (int j = 0 ; j < sizes.length ; j++) {
			if (definedFlag[j] == false) {
				sizes[j] = factors[index];
				index++;
			}
		}
		return sizes;
	}
	
	private ArrayList<GlobalStructure> getPossibleGlobalStructures(String type) {
		int dimensions = type.length();
		Class[] types = new Class[dimensions];
		for (int i = 0 ; i < dimensions ; i++) {
			types[i] = AbstractAxisStructure.getCorrespondingAxis(type.charAt(i));
		}
		int nodeMult = 0;
		HashSet<String> done = new HashSet<>();
		ArrayList<GlobalStructure> gStrList = new ArrayList<>();
		while (nodeMult < maxMultiplicity) {
			nodeMult++;
			int[] sizes = getDimensionSizesForMultAndImposed(types, nodeMult);
			if (sizes == null) continue;
			if (MoreMaths.product(sizes) > targetNodes*10) break;
			if (MoreMaths.product(sizes)*nodeMult < targetNodes) continue;
			if (MoreArrays.max(sizes) > maxNodesInOneDimension) continue;
			int switches = MoreMaths.ceilDiv(targetNodes, nodeMult);			
			String signature = nodeMult + Arrays.toString(sizes);
			if (!done.contains(signature)) {
				done.add(signature);
				
				GlobalStructure gs = new GlobalStructure(MoreArrays.arrayCopy(sizes), types, switches, nodeMult, compact);
				
				gStrList.add(gs);
			}
		}		
		
		return gStrList;
	}
	
	
	
/*	public ArrayList<RoutingResult> getResults() {
		return results;
	}*/

}
