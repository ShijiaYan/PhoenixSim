package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultTunableLaser extends AbstractTunableLaserModel {
	
	private double tuningTime;
	private double wpe;
	
	public DefaultTunableLaser(@ParamName(name="Laser tuning time (ns)", default_="200") double tuningTime,
			@ParamName(name="Laser WPE", default_="0.05") double wpe) {
		this.tuningTime = tuningTime;
		this.wpe = wpe;
	}

	@Override
	public double getTuningTimeNs() {
		return tuningTime;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("laser tuning time", tuningTime, "laser WPE", wpe);
	}

	@Override
	public double getLaserEfficiency(int nbChannels, double inputPowerRequiredPerLambdaMW) {
		return wpe;
	}

}
