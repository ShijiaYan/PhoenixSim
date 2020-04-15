package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public abstract class AbstractModDemodPair {
	
	/* Seb 09/17/15 not sure whether
	 * 
	 * 1) object should provide mod and demod by getter, and let the
	 * "client" call their methods directly
	 * 
	 * 2) redirect the "client" calls on their methods

		2) is choosen for now but without conviction
	 */
	
	protected AbstractModulator mod;
	protected AbstractDemodulatorAndReceiver demod;
	

	public abstract String getModulationFormat();
	
	public AbstractModDemodPair(AbstractModulator mod, AbstractDemodulatorAndReceiver demod) {
		this.mod = mod;
		this.demod = demod;
	}
	

	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return mod.getModulationERAndPowerPenalties(modelSet, linkFormat);
	}
	
	public ArrayList<PowerPenalty> getPassbyModulationPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return mod.getPassbyPowerPenalties(modelSet, linkFormat); 
	}	

	public ArrayList<PowerConsumption> getModulationPowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return mod.getPowerConsumption(modelSet, linkFormat);
	}
	
	public boolean modulatorHasThroughCapability() {
		return mod.modulatorHasThroughCapability();
	}

	// --------------------- RECEIVER
	
	public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return demod.getDemodulationPowerPenalties(modelSet, linkFormat);
	}
	
	public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return demod.getDemodulationAndReceptionPowerConsumptions(opticalPowerAtReceiverdBm, modelSet, linkFormat);
	}

	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet,
			AbstractLinkFormat format) {
		return demod.getReceiverSensitivity(modeSet, format);
	}

	public ArrayList<PowerPenalty> getJitterPowerPenalty(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return demod.getJitterPowerPenalty(modelSet, linkFormat);
	}
	
	public boolean receiverHasThroughCapability() {
		return demod.receiverHasThroughCapability();
	}

	public ArrayList<PowerPenalty> getPassbyDemodPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return demod.getPassbyDemodPowerPenalties(modelSet, linkFormat);
	}	
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = mod.getAllParameters();
		m.putAll(demod.getAllParameters());	
		m.put("Modulator model", mod.getClass().getSimpleName());
		return m;
	}

	

}
