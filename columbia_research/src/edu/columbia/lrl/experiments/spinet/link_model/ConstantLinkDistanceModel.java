package edu.columbia.lrl.experiments.spinet.link_model;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ConstantLinkDistanceModel extends AbstractLinkDistanceModel {
	
	private double lengthInMeter;
	
	public ConstantLinkDistanceModel(@ParamName(name="Fiber lengths in meter", default_="3") double lengthInMeter) {
		this.lengthInMeter = lengthInMeter;
	}	
	
	public ConstantLinkDistanceModel(@ParamName(name="Fiber lengths in meter") double lengthInMeter, 
									 @ParamName(name="Speed of light fraction") double multiplier) {
		this.lengthInMeter = lengthInMeter;
		setSpeedOfLightMultiplier(multiplier);
	}
	
	
	
	public double getLinkLatency(int index, int maxIndex) {
		return getLatencyFromMeters(lengthInMeter);
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Link distance model", "Constant distance",
								"Link distance in m", lengthInMeter +"");
	}

	@Override
	public double getMaxLinkLatency() {
		return getLatencyFromMeters(lengthInMeter);
	}		
	
}
