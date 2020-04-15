package edu.columbia.lrl.CrossLayer.physical_models.util;

public class RateOnlyFormat extends AbstractLinkFormat {

	private double rate;
	private int wavelengths = 0;
	
	public RateOnlyFormat(double rateInGbs) {
		this.rate = rateInGbs*1e9d;
	}
	
	@Override
	public double getWavelengthRate() {
		return rate;
	}

	@Override
	public int getNumberOfChannels() {
		return wavelengths;
	}
	
	public void setNumberOfWavelengths(int wavelengths) {
		this.wavelengths = wavelengths;
	}	

	@Override
	public boolean isNumberOfChannelFixed() {
		return (wavelengths > 0);
	}

}
