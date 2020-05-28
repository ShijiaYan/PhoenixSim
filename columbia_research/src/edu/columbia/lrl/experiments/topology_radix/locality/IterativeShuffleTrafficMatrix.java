package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.routing_study.GlobalStructure;

public class IterativeShuffleTrafficMatrix extends AbstractTrafficMatrix {

	private int iterations;
	private int[] array;
	
	public IterativeShuffleTrafficMatrix(int iterations) {
		this.iterations = iterations;
	}
	
	public void init(int clients, GlobalStructure gs) {
		super.init(clients, null);
		array = new int[clients];
		for (int i = 0 ; i < clients ; i++) {
			array[i] = i;
		}
		for (int i = 0 ; i < iterations ; i++) {
			permute();
		}
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("shuffle_iterations",iterations+"");
		return m;
	}
	
	private void permute() {
		int[] newA = new int[array.length];
		for (int i = 0 ; i < Math.ceil((double)array.length/2d) ; i++) {
			newA[i*2] = array[i];
		}
		int j = 0;
		for (int i = (int)Math.ceil((double)array.length/2d) ; i < array.length ; i++) {
			newA[1+ j*2] = array[i];
			j++;
		}
		array = newA;
		
	}

	@Override
	public double getTraffic(int src, int dest) {
		if (dest == array[src]) {
			return normLoad;
		}
		return 0;
	}

	@Override
	public double getTrafficFrom(int src) {
		return normLoad;
	}

}
