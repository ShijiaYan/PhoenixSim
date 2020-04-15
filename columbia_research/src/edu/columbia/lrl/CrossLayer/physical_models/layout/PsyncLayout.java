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
import edu.columbia.lrl.CrossLayer.physical_models.ad_hoc.AbstractWaveguideLengthModel;
import edu.columbia.lrl.CrossLayer.physical_models.ad_hoc.InterfaceWaveguideLengthModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.switches.Switch1x2;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;


public class PsyncLayout extends PhysicalLayout {

	InterfaceWaveguideLengthModel interfaceLength;
	AbstractWaveguideLengthModel busLength;
	int numCouplers;
	int sitesPerBranch;
	int totalSites;
	PhysicalParameterAndModelsSet devices;
	private Switch1x2 switch1x2;

	public PsyncLayout(InterfaceWaveguideLengthModel interfaceLength, AbstractWaveguideLengthModel busLength,
			@ParamName(name = "Number of couplers", default_ = "3") int numCouplers,
			@ParamName(name = "Sites per link", default_ = "4") int sitesPerBranch,
			@ParamName(name = "Number of transmitters", default_ = "16") int sites) {
		this.interfaceLength = interfaceLength;
		this.busLength = busLength;
		this.numCouplers = numCouplers;
		this.totalSites = sites;
		this.sitesPerBranch = sitesPerBranch;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Number of couplers", String.valueOf(this.numCouplers));
		map.put("Waveguide length model", "" + this.busLength);
		map.putAll(this.busLength.getAllParameters());
		map.putAll(this.interfaceLength.getAllParameters());
		return map;
	}

	public String toString() {
		return "Pysc layout";
	}

	public double getUnavailabilityTime() {
		throw new IllegalStateException("Not meant to be used this way");
	}

	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		LayoutWorseCaseProperties layoutProp = new LayoutWorseCaseProperties(linkFormat.getNumberOfChannels());
		Constants ct = modelSet.getConstants();
		AbstractSignallingModel signalingModel = this.devices.getSignallingModel();
		layoutProp.addPowerPenalties(
				signalingModel.getPowerPenalties(ex, modelSet, linkFormat, this.sitesPerBranch, this.sitesPerBranch));
		int numberDrop = this.totalSites <= this.sitesPerBranch ? 0 : 2;
		int numberThrough = this.totalSites / this.sitesPerBranch - 1;
		layoutProp.addPowerPenalties(
				this.get1x2SwitchModel().getPowerPenalties(ct, linkFormat, numberDrop, numberThrough));
		layoutProp.addPowerPenalty(this.devices.getCouplerPenalty().multiply(this.numCouplers));
		double receiverLengthCm = this.interfaceLength.getFilterWaveguideLengthCm(linkFormat.getNumberOfChannels());
		double busLengthCm = this.busLength.getWaveguideLengthCm(this.totalSites);
		int numBends = this.busLength.getNumBends(this.totalSites);
		layoutProp.addPowerPenalty(this.devices.getWaveguidePenalty().multiply(receiverLengthCm + busLengthCm));
		layoutProp.addPowerPenalty(this.devices.getBendPenalty().multiply(numBends));
		return layoutProp;
	}

	private Switch1x2 get1x2SwitchModel() {
		return this.switch1x2;
	}

	public List<PowerConsumption> getLayoutSpecificConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		System.out.println("Power consumption not implemented yet in Psync");
		return new ArrayList<PowerConsumption>(0);
	}
}
