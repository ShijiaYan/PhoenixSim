//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.util;

import java.util.Map;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;


public abstract class AbstractLinkFormat {

	public AbstractLinkFormat() {
	}

	public abstract double getWavelengthRate();

	public abstract int getNumberOfChannels();

	public double getAggregateRateInGbs() {
		return this.getWavelengthRate() * (double) this.getNumberOfChannels() / 1.0E9D;
	}

	public boolean isValid() {
		return true;
	}

	public abstract boolean isNumberOfChannelFixed();

	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap(new String[] { "Number of channels", String.valueOf(this.getNumberOfChannels()),
				"Channel rate (Gb/s)", String.valueOf(this.getWavelengthRate() / 1.0E9D) });
	}

	public Rate getAggregateRate() {
		return new Rate((long) (this.getAggregateRateInGbs() * 1.0E9D), 1000000.0D);
	}

	public Rate getWavelengthRATE() {
		return new Rate((long) this.getWavelengthRate(), 1000000.0D);
	}

	public void setNumberOfWavelengths(int wavelengths) {
		throw new IllegalStateException("Cannot set the number of wavlengths with a fixed wavelength scheme");
	}
}
