package edu.columbia.lrl.CrossLayer.physical_models.devices.signaling;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class AbstractWDMModDemodModel {
	

	public abstract Map<String, String> getAllParameters();

	public abstract boolean modBankhasThroughCapability();

	public abstract ArrayList<PowerPenalty> getPassbyModulationBankPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);
	public abstract Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);
	public abstract ArrayList<PowerConsumption> getModulationPowerConsumption(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);

	public abstract double getReceiverSensitivity(PhysicalParameterAndModelsSet modelSet,AbstractLinkFormat linkFormat);
	public abstract ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);
	public abstract ArrayList<PowerConsumption> getDemodulationAndDetectionPowerConsumption(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);
	

}
