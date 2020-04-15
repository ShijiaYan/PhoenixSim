package edu.columbia.sebastien.green_optical;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultAmplifierModel extends AbstractAmplifierModel {
	
	private double maxGain;
	private double powerConsumption;
	
	/*
	 * 
	 * @param maxGain
	 * @param powerConsumption The default value of 100 mW can be analyzed this way. With 500 Gb/s
	 * aggregated bandwidth, one achieves 0.5 mW/Gb/s = 0.5 pJ/bit.
	 */
	public DefaultAmplifierModel(
			@ParamName(name="Max gain", default_="10") double maxGain, 
			@ParamName(name="Constant power consumption mW", default_="100") double powerConsumption) {
		this.maxGain = maxGain;
		this.powerConsumption = powerConsumption;
	}

	@Override
	public double getPowerConsumption(double gainIndB, double outputLevelIndBm) {
		if (gainIndB > maxGain) {
		//	throw new IllegalStateException("Too high gain required");
			return Double.NaN;
		} else {
			return powerConsumption;
		}
	}

	@Override
	protected Map<String, String> getAmplifierProperties() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("Max gain", maxGain+"");
		m.put("constant pow consumption", powerConsumption+"");
		return m;
	}

}
