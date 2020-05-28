//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;


public class Interleaver_Num_X_Rate_Format extends AbstractLinkFormat {

    private int wavelengths;
    private double rate;

    public Interleaver_Num_X_Rate_Format(@ParamName(name = "Number of channels") int wavelengths,
                                         @ParamName(name = "Channel rate (Gbps)") double rateInGbs) {
        this.wavelengths = wavelengths;
        this.rate = rateInGbs * 1.0E9D;
    }

    public double getAggregateRateInGbs() {
        return super.getAggregateRateInGbs();
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
