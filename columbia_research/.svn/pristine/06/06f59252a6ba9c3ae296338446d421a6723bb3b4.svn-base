package edu.columbia.sebastien.green_optical;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractReceiverSensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.sensitivity.Ge_OOK_NRZ_ReceiverTakashi;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class DefaultTransceiverModel extends AbstractTransceiverModel {
	
	private double nonLaserPerBitEnergyAt10G;
	private double powerScale;
	private double laserEfficiency;
	
	private double constModMuxDemuxPP;


	public DefaultTransceiverModel(
			@ParamName(name="Detector model", defaultClass_=Ge_OOK_NRZ_ReceiverTakashi.class) AbstractReceiverSensitivityModel detectorModel,
			Constants ct,
			@ParamName(name="Reference non laser power at 10G in mW/Gb/s", default_="20") double nonLaserPerBitEnergyAt10G,
			@ParamName(name="Scaling of power with rate", default_="2") double powerScale,
			@ParamName(name="Laser efficiency", default_="0.1") double laserEfficiency,
			@ParamName(name="Constant modmuxdemux PP (dB)", default_="4") double constModMuxDemuxPP,
			@ParamName(name="Non linear limit (dBm)", default_="20") double limit) {
		super(detectorModel, ct, limit);

		this.nonLaserPerBitEnergyAt10G = nonLaserPerBitEnergyAt10G;
		this.powerScale = powerScale;
		this.laserEfficiency = laserEfficiency;
		this.constModMuxDemuxPP = constModMuxDemuxPP;
	}

	@Override
	public double getLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		double requiredLevel = super.getOutputLevel(rate, numberOfWavelengths, linkBudgetIndB);
		double laserWallPlugPower = getLaserWallPlugPower(requiredLevel, laserEfficiency);
		
		return laserWallPlugPower;
	}
	
	@Override
	public double getNonLaserPowerConsumption(double rate,
			int numberOfWavelengths, double linkBudgetIndB) {
		double scaling = Math.pow(rate/10d, powerScale);
		double modDemod = rate*nonLaserPerBitEnergyAt10G*scaling;
		
		return modDemod;
	}	
	
	@Override
	public double getModMuxDemuxPP(double rate, int numberOfWavelengths, Execution e) {
		return constModMuxDemuxPP;
	}	

	@Override
	public Map<String, String> getTransceiverProperties() {
		Map<String, String> m = new SimpleMap<String, String>();
		m.put("non laser power at 10G (mW/Gb/s)", nonLaserPerBitEnergyAt10G+"");
		m.put("trans power scaling", powerScale+"");
		m.put("laser efficiency", laserEfficiency+"");
		return m;
	}





}
