//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import java.util.ArrayList;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractDemodulatorAndReceiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.Abstract_OOK_SERDES_PowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public abstract class Abstract_OOK_NRZ_Receiver extends AbstractDemodulatorAndReceiver {

	private Abstract_OOK_SERDES_PowerModel serdesPowerModel;

	public Abstract_OOK_NRZ_Receiver(double passiveJitterPenalty, Abstract_OOK_SERDES_PowerModel serdes) {
		super(passiveJitterPenalty);
		this.serdesPowerModel = serdes;
	}

	public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		double powCon = this.serdesPowerModel.getPowerConsumptionMW(linkFormat);
		new PowerConsumption("SERDES", true, true, false, powCon);
		return MoreArrays.getArrayList(new PowerConsumption[0]);
	}
}
