package edu.columbia.sebastien.link_util.components;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ConstantPowerLaserConsumptionModel extends AbstractLaserConsumptionModel {
	
	private double efficiency;
	
	public ConstantPowerLaserConsumptionModel(@ParamName(name="Laser efficiency", default_="0.1") double efficiency) {
		this.efficiency = efficiency;
	}

	@Override
	public double getJoules(double lasingTime, double actualLasingPowerMW) {
		double power = actualLasingPowerMW/efficiency;
		double nanoJoule = power * lasingTime;
		return nanoJoule * 1e-12;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("laser efficiency", efficiency+"");
	}

}
