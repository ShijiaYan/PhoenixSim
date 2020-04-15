//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.ad_hoc.InterfaceWaveguideLengthModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;


public class P2PLayout extends PhysicalLayout {

	int numCouplers;
	PhysicalParameterAndModelsSet devices;

	public P2PLayout(@ParamName(name = "Number of couplers", default_ = "2") int numCouplers) {
		this.numCouplers = numCouplers;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap();
		map.put("Number of couplers", String.valueOf(this.numCouplers));
		return map;
	}

	public String toString() {
		return "P2P layout";
	}

	public double getUnavailabilityTime() {
		return 0.0D;
	}

	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		int wavelengths = linkFormat.getNumberOfChannels();
		LayoutWorseCaseProperties prop = new LayoutWorseCaseProperties(wavelengths);
		Constants ct = modelSet.getConstants();
		AbstractSignallingModel sig = modelSet.getSignallingModel();
		prop.addPowerPenalties(sig.getPowerPenalties(ex, modelSet, linkFormat, 1, 1));
		prop.addPowerPenalty(modelSet.getCouplerPenalty().multiply(this.numCouplers));
		InterfaceWaveguideLengthModel waveGuideModel = modelSet.getInterfaceWaveguideLengthModel();
		double waveguideLengthCm = waveGuideModel.getModulatorWaveguideLengthCm(wavelengths)
				+ waveGuideModel.getFilterWaveguideLengthCm(wavelengths);
		prop.addPowerPenalty(modelSet.getWaveguidePenalty().multiply(waveguideLengthCm));
		double propLatencyNS = waveguideLengthCm / (100.0D * ct.getSpeedOfLight()) * 1.0E9D;
		prop.setLinkLatency(propLatencyNS);
		return prop;
	}

	public List<PowerConsumption> getLayoutSpecificConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return new ArrayList(0);
	}
}
