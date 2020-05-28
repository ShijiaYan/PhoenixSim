package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

import java.util.Map;

public class OOK_DepletionDriverPowerModel_Custom extends AbstractRingPNJunctionDriverPowerModel {

    private double driverSlope;
    private double driverConst;
    private double supplyVoltage;

    public OOK_DepletionDriverPowerModel_Custom(@ParamName(name = "Slope", default_ = "0.2e-23") double driverSlope,
                                                @ParamName(name = "Constant", default_ = "0.2759e-12") double driverConst,
                                                @ParamName(name = "supplyVoltage", default_ = "1") double supplyVoltage) {
        this.driverSlope = driverSlope;
        this.driverConst = driverConst;
        this.supplyVoltage = supplyVoltage;
    }

    public double getEnergyPJperBit(double voltage, double capacitance, AbstractLinkFormat linkFormat) {

        double VDD = this.supplyVoltage;
        double refCapacitance = 50;

        double rate = linkFormat.getWavelengthRate();
        double slope = this.driverSlope;
        double aConst = this.driverConst * VDD * VDD;

        double modifiedDriverSlope =
                slope * Math.abs(Math.pow(voltage / (2 * VDD), 2)) * (capacitance / refCapacitance);
        double modifiedDriverConst =
                aConst - refCapacitance * 1e-15 * Math.pow(2 * VDD, 2) / 4 + capacitance * 1e-15 * Math.pow(voltage,
                        2) * 0.25;

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
