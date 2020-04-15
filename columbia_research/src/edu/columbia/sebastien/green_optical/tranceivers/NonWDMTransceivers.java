package edu.columbia.sebastien.green_optical.tranceivers;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.green_optical.AbstractTransceiverModel;

public class NonWDMTransceivers extends AbstractTransceiverModel {

	private double rateInGbs;
	private double powerConsumption;
	private double maxBudget;
	private double laserShare;
	
	public NonWDMTransceivers(
			@ParamName(name="Rate in Gbs", default_="40") double rateInGbs,
			@ParamName(name="Power in W", default_="1.5") double powerConsumption,
			@ParamName(name="Laser share", default_="0.5") double laserShare,
			@ParamName(name="max budget dB", default_="20") double maxBudget
		) {
		super(null, null, Double.POSITIVE_INFINITY);
		this.rateInGbs = rateInGbs;
		this.powerConsumption = powerConsumption;
		this.laserShare = laserShare;
		this.maxBudget = maxBudget;
		if (laserShare > 1 || laserShare < 0) 
			throw new IllegalStateException("laser share must be between 0 and 1");
	}

	@Override
	public double getLaserPowerConsumption(double rate,
			int numberOfWavelengths, double linkBudgetIndB) {
		checkParams(rate, numberOfWavelengths, linkBudgetIndB);
		return laserShare * powerConsumption * 1000;
	}

	private void checkParams(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		if (rate != this.rateInGbs) {
			throw new WrongExperimentException();
		}
		if (linkBudgetIndB > maxBudget)
			throw new WrongExperimentException();
		if (numberOfWavelengths > 1)
			throw new WrongExperimentException("this transceiver has no WDM capability");
	}

	@Override
	public double getNonLaserPowerConsumption(double rate,
			int numberOfWavelengths, double linkBudgetIndB) {
		checkParams(rate, numberOfWavelengths, linkBudgetIndB);
		return (1 - laserShare) * powerConsumption * 1000;
	}

	@Override
	public double getModMuxDemuxPP(double rate, int numberOfWavelengths,
			Execution execution) {
		return 0;
	}

	@Override
	public Map<String, String> getTransceiverProperties() {
		return SimpleMap.getMap("Transceiver budget", maxBudget,
								"Transceiver power", powerConsumption);
	}

}
