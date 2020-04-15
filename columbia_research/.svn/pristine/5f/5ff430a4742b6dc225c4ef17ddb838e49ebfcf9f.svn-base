package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DefaultLaserModel extends AbstractLaserModel {
	
	private double laserEfficiency;
	
	public DefaultLaserModel(@ParamName(name = "Laser efficiency", default_ = ".1") double laserEfficiency) {
		this.laserEfficiency = laserEfficiency;		
	}
	
	public double getLaserEfficiency(int nbChannels, double inputPowerRequiredPerLambdaMW) {
		return laserEfficiency;
	}

	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Laser efficiency", laserEfficiency+"");
	}



}
