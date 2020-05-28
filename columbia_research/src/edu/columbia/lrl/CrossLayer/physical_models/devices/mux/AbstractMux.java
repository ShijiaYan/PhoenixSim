package edu.columbia.lrl.CrossLayer.physical_models.devices.mux;

import java.util.ArrayList;
import java.util.Map;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractDeviceModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class AbstractMux extends AbstractDeviceModel {
	
	public static final String MUX = "Multiplexing";

	public abstract ArrayList<PowerPenalty> getMuxPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat);	
	
	public abstract double getMuxTotalPenalty();
	
	public abstract int getMuxChannel();
	
	public abstract double getMuxChannelPenalty();
	
	public abstract Map<String, String> getAllParameters();
}
