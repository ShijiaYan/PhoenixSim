package edu.columbia.sebastien.green_optical.EPS;

import java.util.Map;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class HUCAApowerModel extends AbstractSwitchCoreModel {
	
	private FittingExperiment fittingExp;
	
	public HUCAApowerModel(FittingExperiment fittingExp) {
		this.fittingExp = fittingExp;
	}

	@Override
	public double[] getPjPerSwitchedBit(double NRRateInjBW, double rrRate, int concentration, int switchR, 
			AbstractResultsManager man, DataPoint dp) {
		return fittingExp.calculate(NRRateInjBW, rrRate, concentration, switchR, man, dp);
	}

	@Override
	public Map<String, String> getAllProperties() {
		// TODO Auto-generated method stub
		return fittingExp.getAllProperties();
	}
	
	

}
