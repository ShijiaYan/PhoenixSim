package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.Abstract_OOK_SERDES_PowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

import java.util.ArrayList;
import java.util.Map;

public class OOK_NRZ_Receiver_Integrating_28nm extends Abstract_OOK_NRZ_Receiver {
    private double polarizationLoss;

    public OOK_NRZ_Receiver_Integrating_28nm(
            @ParamName(name = "Polarization loss(dB)", default_ =
                    "0.1") double polarizationLoss,
            @ParamName(name = "SERDES model") Abstract_OOK_SERDES_PowerModel serdes) {
        super(0, serdes);
        this.polarizationLoss = polarizationLoss;
    }

    @Override
    public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                                 AbstractLinkFormat linkFormat) {
        PowerPenalty polarizationPenalty = new PowerPenalty("Polarization loss", "Demodulator/Receiver",
                this.polarizationLoss);
        return MoreArrays.getArrayList(polarizationPenalty);
    }

    @Override
    public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format) {
        double rateGbps = format.getWavelengthRate() * 1e-9;
        if (rateGbps > 30) throw new WrongExperimentException("Bit rate exceeds receiver capability.");
        else {
            return -18.784087 - 0.1386 * rateGbps + 0.01464667867 * rateGbps * rateGbps;
        }
    }

    public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(double opticalPowerAtReceiverdBm,
                                                                                    PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
        ArrayList<PowerConsumption> superPowerCons = super.getDemodulationAndReceptionPowerConsumptions(
                opticalPowerAtReceiverdBm, modelSet, linkFormat);

        double rateGbps = linkFormat.getWavelengthRate() * 1e-9;
        if (rateGbps > 30) throw new WrongExperimentException("Bit rate exceeds receiver capability.");
        else {
            double powCon = 0.1374 * rateGbps + 0.6979;
            PowerConsumption pc = new PowerConsumption("Receiver", false, false, true, powCon);
            superPowerCons.add(pc);
            return superPowerCons;
        }
    }

    @Override
    public Map<String, String> getAllReceiverParameters() {
        PropertyMap m = new PropertyMap();
        m.put("Polarization loss", this.polarizationLoss);
        return m;
    }
}
