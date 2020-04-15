package edu.columbia.sebastien.green_optical.tranceivers;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.green_optical.AbstractTransceiverModel;

public class GridBasedTransceiverModel extends AbstractTransceiverModel {

	
	private double maxBudget;
	private double rate;
	private double maxChannels;
	private double power;

	public GridBasedTransceiverModel(
			@ParamName(name="max budget dB", default_="27") double maxBudget,
			@ParamName(name="rate in Gbps", default_="10") double rate,
			@ParamName(name="max nb channels", default_="80") double maxChannels,
			@ParamName(name="Power in W", default_="2") double power) {
		super(null, null, Double.POSITIVE_INFINITY);
		this.maxBudget = maxBudget;
		this.rate = rate;
		this.maxChannels = maxChannels;
		this.power = power;
	}

	@Override
	public double getLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		checkParameters(rate, numberOfWavelengths, linkBudgetIndB);
		return power*1000/2d;
	}

	@Override
	public double getNonLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		checkParameters(rate, numberOfWavelengths, linkBudgetIndB);
		return power*1000/2d;
	}

	@Override
	public double getModMuxDemuxPP(double rate, int numberOfWavelengths, Execution execution) {
		return 0;
	}
	
	private void checkParameters(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		if (rate != this.rate) {
			throw new IllegalStateException("transceiver made to work at " + rate);
		}
		if (numberOfWavelengths > maxChannels)
			throw new WrongExperimentException();
		if (linkBudgetIndB > maxBudget)
			throw new WrongExperimentException();
	}

	@Override
	public Map<String, String> getTransceiverProperties() {
		return SimpleMap.getMap();
	}

}
