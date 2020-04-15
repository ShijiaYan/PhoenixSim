package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation;

import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class Basic_SincSquare_Lorentzian_RingTruncationModel extends
		Abstract_SincSquare_Lorentzian_TruncationModel {

	@Override
	public double getPowerPenalty(Constants ct, double rate, double filterQ) {
		
		double FWHM = ct.getCenterFrequency()/(filterQ) ;
		double alpha = FWHM / (2 * rate) ;
		
		double eyeClosure = 1 - 2* Math.exp(-2*Math.PI*alpha) ;
		double trunPenaltydB = -20*Math.log10(eyeClosure) ;
		
		return trunPenaltydB ;
	}

}
