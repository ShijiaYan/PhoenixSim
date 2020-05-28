package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;


public class RateAndTotalBandwidthFormat extends AbstractLinkFormat {

    private double rate;
    private double totalRate;

    public RateAndTotalBandwidthFormat(@ParamName(name = "One line rate in Gb/s") double rate,
                                       @ParamName(name = "Total rate in Gb/s") double totalRate) {
        this.rate = rate * 1.0E9D;
        this.totalRate = totalRate * 1.0E9D;
    }

    public double getWavelengthRate() {
        return this.rate;
    }

    public int getNumberOfChannels() {
        return (int) Math.floor(totalRate / rate);
    }

    public boolean isNumberOfChannelFixed() {
        return true;
    }
}