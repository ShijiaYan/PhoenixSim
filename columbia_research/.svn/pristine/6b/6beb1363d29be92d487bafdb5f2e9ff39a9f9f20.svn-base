package edu.columbia.lrl.experiments.spinet.link_model;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SymmetricLinearLinkDistanceModel extends LinearLinkDistanceModel {
	
	public SymmetricLinearLinkDistanceModel(@ParamName(name="Min fiber lengths in meter") double minLength, 
											@ParamName(name="Max fiber lengths in meter") double maxLength) {
		super(minLength, maxLength);
	}
	
	public double getLinkLatency(int index, int maxIndex) {
		int halfIndex = index/2;
		int halfMaxIndex = maxIndex/2;
		return getLatencyFromMeters(minLength) + ((double)halfIndex / (double)halfMaxIndex)*(getLatencyFromMeters(maxLength)-getLatencyFromMeters(minLength));
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Link distance model", "Symmetric linear distance",
								"Link min-max distance in m", minLength +"-"+ maxLength);
	}			
	
}
