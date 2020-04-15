package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class OOK_SERDES_PowerModel extends Abstract_OOK_SERDES_PowerModel {
	
	private static double[] ckgen_pJWord = {0.5900,
		1.1675,
		1.4638,
		1.6058,
		1.7231,
		1.7650,
		1.7958,
		1.8286,
		1.8428};
	
	private static double[] bufMux_pJBit = {0.0100,
		0.0148,
		0.0181,
		0.0197,
		0.0209,
		0.0215,
		0.0217,
		0.0218,
		0.0220};

	private static int[] gpbs = {2,
		4,
		8,
		12,
		16,
		20,
		24,
		28,
		32};

	public OOK_SERDES_PowerModel () {
		System.out.print("");
	}

	@Override
	public double getPowerConsumptionMW(AbstractLinkFormat linkFormat) {
		// 1. find where we are in gpbs
		double rateInGbs = linkFormat.getWavelengthRate()/1e9;
		
		double[] result = null;
		if (rateInGbs < gpbs[0]) {
			result = interpolate(0,0, rateInGbs);
		} else if (rateInGbs > gpbs[gpbs.length-1]) {
			result = interpolate(gpbs.length-1, gpbs.length-1, rateInGbs);
		} else {
			int index = 0;
			for (int i = 0 ; i < gpbs.length ; i++) {
				if (rateInGbs >= gpbs[i]) {
					index = i;
				}
			}
			result = interpolate(index,index+1, rateInGbs);
		}
		
		double pJBit = result[0]/(double)linkFormat.getNumberOfChannels() + result[1];
		
		double mwWatt = pJBit*linkFormat.getAggregateRateInGbs();
		
		return mwWatt;
	}
	
	// added by Meisam
	public double getEnergyPJperBit(AbstractLinkFormat linkFormat){
		
		double pJBit = getPowerConsumptionMW(linkFormat)/linkFormat.getAggregateRateInGbs() ;
		return pJBit ;
	}


	private double[] interpolate(int i, int j, double rate) {
		if (i == j) {
			return new double[]{ckgen_pJWord[i], bufMux_pJBit[i]};
		}
		double relativeToInterval = (rate - gpbs[i])/(gpbs[j] - gpbs[i]);
		
		double[] result = new double[2];
		
		result[0] = ckgen_pJWord[i] + relativeToInterval*(ckgen_pJWord[j] - ckgen_pJWord[i]);
		result[1] = bufMux_pJBit[i] + relativeToInterval*(bufMux_pJBit[j] - bufMux_pJBit[i]);
		return result;
	}

	
	
	
}
