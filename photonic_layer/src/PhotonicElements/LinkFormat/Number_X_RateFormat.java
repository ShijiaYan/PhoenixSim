package PhotonicElements.LinkFormat;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Number_X_RateFormat extends AbstractLinkFormat {
	
	private int wavelengths;
	private double rate;
	
	public Number_X_RateFormat(
			@ParamName(name="Number of channels") int wavelengths, 
			@ParamName(name="Line rate (Gb/s)") double rateInGbs) {
		this.wavelengths = wavelengths;
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

	@Override
	public boolean isNumberOfChannelFixed() {
		return wavelengths > 0;
	}

	@Override
	public double getTotalRateGbps() {
		// TODO Auto-generated method stub
		return rate/1e9 * wavelengths;
	}

}
