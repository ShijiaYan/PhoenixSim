package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractModulator {

	public static final String MODULATOR = "Modulator";

	public abstract Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);

	public abstract ArrayList<PowerConsumption> getPowerConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat);

	public abstract boolean modulatorHasThroughCapability();

	public abstract ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat);

	public abstract Map<String, String> getAllParameters();

}
