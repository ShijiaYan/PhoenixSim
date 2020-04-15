//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import java.util.Map;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class RateDependentRingPNJunctionDepletionDriverPowerModel extends AbstractRingPNJunctionDriverPowerModel {

	public RateDependentRingPNJunctionDepletionDriverPowerModel() {
	}

	public double getAverageConsumption(double voltage, double C, AbstractLinkFormat linkFormat) {
		double rateInGbs = linkFormat.getWavelengthRate() / 1.0E9D;
		double singleDriverPowerConsumption = C * 0.001D * rateInGbs * Math.pow(voltage, 2.0D) / 4.0D;
		return singleDriverPowerConsumption * (double) linkFormat.getNumberOfChannels();
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		return map;
	}

	public double getEnergyPJperBit(double voltage, double capacitance, AbstractLinkFormat linkFormat) {
		double energyPJperBit = this.getAverageConsumption(voltage, capacitance, linkFormat)
				/ (linkFormat.getWavelengthRate() / 1.0E9D);
		energyPJperBit *= 1.0E9D;
		return energyPJperBit;
	}
}
