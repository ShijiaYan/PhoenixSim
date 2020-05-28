package edu.columbia.sebastien.green_optical;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractReceiverSensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public abstract class AbstractTransceiverModel {
	
	
	private static class LocalDetectorModel extends AbstractReceiverSensitivityModel {
		
		public LocalDetectorModel() {
		}

		@Override
		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap();
		}

		@Override
		public double getSensitivitydB(Constants ct, double rate) {
			return Double.NEGATIVE_INFINITY;
		}
		
	}
	
	protected AbstractReceiverSensitivityModel detectorModel;
	protected Constants ct;
	protected double nonLinearityLimitdBm;
	
	public AbstractTransceiverModel(AbstractReceiverSensitivityModel detectorModel,
			Constants ct,
			double nonLinearityLimitdBm) {
		this.detectorModel = detectorModel;
		if (detectorModel == null) {
			this.detectorModel = new LocalDetectorModel();
		}
		this.ct = ct;
		this.nonLinearityLimitdBm = nonLinearityLimitdBm;
	}

	public abstract double getLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB);
	public abstract double getNonLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB);
	public abstract double getModMuxDemuxPP(double rate, int numberOfWavelengths, Execution execution);

	public double getOutputLevel(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		double totLevel = detectorModel.getSensitivitydB(ct, rate) + linkBudgetIndB + getModMuxDemuxPP(rate, numberOfWavelengths, null);
		if (totLevel > nonLinearityLimitdBm)
			throw new WrongExperimentException();
		return totLevel;
	}
	
	public double getLaserWallPlugPower(double requiredLevel, double laserEfficiency) {
		double inputOpticalPowerMW = Math.pow(10, requiredLevel /10d);
		double laserWallPlugPower = inputOpticalPowerMW/laserEfficiency;
		return laserWallPlugPower;
	}

	public abstract Map<String, String> getTransceiverProperties();

	public Map<String, String> getAllProperties() {
		Map<String, String> map = getTransceiverProperties();
		map.put("Tranceiver type", this.getClass().getSimpleName());
		return map;
	}
}
