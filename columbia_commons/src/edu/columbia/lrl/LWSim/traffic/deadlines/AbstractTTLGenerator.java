package edu.columbia.lrl.LWSim.traffic.deadlines;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractTTLGenerator {
	
	double mean;
	int min = 0;
	
	public abstract int nextTTL();
	
	public abstract AbstractTTLGenerator getCopy();
	
	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		if (mean >= min)
			this.mean = mean;
		else
			throw new IllegalStateException("Mean < min!");
	}

	public Map<String, String> getAllParameters(){
		return SimpleMap.getMap("TTL generator type", this.getClass().getSimpleName());
	}
}
