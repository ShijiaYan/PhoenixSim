package edu.columbia.sebastien.green_optical;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class MEMSbasedSwitch extends AbstractOpticalSwitchModel {
	
	private double offsetPP;
	private double maxPorts;
	private double consumption;

	public MEMSbasedSwitch(@ParamName(name="Power penalty for low radix (dB)", default_="3") double offsetPP,
						   @ParamName(name="Max port at low power penalty", default_="512") double maxPorts,
						   @ParamName(name="Power consumption per port in mW", default_="10") double consumption) {
		this.offsetPP = offsetPP;
		this.maxPorts = maxPorts;
		this.consumption = consumption;
	}
	
	@Override
	public double getPowerPenalty(int radix, double rate, int wavelengths) {
		if (radix <= maxPorts) {
			return offsetPP;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

	@Override
	public double getSwitchConsumption(int radix) {
		return consumption*radix;
	}

	@Override
	protected Map<String, String> getSwitchProperties() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("Power penalty for low radix", offsetPP+"");
		m.put("Max nb power at low PP", maxPorts+"");
		m.put("Power consumption per port", consumption+"");
		return m;
	}

}
