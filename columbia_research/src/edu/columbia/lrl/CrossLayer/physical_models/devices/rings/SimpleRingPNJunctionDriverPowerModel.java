//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class SimpleRingPNJunctionDriverPowerModel extends AbstractRingPNJunctionDriverPowerModel {

	private double baseConsumption;
	private double scalingPart;

	public SimpleRingPNJunctionDriverPowerModel(
			@ParamName(name = "offset (mW)", default_ = "0.45") double baseConsumption,
			@ParamName(name = "dependence (mW/(gb/s)^2)", default_ = "0.01") double scalingPart) {
		this.baseConsumption = baseConsumption;
		this.scalingPart = scalingPart;
	}

	public double getAverageConsumption(double dum1, double dum2, AbstractLinkFormat linkFormat) {
		return this.baseConsumption + Math.pow(linkFormat.getWavelengthRate() / 1.0E9D, 2.0D) * this.scalingPart;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Base consumption", String.valueOf(this.baseConsumption));
		map.put("scalingPart", String.valueOf(this.scalingPart));
		return map;
	}

	public double getEnergyPJperBit(double voltage, double capacitance, AbstractLinkFormat linkFormat) {
		double energyPJperBit = this.getAverageConsumption(voltage, capacitance, linkFormat)
				/ (linkFormat.getWavelengthRate() / 1.0E9D);
		return energyPJperBit;
	}
}
