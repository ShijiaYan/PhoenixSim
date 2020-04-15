package edu.columbia.lrl.CrossLayer.physical_models.ad_hoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractDeviceModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class InterfaceWaveguideLengthModel extends AbstractDeviceModel {

	double modulatorPadding;
	double filterPadding;
	
	public InterfaceWaveguideLengthModel(
			@ParamName(name="Modulator padding (um)", default_="125") double modulatorPadding,
			@ParamName(name="Filter padding (um)", default_="10") double filterPadding
			) {
		this.modulatorPadding = modulatorPadding;
		this.filterPadding = filterPadding;
	}
		
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap(
				"Modulator padding", modulatorPadding+"",
				"Filter padding", filterPadding+"");				
	}

	
	public double getModulatorWaveguideLengthCm(int wavelengths) {
		return wavelengths*modulatorPadding / 10000;		
	}
	
	public double getFilterWaveguideLengthCm(int wavelengths) {
		return wavelengths*filterPadding / 10000;
	}

	public String toString() {
		return "Interface waveguide";
	}
	
	@Override
	public List<PowerConsumption> getDevicePowerConsumptions(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return new ArrayList<PowerConsumption>(0);
	}	
	
}
