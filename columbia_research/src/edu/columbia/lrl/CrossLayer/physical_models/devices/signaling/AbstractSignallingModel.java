package edu.columbia.lrl.CrossLayer.physical_models.devices.signaling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class AbstractSignallingModel {

	public abstract ArrayList<PowerPenalty> getPowerPenalties(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat, int i, int j);

	public abstract List<PowerConsumption> getPowerConsumptions(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat);
	
	public List<PowerConsumption> getPowerConsumptionsWithMinimalLaser(
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return getPowerConsumptions(getReceiverSensitivity(modelSet, linkFormat), modelSet, linkFormat);
	}

	public abstract double getReceiverSensitivity(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat);

	public double getPowerBudgetdB(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, double maxPowerDB) {
		return maxPowerDB - getReceiverSensitivity(modelSet, linkFormat);
	}

	public abstract Map<String, String> getAllParameters();


}
