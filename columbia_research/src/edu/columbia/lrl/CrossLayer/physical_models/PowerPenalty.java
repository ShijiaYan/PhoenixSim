package edu.columbia.lrl.CrossLayer.physical_models;

import java.util.ArrayList;


public class PowerPenalty {

    private double multiplier;
    private double value;
    private String name;
    private String location;
    public static final String CROSSTALK = "Crosstalk";
    public static final String INSERTION_LOSS = "Insertion loss";
    public static final String OOK = "OOK";
    public static final String ER = "Extinction ratio";
    public static final String TRUNCATION = "Truncation";
    public static final String JITTER = "Jitter";
    public static final String DUTY_CYCLE = "Duty cycle";
    public static final String PASSIVE_INSERTION_LOSS = "Passive insertion loss";

    public PowerPenalty(String name, String location, double value) {
        this.name = name;
        this.location = location;
        this.value = value;
        this.multiplier = 1.0D;
    }

    public PowerPenalty multiply(int factor) {
        this.multiplier *= factor;
        return this;
    }

    public PowerPenalty multiply(double factor) {
        this.multiplier *= factor;
        return this;
    }

    public double getTotalPowerPenalty() {
        return this.value * this.multiplier;
    }

    public double getIndividualPowerPenalty() {
        return this.value;
    }

    public static void multiply(ArrayList<PowerPenalty> list, int factor) {

        for (PowerPenalty pp : list) {
            pp.multiply(factor);
        }

    }

    public String getName() {
        return this.name;
    }

    public boolean isMultiplied() {
        return this.multiplier != 1.0D;
    }

    public String getCategory() {
        return this.location;
    }
}
