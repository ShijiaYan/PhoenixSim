
package PhotonicElements.LinkFormat;

import ch.epfl.general_libraries.clazzes.ParamName;

public class RateAndTotalBandwidthFormat extends AbstractLinkFormat {
	
	private double rate;
	private double totalRate;
	
	public RateAndTotalBandwidthFormat(@ParamName(name="One line rate in Gb/s") double rate, 
			@ParamName(name="Total rate in Gb/s") double totalRate) {
		this.rate = rate*1e9;
		this.totalRate = totalRate*1e9;
	}

	@Override
	public double getWavelengthRate() {
		return rate;
	}

	@Override
	public int getNumberOfChannels() {
		return (int)Math.floor(totalRate/rate);
	}

	@Override
	public boolean isNumberOfChannelFixed() {
		return true;
	}

	@Override
	public double getTotalRateGbps() {
		return (totalRate/1e9) ;
	}

}
