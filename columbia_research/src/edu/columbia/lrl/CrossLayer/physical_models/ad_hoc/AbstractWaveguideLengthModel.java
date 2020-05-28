package edu.columbia.lrl.CrossLayer.physical_models.ad_hoc;

import java.util.ArrayList;
import java.util.List;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractDeviceModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractWaveguideLengthModel extends AbstractDeviceModel {

	public abstract double getWaveguideLengthCm(int numberSites);

	public abstract int getNumBends(int numberSites);

	public abstract String toString();

	@Override
	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return new ArrayList<>(0);
	}

}
