//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

	public AbstractSignallingModel() {
	}

	public abstract ArrayList<PowerPenalty> getPowerPenalties(Execution var1, PhysicalParameterAndModelsSet var2,
			AbstractLinkFormat var3, int var4, int var5);

	public abstract List<PowerConsumption> getPowerConsumptions(double var1, PhysicalParameterAndModelsSet var3,
			AbstractLinkFormat var4);

	public List<PowerConsumption> getPowerConsumptionsWithMinimalLaser(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return this.getPowerConsumptions(this.getReceiverSensitivity(modelSet, linkFormat), modelSet, linkFormat);
	}

	public abstract double getReceiverSensitivity(PhysicalParameterAndModelsSet var1, AbstractLinkFormat var2);

	public double getPowerBudgetdB(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat,
			double maxPowerDB) {
		return maxPowerDB - this.getReceiverSensitivity(modelSet, linkFormat);
	}

	public ArrayList<PowerPenalty> getPowerPenalties(Execution ex, PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return this.getPowerPenalties(ex, modelSet, linkFormat, 1, 1);
	}

	public abstract Map<String, String> getAllParameters();
}
