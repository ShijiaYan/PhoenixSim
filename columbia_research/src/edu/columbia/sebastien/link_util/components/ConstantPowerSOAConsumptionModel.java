package edu.columbia.sebastien.link_util.components;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class ConstantPowerSOAConsumptionModel extends
		AbstractSOAConsumptionModel {
	
	private double efficiency;
	
	public ConstantPowerSOAConsumptionModel(double efficiency) {
		this.efficiency = efficiency;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("soa efficiency", efficiency+"");
	}

	@Override
	public double getJoules(double lasingTime, double inputPower,
			double desiredOutputPowerMW) {
		double power = desiredOutputPowerMW/efficiency;
		double nanoJoule = power * lasingTime;
		return nanoJoule * 1e-12;
	}


}
