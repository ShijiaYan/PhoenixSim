package edu.columbia.lrl.CrossLayer.physical_models.devices._old;

import java.util.Map;

public abstract class AbstractDemodulator__ {
	
	public abstract double getPowerPenalties(double dutyCycle) ;
	public abstract double getDemodulatorPowerConsumption(double dataRateGbps) ;
	
	public abstract Map<String, String> getAllParameters();
}

	
