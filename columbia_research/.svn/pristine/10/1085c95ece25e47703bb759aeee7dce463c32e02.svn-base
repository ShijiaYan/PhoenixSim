package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import ch.epfl.general_libraries.clazzes.ParamName;

public class GeDetector {
	
	//Parameters
	private double responsivity;
	private double extinctionDB;
	private double darkCurrent ; // dark current of the 
	
	
	public GeDetector(
		@ParamName(name="Responsivity (A/W)", default_="0.7") double responsivity,
		@ParamName(name="Extinction Ratio (dB)", default_="12") double extinctionDB,
		@ParamName(name="PhotoDetector Dark Current (uA)", default_="1") double darkCurrent
			) {
		this.responsivity = responsivity ;
		this.extinctionDB = extinctionDB ;
		this.darkCurrent = darkCurrent ;

	}



	
	public double getSensitivitydB(Constants ct, double rate) {
		
		double er = Math.pow(10, extinctionDB/10) ;
		double erFactor = (er+1)/(er-1) ;
		double rateGbps = rate/1e9 ;
		double bandwidthGhz = rateGbps * 0.75 ;
		double qBER = 7 ; // for BER = 10^(-12)
		
		// This one is interpolated from TAKASHI's data for noise power at the Ge photodetector (refer to slides)
		double iNoise = 0.0035 * Math.pow(bandwidthGhz, 2) + 0.0655 * Math.pow(bandwidthGhz, 1) + 0.842 ;
		
		double sensitivity = qBER * (darkCurrent+ iNoise) * 1e-6 / responsivity * erFactor  ; // Convert currents to ampere
		
		double sens_dBm = 10*Math.log10(sensitivity) + 30 ; // need to convert the power to dBm

		return sens_dBm;
	}

	
	
}
