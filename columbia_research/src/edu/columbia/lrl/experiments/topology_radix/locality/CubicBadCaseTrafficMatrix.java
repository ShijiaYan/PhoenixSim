package edu.columbia.lrl.experiments.topology_radix.locality;

import ch.epfl.general_libraries.utils.MultiDigitCounter;
import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class CubicBadCaseTrafficMatrix extends AbstractTrafficMatrix {

	int root;
	int dist;
	
	
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, null);
		this.root = (int)Math.round(Math.pow(clients, 1.0/3d));
		this.dist = root/2;
	}	
	
	
	@Override
	public double getTraffic(int src, int dest) {
		int[] coords = MultiDigitCounter.getCoords(src, root, 3);
		int x = coords[0];
		coords[1] = (coords[1] + dist) % root;
		coords[0] = coords[2];
		coords[2] = x;
		if (MultiDigitCounter.getDecimalFromCoords(coords, root) == dest) {
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
