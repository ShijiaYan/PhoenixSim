package edu.columbia.lrl.experiments.topology_radix.locality;

public class AdversarialTrafficMatrixForFatTree extends AbstractTrafficMatrix {
	
	public AdversarialTrafficMatrixForFatTree(double load) {
		super(load);
	}

	@Override
	public double getTraffic(int src, int dest) {
		// trial by Seb on May 19th, 2016
		// dest is 0
		if (src < clients/2) {
			if (dest == clients - 1) {
				return normLoad; 
			} else {
				return 0;
			}
		} else {
			if (dest == 0) {
				return normLoad; 
			} else {
				return 0;
			}
		}
		
		

		//	return (dest == clients-src) ? normLoad : 0;

	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

/*	@Override
	public double getTrafficFrom(int src, int startDest, int range) {
		if (range == 0) return 0;
		if (src < clients/2) {
			return (startDest + range >= (clients - 1)) ? normLoad : 0;
		} else {
			return ((startDest + range) % clients) >= 0 ? normLoad : 0;
		}
	}*/

}
