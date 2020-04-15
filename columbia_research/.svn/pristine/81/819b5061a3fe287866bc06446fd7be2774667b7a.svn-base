package edu.columbia.sebastien.green_optical;

import java.util.Map;

public abstract class AbstractAmplifierModel {

	public abstract double getPowerConsumption(double gainIndB, double outputLevelIndBm);

	public Map<String, String> getAllProperties() {
		Map<String, String> map = getAmplifierProperties();
		map.put("Amplifier model", this.getClass().getSimpleName());
		return map;
	}

	protected abstract Map<String, String> getAmplifierProperties();
}
