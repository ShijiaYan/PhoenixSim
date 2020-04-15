//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.interleaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractDeviceModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractInterleaver extends AbstractDeviceModel {

	public AbstractInterleaver() {
	}

	public abstract int getPortNumbers();

	public abstract double getLeaverXTalk();

	public abstract List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet var1,
			AbstractLinkFormat var2);

	public abstract ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet var1,
			AbstractLinkFormat var2, Execution var3, double var4);

	public abstract Map<String, String> getAllParameters();
}
