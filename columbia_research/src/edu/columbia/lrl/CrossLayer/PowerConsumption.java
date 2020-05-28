package edu.columbia.lrl.CrossLayer;

import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.List;

public class PowerConsumption {

    private boolean isDynamic;
    private boolean isPerInterface;
    private boolean isPerWavelength;
    private boolean isCircuit;

    private double powerInMW;
    private String origin;
    private double factor = 1;

    private SimpleMap<String, Double> factorList = new SimpleMap<>();

    public PowerConsumption(String origin, boolean isDynamic, boolean isPerInterface, boolean isPerWavelength,
                            double powerInMW) {
        this.isDynamic = isDynamic;
        this.isPerInterface = isPerInterface;
        this.isPerWavelength = isPerWavelength;
        this.powerInMW = powerInMW;
        this.origin = origin;
    }

    public void multiply(String factorName, double factor) {
        this.factor *= factor;
        if (factorList.containsKey(factorName)) {
            factorList.replace(factorName, factor);
        } else {
            System.out.println("In multiplication:\nfactorName not found in factorList...\ngenerating new factor pair" +
                    ".");
            factorList.put(factorName, factor);
        }
    }

    public void setPerWavelength(boolean b) {
        isPerWavelength = b;
    }

    public void setCircuit(boolean b) {
        isCircuit = b;
    }

    public String toString() {
        return "Consumption of " + powerInMW + "mW (" + (isDynamic ? "dynamic" : "static")
                + (isPerInterface ? "per interface" : "global")
                + (isPerWavelength ? "per wavelength" : "per link (not per wavelength");
    }

    public static double compute(List<PowerConsumption> consumptions, double circuit, double utilization,
                                 int opticalInterfaces, int wavelengths) {
        double accum = 0;
        for (PowerConsumption con : consumptions) {
            double util;
            double clients;
            int wav;

            if (con.isDynamic) {
                util = utilization;
            } else if (con.isCircuit) {
                util = circuit;
            } else {
                util = 1;
            }
            if (con.isPerInterface) {
                clients = opticalInterfaces;
            } else {
                clients = 1;
            }
            if (con.isPerWavelength) {
                wav = wavelengths;
            } else {
                wav = 1;
            }

            accum += con.powerInMW * con.factor * clients * util * wav;
        }
        return accum;
    }

    public String getOrigin() {
        return origin;
    }

    public static void multiply(List<PowerConsumption> list, String factorName, int factor) {
        for (PowerConsumption con : list) {
            con.multiply(factorName, factor);
        }
    }

    public double compute(double circuit, double utilization, int nbInterfaces, int wavelengths) {
        return compute(MoreArrays.getArrayList(this), circuit, utilization, nbInterfaces, wavelengths);
    }

    public static double dBmTomW(double d) {
        return Math.pow(10, d / 10d);
    }

}
