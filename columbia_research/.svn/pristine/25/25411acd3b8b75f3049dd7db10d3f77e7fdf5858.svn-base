package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FixedPowerLaserModel extends DefaultLaserModel {

	private double emittedPowerPerWavelengthdBm;
	
	public FixedPowerLaserModel(
			@ParamName(name="Emitted power per wavelength dBm", default_="-10") double emittedPowerPerWavelengthdBm,
			double laserEfficiency) {
		super(laserEfficiency);
		this.emittedPowerPerWavelengthdBm = emittedPowerPerWavelengthdBm;
	}
	
	public boolean isPowerFixed() {
		return true;
	}

	public double getEmittedPowerdBm() {
		return emittedPowerPerWavelengthdBm;
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("Fixed lasing power dBm", emittedPowerPerWavelengthdBm+"");
		return m;
	}	

}
