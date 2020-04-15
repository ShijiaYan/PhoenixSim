package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.math.MoreMaths;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class TornadoTrafficMatrix extends AbstractTrafficMatrix {

	public TornadoTrafficMatrix(double normLoad) {
		super(normLoad);
	}
	
	public TornadoTrafficMatrix() {	}	
	
	private int x;
	private int y;
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, gs);
		int[] fact = MoreMaths.factorize(clients, 2);
		if (MoreMaths.product(fact) != clients)
			throw new IllegalStateException("Traffic matrix size (" + clients + ") can't be factorized");
		x = fact[0];
		y = fact[1];
	}

	@Override
	public double getTraffic(int src, int dest) {
		int srcX = src / x;
		int srcY = src % x;
		
		int destX = (srcX + (x/2-1)) % x;
		int destY = (srcY + (x/2-1)) % y;
		if (destX*x + destY == dest) {
			return normLoad;
		} else {
			return 0;
		}
		
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

}
