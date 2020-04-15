package edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers;

import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.LWSim.application.ActionManager;

public class TopoAwarePoolManager extends FlatClientPoolManager {
	
	public TopoAwarePoolManager(boolean shuffle) {
		super(shuffle);
	}

	/////////////////////////////////// PLACE THIS IN A PARAMETER OBJECT
	@Override
	protected int[] getSortedArrayOfTrials(ActionManager action, int fromAnode, boolean includeZero) {
		if (fromAnode >= 0) {
			int[][] nei = action.getExperiment().getTopologyBuilder().getNeighborhood(fromAnode);
			int[] concat = new int[0];
			for (int i = 0 ; i < nei.length ; i++) {
				int[] part;
				if (shuffle)
					part = action.getExperimentStream().shuffle(nei[i]);
				else
					part = nei[i];
				concat = MoreArrays.concat(concat, part);
			}
			return concat;
		} else {
			return super.getSortedArrayOfTrials(action, fromAnode, includeZero);
		}	
	}	
	
	
	
}
