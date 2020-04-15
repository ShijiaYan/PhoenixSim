package edu.columbia.lrl.CrossLayer.physical_models.devices;

import java.util.List;
import java.util.Map;

import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class AbstractDeviceModel {
	
	public abstract Map<String, String> getAllParameters();
	
//	public abstract Map<String, Double> getDevicePowerConsumption(PhysicalLayoutBasedLWSimExperiment phyExp, double utilization);
	
	public abstract List<PowerConsumption> getDevicePowerConsumptions(
			PhysicalParameterAndModelsSet modelSet, 
			AbstractLinkFormat linkFormat);
	
}
