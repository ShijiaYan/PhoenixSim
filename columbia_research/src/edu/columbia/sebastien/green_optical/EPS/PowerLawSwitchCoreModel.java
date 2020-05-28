package edu.columbia.sebastien.green_optical.EPS;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.SimpleMap;

public class PowerLawSwitchCoreModel extends AbstractSwitchCoreModel {
	
	private double baseConsumption; // for "null" radix, "low" rate of 20G
	private double portScalingCoeff;
	private double rateScalingCoeff;
	private double portScalingPower;
	private double rateScalingPower;
	
	public PowerLawSwitchCoreModel(
			@ParamName(name="Base consumption (pJ/bit)", default_="8") double baseConsumption,
			@ParamName(name="portScalingCoeff", default_="0.002") double portScalingCoeff,
			@ParamName(name="portScalingPower", default_="1.5") double portScalingPower,			
			@ParamName(name="rateScalingCoeff", default_="1") double rateScalingCoeff,
			@ParamName(name="rateScalingPower", default_="1") double rateScalingPower
			) {
		this.baseConsumption = baseConsumption;
		this.portScalingCoeff = portScalingCoeff;
		this.portScalingPower = portScalingPower;
		this.rateScalingCoeff = rateScalingCoeff;
		this.rateScalingPower = rateScalingPower;
		// TODO Auto-generated constructor stub
	}

	@Override
	public double[] getPjPerSwitchedBit(double NRRateInjBW, double rrRateInGbs, int concentration ,int switchR, 
			AbstractResultsManager man, DataPoint dp) {
		double totalRRRate = switchR * rrRateInGbs;
		double totalNRRate = concentration * NRRateInjBW;
		
		double averageRate = (totalRRRate + totalNRRate)/(concentration + switchR);
		
		double baseForRadix = baseConsumption + portScalingCoeff * Math.pow(concentration + switchR, portScalingPower);
		double rateFactor = averageRate/rateScalingCoeff;
		double rateFactorPowerized = Math.pow(rateFactor, rateScalingPower);
		return new double[]{baseForRadix*rateFactorPowerized};
	}

	@Override
	public Map<String, String> getAllProperties() {
		SimpleMap<String, String> map = new SimpleMap<>();
		map.put("Router model", "power law");
		map.put("Port scaling coeff", portScalingCoeff+"");
		map.put("Rate scaling coeff",  rateScalingCoeff+"");
		map.put("Port scaling power",  portScalingPower+"");
		map.put("Rate scaling power",  rateScalingPower+"");
		return map;
	}

}
