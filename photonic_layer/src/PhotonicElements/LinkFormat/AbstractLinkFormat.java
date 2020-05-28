package PhotonicElements.LinkFormat;

import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public abstract class AbstractLinkFormat {
	
	public abstract double getWavelengthRate(); // in bit/sec
	public abstract int getNumberOfChannels();
	
	public double getAggregateRateInGbs() {
//		return getWavelengthRate()*getNumberOfChannels()/1e9d;
		return getWavelengthRate()*getNumberOfChannels()/1e9;
	}
	
	public abstract double getTotalRateGbps() ;
	
	/**
	 * Formats are assumed valid by default, but overriding class can
	 * override this method to declare a combination of parameters invalid
	 * @return
	 */
	public boolean isValid() {
		return true;
	}
	
	public abstract boolean isNumberOfChannelFixed();


	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Number of channels", getNumberOfChannels()+"",
								"Channel rate (Gb/s)", getWavelengthRate()/1e9d+"",
								"Total rate (Gb/s)", getTotalRateGbps()+"");
	}
	public Rate getAggregateRate() {
		return new Rate((long)(getAggregateRateInGbs()*1e9), 1e6);
	}
	
	public Rate getWavelengthRATE() {
		return new Rate((long) getWavelengthRate(), 1e6);
	}
	/**
	 * This method throws by default an extension. *Configurable* formats
	 * should override it to let the number of wavelength configurable
	 * @param wavelengths
	 */
	public void setNumberOfWavelengths(int wavelengths) {
		throw new IllegalStateException("Cannot set the number of wavlengths with a fixed wavelength scheme");	
	}	

}
