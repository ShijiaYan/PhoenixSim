package edu.columbia.lrl.experiments.topology_radix.locality;

import java.util.Map;

public abstract class AbstractNormalisedTrafficVectorGenerator {

	public abstract double[] getVector(int index, int length);

	public abstract Map<String, String> getTrafficVectorGeneratorsParameters();
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getTrafficVectorGeneratorsParameters();
		m.put("traffic vector generator", this.getClass().getSimpleName());
		return m;
	}

	public abstract void setMaxLoad(double relativeLoad);
	
}
