//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays;

import java.util.ArrayList;
import java.util.Map;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.Abstract_OOK_NRZ_Receiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractWDMModDemodModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public abstract class AbstractRingBased_OOK_NRZ_ModulatorArrayModel extends AbstractWDMModDemodModel {

	private Abstract_OOK_NRZ_Receiver ookReceiver;
	private AbstractRingPNJunctionDriverPowerModel powMod;

	public abstract Map<String, String> getRingBasedArrayParameters();

	public AbstractRingBased_OOK_NRZ_ModulatorArrayModel(Abstract_OOK_NRZ_Receiver ookReceiver,
			AbstractRingPNJunctionDriverPowerModel powMod) {
		this.ookReceiver = ookReceiver;
		this.powMod = powMod;
	}

	public ArrayList<PowerConsumption> getModulationPowerConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<>(1);
		PowerConsumption p = new PowerConsumption("Modulator static", false, true, true,
				modelSet.getDefaultSingleRingTTPowerMW());
		double modulatorConsumptions = this.powMod.getAverageConsumption(
				this.getDrivingVoltage(modelSet.getConstants(), linkFormat), this.getDrivingCapacitance(), linkFormat);
		PowerConsumption p2 = new PowerConsumption("Modulator dynamic", true, true, true, modulatorConsumptions);
		pc.add(p);
		pc.add(p2);
		return pc;
	}

	public abstract double getDrivingCapacitance();

	public abstract double getDrivingVoltage(Constants var1, AbstractLinkFormat var2);

	public boolean modBankhasThroughCapability() {
		return true;
	}

	public final Map<String, String> getAllParameters() {
		Map<String, String> m = this.ookReceiver.getAllParameters();
		m.putAll(this.powMod.getAllParameters());
		m.put("modulator power model", this.powMod.getClass().getSimpleName());
		m.put("modulator array model", this.getClass().getSimpleName());
		m.putAll(this.getRingBasedArrayParameters());
		return m;
	}

	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format) {
		return this.ookReceiver.getReceiverSensitivity(modeSet, format);
	}

	public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerPenalty> al = this.ookReceiver.getJitterPowerPenalty(modelSet, linkFormat);
		al.addAll(this.ookReceiver.getDemodulationPowerPenalties(modelSet, linkFormat));
		return al;
	}

	public ArrayList<PowerConsumption> getDemodulationAndDetectionPowerConsumption(double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return this.ookReceiver.getDemodulationAndReceptionPowerConsumptions(opticalPowerAtReceiverdBm, modelSet,
				linkFormat);
	}
}
