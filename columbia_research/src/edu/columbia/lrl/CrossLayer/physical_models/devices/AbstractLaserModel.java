package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

public abstract class AbstractLaserModel {

	public abstract double getLaserEfficiency(int nbChannels, double inputPowerRequiredPerLambdaMW);

	public abstract Map<? extends String, ? extends String> getAllParameters();

	public double getLaserWallPlugConsumption(double inputLaserPowerPerWavelength_mW, int nbChannels) {
		return inputLaserPowerPerWavelength_mW/getLaserEfficiency(nbChannels, inputLaserPowerPerWavelength_mW);
	}
	
	public boolean isPowerFixed() {
		return false;
	}

	public double getEmittedPowerdBm() {
		// TODO Auto-generated method stub
		return 0;
	}
}
