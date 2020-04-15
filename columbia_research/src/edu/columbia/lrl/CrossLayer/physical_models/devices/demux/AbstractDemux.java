//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

	public abstract ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet var1,
			AbstractLinkFormat var2, Execution var3, double var4);

	public abstract boolean hasThroughCapability();

	public abstract ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet var1,
			AbstractLinkFormat var2);
}
