package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class Abstract_OOK_SERDES_PowerModel {

	public abstract double getPowerConsumptionMW(AbstractLinkFormat linkFormat);

}
