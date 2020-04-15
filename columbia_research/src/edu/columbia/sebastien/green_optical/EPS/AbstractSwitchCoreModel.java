package edu.columbia.sebastien.green_optical.EPS;

import java.util.Map;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public abstract class AbstractSwitchCoreModel {

	
	public abstract double[] getPjPerSwitchedBit(double NRRateInjBW, double rrRate, int concentration, int switchR, 
			AbstractResultsManager man, DataPoint dp);
	
	public abstract Map<String, String> getAllProperties();
}
