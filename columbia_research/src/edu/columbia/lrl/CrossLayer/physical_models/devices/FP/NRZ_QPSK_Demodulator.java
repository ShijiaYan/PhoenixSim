package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.CrossLayer.physical_models.devices._old.AbstractDemodulator__;

public class NRZ_QPSK_Demodulator extends AbstractDemodulator__ {

	// parameters
	private double passiveIL ;
	private double polarizationLoss ;
	private double demodStaticPower ;
	private double inputCoupling ;
	private double balancedDetecCouplingLoss ;
	private double passiveJitterPenalty ;
	private double phaseShifterLoss ;
	private double junctionLoss ;

	public NRZ_QPSK_Demodulator(
			@ParamName(name = "Passive insertion loss (dB)", default_ = "0.5") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "Phase Shifter Loss (dB)", default_ = "0.5") double phaseShifterLoss,
			@ParamName(name = "Junction Loss (dB)", default_ = "0.5") double junctionLoss,
			@ParamName(name = "Input coupler efficiency", default_ = "0.8") double inputCoupling,
			@ParamName(name = "Balanced Detector Coupling Loss (dB)", default_ = "0.5") double balancedDetecCouplingLoss,
			@ParamName(name = "Jitter Penalty (dB)", default_ = "1") double passiveJitterPenalty,
			@ParamName(name = "Receiver Power @ 10 Gb/s (mW)", default_ = "3") double demodStaticPower) {
		this.passiveIL = passiveIL ;
		this.polarizationLoss = polarizationLoss ;
		this.inputCoupling = inputCoupling ;
		this.balancedDetecCouplingLoss = balancedDetecCouplingLoss ;
		this.passiveJitterPenalty = passiveJitterPenalty ;
		this.demodStaticPower = demodStaticPower ;
		this.phaseShifterLoss = phaseShifterLoss ;
		this.junctionLoss = junctionLoss ;
		
	}
	
	// Write the map
	@Override
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Receiver Power (mW)", demodStaticPower + "");
		return map;
	}

	@Override
	public double getPowerPenalties(double dutyCycle) {
		
		double jitterPP = passiveJitterPenalty + 1 * dutyCycle ; // 1 dB penalty if 100% duty cycle (a linear assumption)
		double demodulatorLoss = passiveIL + polarizationLoss + junctionLoss + phaseShifterLoss + 2*balancedDetecCouplingLoss + (-10*Math.log10(inputCoupling)) + jitterPP ;
		
		return demodulatorLoss ;
		
	}
	
	// Power Consumption?
	@Override
	public double getDemodulatorPowerConsumption(double dataRateGbps){
		
		double powerConsumption = demodStaticPower * (dataRateGbps/10) * (dataRateGbps/10) ; // scaling power consumption with bit rate at 10 Gbps
		return powerConsumption * 1e3 * 2 ; // return power in micro watts. Factor of two for both I and Q components.
	}

}
