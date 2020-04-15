package edu.columbia.lrl.CrossLayer.physical_models.devices.signaling;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractModDemodPair;
import edu.columbia.lrl.CrossLayer.physical_models.devices.mux.AbstractMux;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class ModandMuxWDMModDemodModel extends AbstractWDMModDemodModel {
	
	private AbstractModDemodPair modDemodPair;
	private AbstractMux mux;
	
	public ModandMuxWDMModDemodModel(AbstractModDemodPair modDemodPair, AbstractMux aMux) {
		this.modDemodPair = modDemodPair;
		this.mux = aMux;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = modDemodPair.getAllParameters();
		m.putAll(mux.getAllParameters());
		m.put("Multiplexer used", mux.getClass().getSimpleName());
		m.put("Modulation format", modDemodPair.getModulationFormat());
		return m;
	}	

	@Override
	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format) {
		return modDemodPair.getReceiverSensitivity(modeSet, format);
	}

	@Override
	public boolean modBankhasThroughCapability() {
		return false;
	}

	@Override
	public ArrayList<PowerPenalty> getPassbyModulationBankPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("Cannot be passed by");
	}

	@Override
	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		Pair<Double, ArrayList<PowerPenalty>> pp = modDemodPair.getModulationERAndPowerPenalties(modelSet, linkFormat);
		pp.getSecond().addAll(mux.getMuxPowerPenalties(modelSet, linkFormat));
		return pp;
	}

	@Override
	public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		ArrayList<PowerPenalty> al = modDemodPair.getDemodulationPowerPenalties(modelSet, linkFormat);
		al.addAll(modDemodPair.getJitterPowerPenalty(modelSet, linkFormat));
		return al;
	}

	@Override
	public ArrayList<PowerConsumption> getModulationPowerConsumption(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return modDemodPair.getModulationPowerConsumptions(modelSet, linkFormat);
	}

	@Override
	public ArrayList<PowerConsumption> getDemodulationAndDetectionPowerConsumption(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet, 
			AbstractLinkFormat linkFormat) {
		return modDemodPair.getDemodulationAndReceptionPowerConsumptions(opticalPowerAtReceiverdBm, modelSet, linkFormat);
	}




}
