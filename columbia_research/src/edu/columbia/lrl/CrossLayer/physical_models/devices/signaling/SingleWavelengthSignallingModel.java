package edu.columbia.lrl.CrossLayer.physical_models.devices.signaling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractModDemodPair;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class SingleWavelengthSignallingModel extends AbstractSignallingModel {
	
	private AbstractModDemodPair modDemod;
	
	public SingleWavelengthSignallingModel(AbstractModDemodPair modDemod) {
		this.modDemod = modDemod;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = modDemod.getAllParameters();
		return m;
	}		

	@Override
	public ArrayList<PowerPenalty> getPowerPenalties(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat, int i, int j) {
		
		ArrayList<PowerPenalty> allPP = new ArrayList<PowerPenalty>();
		
		if (i > 1) {
			if (!modDemod.modulatorHasThroughCapability()) {
				String name = modDemod.getClass().getSimpleName();
				throw new WrongExperimentException("Modulator of " + name + " cannot be traversed");
			}
			ArrayList<PowerPenalty> passMod = modDemod.getPassbyModulationPowerPenalties(modelSet, linkFormat);
			PowerPenalty.multiply(passMod, i-1);
			allPP.addAll(passMod);			
		}
		if (j > 1) {
			if (!modDemod.receiverHasThroughCapability()) {
				String name = modDemod.getClass().getSimpleName();
				throw new WrongExperimentException("Modulator of " + name + " cannot be traversed");
			}
			ArrayList<PowerPenalty> passDemod = modDemod.getPassbyDemodPowerPenalties(modelSet, linkFormat);		
			PowerPenalty.multiply(passDemod, j-1);
			allPP.addAll(passDemod);			
		}
		Pair<Double, ArrayList<PowerPenalty>> mod = modDemod.getModulationERAndPowerPenalties(modelSet, linkFormat);
		ArrayList<PowerPenalty> demod = modDemod.getDemodulationPowerPenalties(modelSet, linkFormat);	
		
		allPP.addAll(mod.getSecond());
		allPP.addAll(demod);
		return allPP;
	}

	@Override
	public List<PowerConsumption> getPowerConsumptions(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> mod = modDemod.getModulationPowerConsumptions(modelSet, linkFormat);
		mod.addAll(modDemod.getDemodulationAndReceptionPowerConsumptions(opticalPowerAtReceiverdBm, modelSet, linkFormat));
		return mod;
	}


	@Override
	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet,
			AbstractLinkFormat format) {
		return modDemod.getReceiverSensitivity(modeSet, format);
	}


}
