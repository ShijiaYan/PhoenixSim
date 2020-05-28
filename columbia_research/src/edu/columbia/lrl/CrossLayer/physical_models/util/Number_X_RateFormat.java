//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;


public class Number_X_RateFormat extends AbstractLinkFormat {

    private int wavelengths;
    private double rate;

    public Number_X_RateFormat(@ParamName(name = "Number of channels") int wavelengths,
                               @ParamName(name = "Channel rate (Gb/s)") double rateInGbs) {
        this.wavelengths = wavelengths;
        this.rate = rateInGbs * 1.0E9D;
    }

    public double getWavelengthRate() {
        return this.rate;
    }

    public int getNumberOfChannels() {
        return this.wavelengths;
    }

    public boolean isNumberOfChannelFixed() {
        return this.wavelengths > 0;
    }

    @Override
    public void setNumberOfWavelengths(int wavelengths) {
        this.wavelengths = wavelengths;
    }
}
