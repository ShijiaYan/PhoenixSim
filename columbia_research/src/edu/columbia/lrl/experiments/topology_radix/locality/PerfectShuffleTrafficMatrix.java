package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.math.MoreMaths;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class PerfectShuffleTrafficMatrix extends AbstractTrafficMatrix {

	public PerfectShuffleTrafficMatrix(double normLoad) {
		super(normLoad);
	}
	
	public PerfectShuffleTrafficMatrix() {	}
	
	private int bits;
	private int mask;
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, gs);
		bits = (int)Math.ceil(MoreMaths.log2(clients));
		mask = (int)Math.pow(2, bits-1);
	}	

	@Override
	public double getTraffic(int src, int dest) {
		boolean addS = (src & mask) > 0;
		boolean addD = (dest & mask) > 0;
		int src2 = src << 1;
		int dest2 = dest << 1;
		if (addS) {
			src2 = src2 - mask - mask + 1;
		}
		if (addD) {
			dest2 = dest2 - mask - mask + 1;
		}
		if (src2 == dest || dest2 == src)
			return normLoad/2;
		else
			return 0;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

}
