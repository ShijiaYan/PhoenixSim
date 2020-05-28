package edu.columbia.lrl.CrossLayer.physical_models.devices.demux;

import java.util.ArrayList;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractDeviceModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractDemux extends AbstractDeviceModel {

	public static final String DEMUX = "Demux";

	public AbstractDemux() {
	}

	public abstract ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelsSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulatorER);

	public abstract boolean hasThroughCapability();

	public abstract ArrayList<PowerPenalty> getPassByPowerPenalties(PhysicalParameterAndModelsSet modelsSet,
																	AbstractLinkFormat linkFormat);
}
