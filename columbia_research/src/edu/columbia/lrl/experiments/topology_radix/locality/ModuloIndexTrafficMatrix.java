package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

public class ModuloIndexTrafficMatrix extends AbstractTrafficMatrix {
	
	private int dist;
	private boolean repeat;
	
	public ModuloIndexTrafficMatrix(int dist) {
		this.dist = dist;
		repeat = false;
	}	
	
	public ModuloIndexTrafficMatrix(double load, int dist) {
		super(load);
		this.dist = dist;
		repeat = false;
	}
	
	public ModuloIndexTrafficMatrix(double load, int dist, boolean repeat) {
		super(load);
		this.dist = dist;
		this.repeat = repeat;
	}
	
	public ModuloIndexTrafficMatrix(int dist, boolean repeat) {
		this.dist = dist;
		this.repeat = repeat;
	}	

	@Override
	public double getTraffic(int src, int dest) {
		if (repeat) {
			if ((src - dist + clients) % clients % dist == 0) {
				return normLoad;
			} else {
				return 0;
			}		
		} else {
			if ((src + dist) % clients == dest) {
				return normLoad;
			} else {
				return 0;
			}
		}
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("repeat_mod_traf", repeat+"");
		m.put("modulo", dist +"");
		return m;
	}	

}
