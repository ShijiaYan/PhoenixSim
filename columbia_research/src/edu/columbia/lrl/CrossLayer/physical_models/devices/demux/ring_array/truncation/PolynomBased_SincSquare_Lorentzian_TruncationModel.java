package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation;

import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class PolynomBased_SincSquare_Lorentzian_TruncationModel extends
		Abstract_SincSquare_Lorentzian_TruncationModel {

	@Override
	public double getPowerPenalty(Constants ct, double rate, double filterQ) {
		
		double dv = ct.getCenterFrequency() / (filterQ * rate);
		
		if (dv > 3) {
			return 0.04;
		} else {
			return 17.228  
					-42.29*dv
					+44.443*Math.pow(dv,2)		
					-25.148*Math.pow(dv,3)
					+7.9945*Math.pow(dv,4)
					-1.3447*Math.pow(dv,5)
					+0.093144*Math.pow(dv,6);
		}
	}

}
