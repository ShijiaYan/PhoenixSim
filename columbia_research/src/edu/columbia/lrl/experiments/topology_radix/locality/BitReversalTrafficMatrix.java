package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.math.MoreMaths;

public class BitReversalTrafficMatrix extends AbstractTrafficMatrix {

	public BitReversalTrafficMatrix(double normLoad) {
		super(normLoad);
		// TODO Auto-generated constructor stub
	}
	
	public BitReversalTrafficMatrix() {}
	

	@Override
	public double getTraffic(int src, int dest) {
		
		if (MoreMaths.reverse(src, clients) == dest)
			return normLoad;
		else
			return 0;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

}
