package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

import java.util.Map;

public class OOK_DepletionDriverPowerModel_65nm extends AbstractRingPNJunctionDriverPowerModel {


    public double getEnergyPJperBit(double voltage, double capacitance, AbstractLinkFormat linkFormat) {
        double VDD = 1.2; // supply voltage for 65nm CMOS
        double refCapacitance = 50; // in femto Farad. This is the modulator capacitance in Robert's paper

        double rate = linkFormat.getWavelengthRate();
        double driverSlope = 1.4e-23; // grows as V^3 , for VDD = 1.2 V (65nm CMOS tech)
        double driverConst = 8.4e-14; // grows as V^2 , for VDD = 1.2 V (65nm CMOS tech)

        double modifiedDriverSlope =
                driverSlope * Math.abs(Math.pow(voltage / (2 * VDD), 2)) * (capacitance / refCapacitance);
        double modifiedDriverConst =
                driverConst - refCapacitance * 1e-15 * Math.pow(2 * VDD, 2) / 4 + capacitance * 1e-15 * Math.pow(voltage, 2) * 0.25;

        double EnergyJperBit = modifiedDriverSlope * rate + modifiedDriverConst;
        return EnergyJperBit * 1e12;
    }


    @Override
    public double getAverageConsumption(double voltage, double capacitance, AbstractLinkFormat linkFormat) {
        double powerMW = getEnergyPJperBit(voltage, capacitance, linkFormat) * linkFormat.getWavelengthRate() / 1e9;
        // This is for just one driver
        return powerMW;
    }

    @Override
    public Map<String, String> getAllParameters() {
        return new SimpleMap<>();
    }


}
