//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public abstract class AbstractRingModel {

	public AbstractRingModel() {
	}

	public abstract double getQ();

	public abstract double getER();

	public abstract double[] getCouplingCoeff_t_k(Constants var1);

	public abstract double getDBperCm(Constants var1);

	public abstract double getNeff();
}
