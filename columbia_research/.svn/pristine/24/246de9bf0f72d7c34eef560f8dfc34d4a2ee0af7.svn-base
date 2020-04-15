package edu.columbia.lrl.experiments.topology_radix.locality;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class UniformTrafficMatrix extends AbstractTrafficMatrix {
	
	private double cellTrafficDouble;
	
	public UniformTrafficMatrix(double normLoad) {
		super(normLoad);
	}
	
	public UniformTrafficMatrix() {
		super(1);
	}	
	
	@Override
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, null);
		cellTrafficDouble = normLoad/(double)(clients-1);
	}	

	@Override
	public double getTraffic(int src, int dest) {
		if (src == dest)
			return 0;
		else
			return cellTrafficDouble;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

	@Override
	public double getTrafficFrom(int src, int startDest, int range) {
		// is src in range?
		boolean inRange = false;
		if (startDest <= src) {
			if (startDest + range - 1 >= src) {
				inRange = true;
			}
		} else {
			if ((startDest + range - 1) - clients >= src) {
				inRange = true;
			}
		}
		if (inRange) {
			return (normLoad/(double)(clients-1))*(range-1);
		} else {
			return (normLoad/(double)(clients-1))*range;
		}
	}
	
	public static void main(String[] args) {
		UniformTrafficMatrix mat = new UniformTrafficMatrix(1.0);
		mat.init(101, null);
		for (int i = 0 ; i < 101 ; i++) {
			System.out.println(mat.getTraffic(i, 5, 5, 5));
		}
	}

}
