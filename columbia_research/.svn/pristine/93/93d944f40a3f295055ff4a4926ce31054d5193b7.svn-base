package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation;

import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class ResidualsBased_SincSquare_Lorentzian_Model extends Abstract_SincSquare_Lorentzian_TruncationModel {
	
	// Added by Meisam : Based on the Equation I calculated for OOK NRZ
	
	public double getPowerPenalty(Constants ct, double rate, double filterQ){
		
		double dv = ct.getCenterFrequency() / (filterQ * rate);
		
		double trunPenalty= 1 - (1 - Math.exp(-Math.PI*dv))/(Math.PI*dv) ;
		double trunPenaltydB = -10 * Math.log10(trunPenalty) ;
		
		return trunPenaltydB ;
	}
	
	
	
}
