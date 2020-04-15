package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class AdversarialTrafficVector extends
		AbstractNormalisedTrafficVectorGenerator {
	
	private double relativeLoad;

	@Override
	public double[] getVector(int index, int length) {
		double[] d = new double[length];
		d[length-1] = relativeLoad;
		return d;
	}

	@Override
	public Map<String, String> getTrafficVectorGeneratorsParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public void setMaxLoad(double relativeLoad) {
		this.relativeLoad = relativeLoad;

	}

}
