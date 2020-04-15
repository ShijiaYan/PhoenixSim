package edu.columbia.sebastien.green_optical.switches;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.green_optical.AbstractOpticalSwitchModel;

public class SophisticatedMEMSbasedSwitch extends AbstractOpticalSwitchModel {
	
	private double powerConsumptionInMw;
	private double basePowerPenalty;
	private double perPortPPIncrease;
	
	public SophisticatedMEMSbasedSwitch(
			@ParamName(name="Power consumption per port", default_="10") double powerConsumptionInMw,
			@ParamName(name="Base power penalty (dB)", default_="3") double basePowerPenalty,
			@ParamName(name="Extra PP per port (dB)", default_="10") double perPortPPincrease) {
		this.powerConsumptionInMw = powerConsumptionInMw;
		this.basePowerPenalty = basePowerPenalty;
		this.perPortPPIncrease = perPortPPincrease;
	}

	@Override
	public double getPowerPenalty(int radix, double rate, int nbWavelengths) {
		return basePowerPenalty + (radix * perPortPPIncrease);
	}

	@Override
	public double getSwitchConsumption(int radix) {
		return powerConsumptionInMw*radix;
	}

	@Override
	protected Map<String, String> getSwitchProperties() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("Base power penalty", basePowerPenalty+"");
		m.put("Per port PP increase", perPortPPIncrease+"");
		m.put("Power consumption per port", powerConsumptionInMw+"");
		return m;
	}

}
