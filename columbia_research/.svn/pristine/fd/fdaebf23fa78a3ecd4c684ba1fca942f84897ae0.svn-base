package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class GlobalStructBasedTrafficMatrix extends AbstractTrafficMatrix {

	private GlobalStructure gs;
	private double power;
	double[] totalDistances;
	
	private int mode;

	public GlobalStructBasedTrafficMatrix(double power, GlobalStructure gs) {
		this(0, power, gs);
	}
	
	public GlobalStructBasedTrafficMatrix(double normLoad, double power, GlobalStructure gs) {
		super(normLoad);
		super.init(gs.getTotalNumberOfNodes(), gs);
		totalDistances = new double[clients];
		this.gs = gs;
		this.power = power;
		for (int i = 0 ; i < clients ; i++) {
			double tot = 0;
			for (int j = 0 ; j < clients ; j++) {
				int hops = gs.getNumberOfHops(i, j);
				if (hops > 0)
					tot += Math.pow(hops, power);
			}
			totalDistances[i] = tot;
		}
		mode = 0;
	}
	
	public GlobalStructBasedTrafficMatrix(GlobalStructure gs) {	
		this(gs,0);
	}
	
	public GlobalStructBasedTrafficMatrix(GlobalStructure gs, double normLoad) {
		super(normLoad);
		super.init(gs.getTotalNumberOfNodes(), gs);
		totalDistances = new double[clients];
		this.gs = gs;
		for (int i = 0 ; i < clients ; i++) {
			double tot = 0;
			for (int j = 0 ; j < clients ; j++) {
				int hops = gs.getNumberOfHops(i, j);
				if (hops == 1)
					tot += Math.pow(hops, power);
			}
			totalDistances[i] = tot;
		}
		mode = 1;
	}	

	@Override
	public double getTraffic(int src, int dest) {
		if (src == dest) return 0;
		if (mode == 0)
			return normLoad*Math.pow(gs.getNumberOfHops(src, dest), power)/(double)totalDistances[src];
		if (gs.getNumberOfHops(src, dest) == 1) {
			return normLoad/(double)totalDistances[src];
		} else {
			return 0;
		}
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("traffic-type", gs.getGeneralTypeAndDimensions());
		return m;
	}

}
