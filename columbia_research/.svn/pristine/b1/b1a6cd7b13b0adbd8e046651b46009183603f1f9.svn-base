package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class OOK_SERDES_PowerModel_Extrapolate extends Abstract_OOK_SERDES_PowerModel {
	

	public double getEnergyPJperBit(AbstractLinkFormat linkFormat){
		
		double rateGbps = linkFormat.getWavelengthRate()/1e9 ;
		double numWavelengths = linkFormat.getNumberOfChannels() ;
		
/*		if (rateGbps < 2){
			return 0.128 ;
		}*/
		
		// define extrapolation coefficients
		double a = -0.506220757 ;
		double b = 1.140084754 ;
		double c = -0.0002194973244 ;
		double d = -2.013316363 ;
		double e = 0.8165327059 ;
		double f = 1.401713856 ;
		
		double EnergyPJperBit = a * Math.pow(rateGbps, b) * (c * numWavelengths * rateGbps + d)/(e * numWavelengths * rateGbps + f) ;
		
		return EnergyPJperBit;
	}
	
	@Override
	public double getPowerConsumptionMW(AbstractLinkFormat linkFormat) {
		
		double powerMW = getEnergyPJperBit(linkFormat) * linkFormat.getAggregateRateInGbs() ;
		
		return powerMW ;
	}



	
	
}
