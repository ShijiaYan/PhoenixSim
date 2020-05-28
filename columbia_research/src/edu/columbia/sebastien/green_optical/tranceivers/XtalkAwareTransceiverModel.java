package edu.columbia.sebastien.green_optical.tranceivers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.AbstractDemux;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractReceiverSensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays.AbstractRingBased_OOK_NRZ_ModulatorArrayModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.ChannelsAndBandwidthFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.Number_X_RateFormat;
import edu.columbia.sebastien.green_optical.AbstractTransceiverModel;


public class XtalkAwareTransceiverModel extends AbstractTransceiverModel {

	private AbstractRingBased_OOK_NRZ_ModulatorArrayModel modArray;
	private AbstractDemux demux;
	private double laserEfficiency;

	private PhysicalParameterAndModelsSet modelSet;

	public XtalkAwareTransceiverModel(AbstractReceiverSensitivityModel detectorModel,
			AbstractRingBased_OOK_NRZ_ModulatorArrayModel modArray, AbstractDemux demux, Constants ct,
			@ParamName(name = "Laser efficiency", default_ = "0.02") double laserEfficiency,
			@ParamName(name = "Ring power stab (mW)", default_ = "0.5") double ringStabPower,
			@ParamName(name = "Non linear limit (dBm)", default_ = "20") double limit) {
		super(detectorModel, ct, limit);

		this.modArray = modArray;
		this.demux = demux;
		this.laserEfficiency = laserEfficiency;

	}

	@Override
	public double getNonLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB) {
		AbstractLinkFormat linkFormat = new Number_X_RateFormat(numberOfWavelengths, rate);

		List<PowerConsumption> list = modArray.getModulationPowerConsumption(modelSet, linkFormat);
		list.addAll(demux.getDevicePowerConsumptions(modelSet, linkFormat));

		double nonLaserPow = PowerConsumption.compute(list, 1, 1, 1, 1/* faking 1 wavelength for now */);

		// TODO Auto-generated method stub
		return nonLaserPow;
	}

	@Override
	public double getLaserPowerConsumption(double rate, int numberOfWavelengths, double linkBudgetIndB) {

		double requiredLevel = super.getOutputLevel(rate, numberOfWavelengths, linkBudgetIndB);
		double laserWallPlugPower = getLaserWallPlugPower(requiredLevel, laserEfficiency);

		// TODO Auto-generated method stub
		return laserWallPlugPower;
	}

	@Override
	public double getModMuxDemuxPP(double rate, int numberOfWavelengths, Execution execution) {

		AbstractLinkFormat linkFormat = new ChannelsAndBandwidthFormat(numberOfWavelengths, rate);

		Pair<Double, ArrayList<PowerPenalty>> pair = modArray.getModulationERAndPowerPenalties(modelSet, linkFormat);
		pair.getSecond().addAll(demux.getPowerPenalties(modelSet, linkFormat, execution, pair.getFirst()));

		double accum = 0;
		for (PowerPenalty pp : pair.getSecond()) {
			accum += pp.getTotalPowerPenalty();
		}

		return accum;
	}

	@Override
	public Map<String, String> getTransceiverProperties() {
		Map<String, String> m = new PropertyMap();
		m.put("laser efficiency", laserEfficiency + "");
		m.putAll(modArray.getAllParameters());
		m.putAll(demux.getAllParameters());
		return m;
	}

}
