package PhotonicElements.LinkFormat;

public class RateOnlyFormat extends AbstractLinkFormat {

	private double rate;
	private int wavelengths = 0;
	
	public RateOnlyFormat(double rateInGbs) {
		this.rate = rateInGbs*1e9;
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
		return wavelengths > 0;
	}

	@Override
	public double getTotalRateGbps() {
		return rate/1e9 * wavelengths;
	}

}
