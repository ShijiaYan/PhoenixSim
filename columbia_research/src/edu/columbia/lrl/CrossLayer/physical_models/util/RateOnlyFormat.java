//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;


public class RateOnlyFormat extends AbstractLinkFormat {

	private double rate;
	private int wavelengths = 0;

	public RateOnlyFormat(@ParamName(name = "Line Rate (Gbps)", default_ = "") double rateInGbs) {
		this.rate = rateInGbs * 1.0E9D;
	}

	public double getWavelengthRate() {
		return this.rate;
	}

	public int getNumberOfChannels() {
		return this.wavelengths;
	}

	public void setNumberOfWavelengths(int wavelengths) {
		this.wavelengths = wavelengths;
	}

	public boolean isNumberOfChannelFixed() {
		return this.wavelengths > 0;
	}
}
