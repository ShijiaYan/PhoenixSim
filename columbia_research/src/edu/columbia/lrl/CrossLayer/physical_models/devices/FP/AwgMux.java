package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import ch.epfl.general_libraries.clazzes.ParamName;

public class AwgMux {

	// parameters
	double passiveIL ;
	double polarizationLoss ;
	double awgStaticPower ;

	public AwgMux(
			@ParamName(name = "Passive insertion loss (dB)", default_ = "3") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "AWG Thermal Tunning Power (mW)", default_ = "0") double awgStaticPower) {
		this.passiveIL = passiveIL ;
		this.polarizationLoss = polarizationLoss ;
		
	}



	public double getPowerPenalties(int numWavelengths) {
		
		double totalMuxLoss = passiveIL + polarizationLoss + 0.01 * numWavelengths ;
		
		return totalMuxLoss ;
		
	}
	
	// Power Consumption
	
	public double getAwgMuxPowerConsumption(double dataRateGbps){
		
		double powerConsumption = awgStaticPower ; // scaling power consumption with bit rate at 10 Gbps
		return powerConsumption * 1e3 ; // return power in micro watts
	}

}
