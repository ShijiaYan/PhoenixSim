package edu.columbia.lrl.CrossLayer.physical_models.util;

public class Number_X_RateFormatWithLimit extends Number_X_RateFormat {
	
	private double limit;

	public Number_X_RateFormatWithLimit(int wavelengths, double rateInGbs, double limitInGbs) {
		super(wavelengths, rateInGbs);
		this.limit = limitInGbs*1e9;
	}
	
	/**
	 * Formats are assumed valid by default, but overriding class can
	 * override this method to declare a combination of parameters invalid
	 * @return
	 */
	public boolean isValid() {
		return getWavelengthRate()*getNumberOfChannels() < limit;
	}	

}
