package edu.columbia.lrl.experiments.topology_radix.locality;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class IndexDistanceDecreasingTrafficMatrix extends AbstractTrafficMatrix {
	
	private double trafLoad;
	private boolean sym;
	private boolean cycle;
	
	public IndexDistanceDecreasingTrafficMatrix(boolean sym, boolean cycle) {
		this.sym = sym;
		this.cycle = cycle;	
	}
	
	public IndexDistanceDecreasingTrafficMatrix(double normLoad, boolean sym, boolean cycle) {
		super(normLoad);
		this.sym = sym;
		this.cycle = cycle;
	}
	
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, gs);
		trafLoad = normLoad/(double)(clients-1);
	}

	@Override
	public double getTraffic(int src, int dest) {
		if (src == dest) return 0;
		if (sym != false) {
			if (cycle) {
				int dist = (src - dest + clients) % clients;
				int invDist = clients - dist;
				double factor = (trafLoad*2)/clients;
			
				double total =invDist*factor;
			
				return total;
			} else {
				int dist;
				if (src < dest) {
					dist = clients;
				} else {
					dist = src - dest;
				}
				int invDist = clients - dist;
				double factor = (trafLoad)/clients;
			
				double total =invDist*factor;
			
				return total;				
			}
		} else {
			if (cycle) {
				int dist = Math.min(Math.min(Math.abs(src - dest), Math.abs(src - dest + clients)), Math.abs(src - dest - clients));
				int invDist = clients/2 - dist;
				double factor = trafLoad/clients;
				
				double total = invDist*factor;
				return total;
			} else {
				int dist = Math.abs(src - dest);
				int invDist = clients - dist;
				double factor = trafLoad/clients;
				
				double total = invDist*factor;
				return total;				
			}
		}
	}
	
	public float getTrafficFloat(int src, int dest) {
		return (float)getTraffic(src, dest);
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

	@Override
	public double getTrafficFrom(int src, int startDest, int range) {
		double accum = 0;
		for (int i = startDest ; i < startDest + range ; i++) {
			accum += getTraffic(src, i);
		}
		return accum;
	}
	
	public static void main(String[] args) {
		IndexDistanceDecreasingTrafficMatrix mat = new IndexDistanceDecreasingTrafficMatrix(1.0, false, false);
		mat.init(101, null);
		for (int i = 0 ; i < 101 ; i++) {
			System.out.println(mat.getTraffic(i, 5, 5, 5));
		}
	}	

}
