package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.sensitivity;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class Constant_OOK_NRZ_ReceiverSensitivity extends Abstract_OOK_NRZ_SensitivityModel {

	double sensitivity;
	
	public Constant_OOK_NRZ_ReceiverSensitivity(
			@ParamName(name="Photodetector sensitivity (dBm)", default_="-20") double sensitivity
			) {
		this.sensitivity = sensitivity;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Photodetector sensitivity (dB)", sensitivity+"");
	}

	@Override
	public String toString() {
		return "Constant sensitivity detector";
	}

	@Override
	public double getSensitivitydB(Constants ct, double rate) {
		return sensitivity;
	}
}
