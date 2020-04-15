package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class DistanceProportionalTrafficMatrix extends AbstractTrafficMatrix {
	
	public double power;
	
	public DistanceProportionalTrafficMatrix(double normLoad, double power) {
		super(normLoad);
		this.power = power;
	}

	double[] totalDistances;
	
	
	@Override
	public void init(int clients, GlobalStructure gloStr) {
		super.init(clients, gloStr);
		totalDistances = new double[clients];
		for (int i = 0 ; i < clients ; i++) {
			double tot = 0;
			for (int j = 0 ; j < clients ; j++) {
				int hops = gloStr.getNumberOfHops(i, j);
				if (hops > 0)
					tot += Math.pow(hops, power);
			}
			totalDistances[i] = tot;
		}
	}		
	

	@Override
	public double getTraffic(int src, int dest) {
		if (src == dest) return 0;
		return normLoad*Math.pow(gs.getNumberOfHops(src, dest), power)/(double)totalDistances[src];
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

	@Override
	public double getTrafficFrom(int src, int startDest, int range) {
		double tot = 0;
		for (int i = startDest ; i < range + startDest % clients ; i++) {
			tot += getTraffic(src, i);
		}
		return tot;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("Power factor of distance", power+"");
		return m;
	}

}
