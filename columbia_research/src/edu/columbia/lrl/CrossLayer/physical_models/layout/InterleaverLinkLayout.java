package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractTunableLaserModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.AbstractDemux;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractWDMModDemodModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.ModandMuxWDMModDemodModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.WDMSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;

public class InterleaverLinkLayout extends AbstractPhysicalLayout {
	
	private AbstractDemux demux;
	private AbstractTunableLaserModel laserModel;
	
	public InterleaverLinkLayout(
			AbstractDemux demux,
			AbstractTunableLaserModel laserModel
		) {
		this.demux = demux;
		this.laserModel = laserModel;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = demux.getAllParameters();
		m.putAll(laserModel.getAllParameters());
		return m;
	}
	
	private ModandMuxWDMModDemodModel getModandMux(PhysicalParameterAndModelsSet modelSet) {
		AbstractSignallingModel sigMod = modelSet.getSignallingModel();
		if (!(sigMod instanceof WDMSignallingModel)) {
			throw new IllegalStateException("Tunable laser mux demux model must use WDM signalling");
		}
		AbstractWDMModDemodModel modDemod = ((WDMSignallingModel)sigMod).getWDMModDemodModel();
		
		if (!(modDemod instanceof ModandMuxWDMModDemodModel)) {
			throw new IllegalStateException("Tunable laser mux demux model must use a ModandMuxWDMModDemodModel signalling");
		}
		return (ModandMuxWDMModDemodModel)modDemod;
	}

	@Override
	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(
			Execution ex, PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		
		LayoutWorseCaseProperties prop = new LayoutWorseCaseProperties(linkFormat.getNumberOfChannels());
		
		ModandMuxWDMModDemodModel modDemod = getModandMux(modelSet);
		
		Pair<Double, ArrayList<PowerPenalty>> mod = modDemod.getModulationERAndPowerPenalties(modelSet, linkFormat);
		prop.addPowerPenalties(mod.getSecond());
			
		prop.addPowerPenalties(demux.getPowerPenalties(modelSet, linkFormat, ex, mod.getFirst()));
		prop.addPowerPenalties(modDemod.getDemodulationPowerPenalties(modelSet, linkFormat));
		
		return prop;
	}
	
	public List<PowerConsumption> getPowerConsumptions(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		List<PowerConsumption> list = new ArrayList<>();
		
		ModandMuxWDMModDemodModel modDemod = getModandMux(modelSet);
		
		List<PowerConsumption> modPCList = modDemod.getModulationPowerConsumption(modelSet, linkFormat);
		for (PowerConsumption pc : modPCList) {
			pc.setPerWavelength(false);
			list.add(pc);
		}
		
		double receiverSensitivity = modDemod.getReceiverSensitivity(modelSet, linkFormat);		

		list.addAll(modDemod.getDemodulationAndDetectionPowerConsumption(receiverSensitivity, modelSet, linkFormat));
		LayoutWorseCaseProperties worseCaseProp = getLayoutPropertiesForaGivenNumberOfWavelengths(null, modelSet, linkFormat);


		double loss = worseCaseProp.getTotalPowerPenalty();
		
		double reqOptPow_mW = PhysicalParameterAndModelsSet.dBmTomW(receiverSensitivity+loss); 
		
		PowerConsumption las = modelSet.getSingleLaserConsumption(laserModel, reqOptPow_mW, linkFormat.getNumberOfChannels());
		
		las.setCircuit(true);
		las.setPerWavelength(false);
		
		list.add(las);		
		return list;
	}	

	@Override
	public List<PowerConsumption> getLayoutSpecificConsumption(
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("Should not be there");
	}

	@Override
	public double getUnavailabilityTime() {
		return laserModel.getTuningTimeNs();
	}

}
