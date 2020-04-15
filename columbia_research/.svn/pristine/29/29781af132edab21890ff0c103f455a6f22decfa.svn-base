package edu.columbia.lrl.experiments.spinet.link_model;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class LinearLinkDistanceModel extends AbstractLinkDistanceModel {
	
	protected double minLength;
	protected double maxLength;
	
	public LinearLinkDistanceModel(@ParamName(name="Min fiber lengths in meter") double minLength, 
								   @ParamName(name="Max fiber lengths in meter") double maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public double getLinkLatency(int index, int maxIndex) {
		return getLatencyFromMeters(minLength) + ((double)index / (double)maxIndex)*(getLatencyFromMeters(maxLength)-getLatencyFromMeters(minLength));
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Link distance model", "Linear distance",
								"Link min-max distance in m", minLength +"-"+ maxLength);
	}

	@Override
	public double getMaxLinkLatency() {
		return getLatencyFromMeters(maxLength);
	}	

}
