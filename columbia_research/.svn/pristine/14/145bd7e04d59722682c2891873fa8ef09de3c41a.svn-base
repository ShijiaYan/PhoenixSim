package edu.columbia.lrl.CrossLayer.physical_models.devices._old;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RZ_OOK_Demodulator extends AbstractDemodulator__ {

	// parameters
	private double passiveIL ;
	private double polarizationLoss ;
	private double demodStaticPower ;
	private double inputCoupling ;
	private double balancedDetecCouplingLoss ;
	private double passiveJitterPenalty ;

	public RZ_OOK_Demodulator(
			@ParamName(name = "Passive insertion loss (dB)", default_ = "0.5") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "Jitter Penalty (dB)", default_ = "2") double passiveJitterPenalty,
			@ParamName(name = "Receiver Power @ 10 Gb/s (mW)", default_ = "3") double demodStaticPower) {
		this.passiveIL = passiveIL ;
		this.polarizationLoss = polarizationLoss ;
		this.passiveJitterPenalty = passiveJitterPenalty ;
		this.demodStaticPower = demodStaticPower ;
		
	}
	
	// Write the map
	@Override
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Receiver Power (mW)", demodStaticPower + "");
//		map.put("Effective index", effectiveIndex + "");
		return map;
	}

	@Override
	public double getPowerPenalties(double dutyCycle) {
		
		double jitterPP = passiveJitterPenalty + 0 * dutyCycle ; // 5 dB penalty if 100% duty cycle (a linear assumption)
		double demodulatorLoss = passiveIL + polarizationLoss  + jitterPP ;
		
		return demodulatorLoss ;
		
	}
	
	// Power Consumption?
	@Override
	public double getDemodulatorPowerConsumption(double dataRateGbps){
		
		double powerConsumption = demodStaticPower * (dataRateGbps/10) * (dataRateGbps/10) ; // scaling power consumption with bit rate at 10 Gbps
		return powerConsumption * 1e3 ; // return power in micro watts
	}



}
