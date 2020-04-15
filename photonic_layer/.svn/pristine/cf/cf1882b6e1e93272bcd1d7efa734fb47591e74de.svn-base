package NASA.Link;

import PhotonicElements.LinkFormat.AbstractLinkFormat;

/**
 * This is OOK SERDES for 65nm CMOS technology --> Based on Robert's model
 * @author Meisam
 *
 */

public class Serializer {
	
	AbstractLinkFormat linkFormat ;
	
	public Serializer(
			AbstractLinkFormat linkFormat
			){
		this.linkFormat = linkFormat ;
	}

	public double getEnergyPJperBit(){
		
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
	
	public double getPowerConsumptionMW() {
		
		double powerMW = getEnergyPJperBit() * linkFormat.getAggregateRateInGbs() ;
		
		return powerMW ;
	}
	
	
}
