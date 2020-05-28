package edu.columbia.lrl.CrossLayer.physical_models.devices.switches;

import java.util.ArrayList;
import java.util.List;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class Abstract2x2Switch extends AbstractRingBasedSwitch {
	
	protected final static String _2x2Switch = "2x2 switch";
	
	public Abstract2x2Switch(double ringDrivingPower) {
		super(ringDrivingPower);
	}

	public abstract PowerPenalty getPowerPenalty(AbstractLinkFormat linkFormat, int numStages);
	
	
	public List<PowerConsumption> getDevicePowerConsumptions(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		List<PowerConsumption> pc = new ArrayList<>(1);
		pc.add(new PowerConsumption(_2x2Switch, false, false, false, drivingPower*0.5 + modelSet.getDefaultSingleRingTTPowerMW()));
		return pc;
	}

	public abstract double getSize();
	
	
}
