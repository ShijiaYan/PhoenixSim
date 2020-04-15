package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Arrays;
import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;



public class UniformTrafficVector extends
		AbstractNormalisedTrafficVectorGenerator {
	
	private double relativeLoad;
	
	public UniformTrafficVector() {}

	@Override
	public double[] getVector(int index, int length) {
		double[] vec = new double[length];
		Arrays.fill(vec, relativeLoad/((double)length-1f));
		vec[index] = 0;
		return vec;
	}

	@Override
	public Map<String, String> getTrafficVectorGeneratorsParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public void setMaxLoad(double relativeLoad) {
		if (relativeLoad <= 0 || relativeLoad > 1) throw new IllegalStateException("Load is not relative (" + relativeLoad + ")");
		this.relativeLoad = relativeLoad;
		
	}



}
