package edu.columbia.sebastien.green_optical;

import java.util.Map;

public abstract class AbstractOpticalSwitchModel {

	
	public abstract double getPowerPenalty(int radix, double rate, int nbWavelengths);
	public abstract double getSwitchConsumption(int radix);

	public Map<String, String> getAllProperties() {
		Map<String, String> map = getSwitchProperties();
		map.put("Switch model", this.getClass().getSimpleName());
		return map;
	}
	
	protected abstract Map<String, String> getSwitchProperties();
}
