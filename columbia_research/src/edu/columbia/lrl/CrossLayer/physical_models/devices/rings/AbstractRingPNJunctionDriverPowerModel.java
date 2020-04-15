//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import java.util.Map;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class AbstractRingPNJunctionDriverPowerModel {

	public AbstractRingPNJunctionDriverPowerModel() {
	}

	public abstract double getAverageConsumption(double var1, double var3, AbstractLinkFormat var5);

	public abstract double getEnergyPJperBit(double var1, double var3, AbstractLinkFormat var5);

	public abstract Map<String, String> getAllParameters();
}
