package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.sensitivity;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
//import edu.columbia.lrl.CrossLayer.addons.ResultManager;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class PIN_OOK_NRZ_ReceiverSensitivityModel extends Abstract_OOK_NRZ_SensitivityModel {

	// Parameters
	private double efficiency;
	private double gamma;

	// Part of thermal variance calculation
	private final double magicThermal = 1.656e-40;
	private final double magicResponsivity = 1.24;

	public PIN_OOK_NRZ_ReceiverSensitivityModel(
			@ParamName(name = "Effiency 0<=N<=1", default_ = "1.0") double efficiency,
			@ParamName(name = "Gamma", default_ = "7") double gamma) {
		this.efficiency = efficiency;
		this.gamma = gamma;

	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Efficiency", efficiency + "", "Gamma", gamma + "");
	}

	@Override
	public String toString() {
		return "PINDetector";
	}

	@Override
	public double getSensitivitydB(Constants ct, double rate) {
		double responsivity = efficiency * ct.getCenterWavelength() * 1e6d / magicResponsivity;
		double bandwidth = rate / 2.0d;

		double thermalDev = Math.sqrt(magicThermal * rate * rate * rate);

		double sensitivity = (gamma * ((Constants.ELECTRON_CHARGE * bandwidth * gamma) + thermalDev)) / responsivity;

		double sens_dB = 30 + 10d * Math.log10(sensitivity);

		return sens_dB;
	}

}
