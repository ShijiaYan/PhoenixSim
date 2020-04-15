package edu.columbia.lrl.experiments.spinet.link_model;

import java.util.Map;


public abstract class AbstractLinkDistanceModel {
	
	private double multiplier = 0.6666; // default 
	
	public abstract double getLinkLatency(int index, int maxIndex);
	public abstract double getMaxLinkLatency();
	public abstract Map<String, String> getAllParameters();
	
	public double getSpeedOfLightMultiplier() {
		return multiplier;
	}
	public void setSpeedOfLightMultiplier(double m) {
		multiplier = m;
	}
	public double getLatencyFromMeters(double meters) {
		return meters/(0.3*multiplier);
	}

}
