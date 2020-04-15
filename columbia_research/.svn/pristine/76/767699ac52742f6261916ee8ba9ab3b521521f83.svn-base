package edu.columbia.lrl.CrossLayer.physical_models.devices.signaling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.AbstractDemux;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.RingBasedFilterArrayModelAdaptiveIL;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays.CarrierInjectionRing_OOK_NRZ_ModulatorArrayModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class WDMSignallingModel extends AbstractSignallingModel {
	
	private AbstractWDMModDemodModel wdmModel;
	private AbstractDemux demux;
	
	public WDMSignallingModel(
			@ParamName(name="", defaultClass_=CarrierInjectionRing_OOK_NRZ_ModulatorArrayModel.class) AbstractWDMModDemodModel wdmModel, 
			@ParamName(name="", defaultClass_=RingBasedFilterArrayModelAdaptiveIL.class) AbstractDemux demux) {
		this.wdmModel = wdmModel;
		this.demux = demux;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = wdmModel.getAllParameters();
		m.put("wdmModel", wdmModel.getClass().getSimpleName());
		m.putAll(demux.getAllParameters());
		return m;
	}
	
	public AbstractWDMModDemodModel getWDMModDemodModel() {
		return wdmModel;
	}

	@Override
	public ArrayList<PowerPenalty> getPowerPenalties(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat, int i, int j) {
		ArrayList<PowerPenalty> all = new ArrayList<PowerPenalty>();
		if (j > 1) {
			if (!demux.hasThroughCapability()) {
				// in the future, account for a 1x2 switch penalty
				throw new WrongExperimentException("Cannot use multi demux (" + j + ") with demux of type " + demux.getClass().getSimpleName());
			}
			ArrayList<PowerPenalty> passDemux = demux.getPassbyPowerPenalties(modelSet, linkFormat);	
			PowerPenalty.multiply(passDemux, j-1);
			all.addAll(passDemux);
		}
		
		if (i > 1) {
			if (wdmModel.modBankhasThroughCapability()) {
				throw new WrongExperimentException("Cannot use multi mod (" + i + ") with mod of type " + wdmModel.getClass().getSimpleName());			
			}
			ArrayList<PowerPenalty> passMod = wdmModel.getPassbyModulationBankPowerPenalties(modelSet, linkFormat);
			PowerPenalty.multiply(passMod, i-1);
			all.addAll(passMod);
		}
		
		
		Pair<Double, ArrayList<PowerPenalty>> mod = wdmModel.getModulationERAndPowerPenalties(modelSet, linkFormat);
		ArrayList<PowerPenalty> demuxPP = demux.getPowerPenalties(modelSet, linkFormat, ex, mod.getFirst()) ;		
		ArrayList<PowerPenalty> demod = wdmModel.getDemodulationPowerPenalties(modelSet, linkFormat);	
			
		all.addAll(mod.getSecond());
		all.addAll(demod);
		all.addAll(demuxPP);
		return all;
	}

	@Override
	public List<PowerConsumption> getPowerConsumptions(
			double opticalPowerAtReceiverdBm,
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		List<PowerConsumption> pc = demux.getDevicePowerConsumptions(modelSet, linkFormat);
		pc.addAll(wdmModel.getModulationPowerConsumption(modelSet, linkFormat));
		pc.addAll(wdmModel.getDemodulationAndDetectionPowerConsumption(opticalPowerAtReceiverdBm, modelSet, linkFormat));
		return pc;
	}


	@Override
	public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet,
			AbstractLinkFormat format) {
		return wdmModel.getReceiverSensitivity(modeSet, format);
	}

}
