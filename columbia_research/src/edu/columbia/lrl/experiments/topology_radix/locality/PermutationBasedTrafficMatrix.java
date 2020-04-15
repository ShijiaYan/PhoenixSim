package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.utils.TypeParser;

public class PermutationBasedTrafficMatrix extends
		AbstractTrafficMatrix {
	
	private int[] array;
	
	public PermutationBasedTrafficMatrix(String permut) {
		array = TypeParser.parseIntArray(permut);
	}

	@Override
	public double getTraffic(int src, int dest) {
		if (dest == array[src]) return normLoad;
		return 0;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

}
