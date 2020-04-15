package edu.columbia.sebastien.green_optical.switches;

import java.util.Map;

import ch.epfl.general_libraries.results.PropertyMap;
import edu.columbia.sebastien.green_optical.AbstractOpticalSwitchModel;

public class CambridgeOFC2015Switch extends AbstractOpticalSwitchModel {

	@Override
	public double getPowerPenalty(int radix, double rate, int nbWavelengths) {
		if (radix <= 8)
			return 0.8;
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getSwitchConsumption(int radix) {
		if (radix <= 8) {
			return 1260;
		} 
		return Double.POSITIVE_INFINITY;
	}

	@Override
	protected Map<String, String> getSwitchProperties() {
		return new PropertyMap();
	}

}
