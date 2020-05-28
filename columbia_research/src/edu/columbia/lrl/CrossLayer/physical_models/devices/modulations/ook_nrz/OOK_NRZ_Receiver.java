package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractReceiverSensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.OOK_SERDES_PowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.receiver_power.OOK_NRZ_ReceiverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.receiver_power.OOK_NRZ_ReceiverPowerModel.Conservative;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.sensitivity.Abstract_OOK_NRZ_SensitivityModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.sensitivity.Ge_OOK_NRZ_ReceiverTakashi;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class OOK_NRZ_Receiver extends Abstract_OOK_NRZ_Receiver {

    private AbstractReceiverSensitivityModel sensitivityModel;
    private double polarizationLoss;
    private OOK_NRZ_ReceiverPowerModel powModel;

    public OOK_NRZ_Receiver(
            @ParamName(name = "Sensitivity Model", defaultClass_ = Ge_OOK_NRZ_ReceiverTakashi.class) Abstract_OOK_NRZ_SensitivityModel sensitivityModel,
            @ParamName(name = "", defaultClass_ = Conservative.class) OOK_NRZ_ReceiverPowerModel powModel,
            @ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
            @ParamName(name = "Jitter Penalty (dB)", default_ = "2") double passiveJitterPenalty,
            @ParamName(name = "SERDES Power Model") OOK_SERDES_PowerModel serdes) {
        super(passiveJitterPenalty, serdes);
        this.sensitivityModel = sensitivityModel;
        this.polarizationLoss = polarizationLoss;
        this.powModel = powModel;
    }

    public double getReceiverSensitivity(PhysicalParameterAndModelsSet modeSet, AbstractLinkFormat format) {
        return this.sensitivityModel.getSensitivitydB(modeSet.getConstants(), format.getWavelengthRate());
    }

    public Map<String, String> getAllReceiverParameters() {
        PropertyMap m = new PropertyMap();
        m.put("Polarization loss", this.polarizationLoss);
        m.putAll(this.sensitivityModel.getAllParameters());
        m.putAll(this.powModel.getAllParameters());
        return m;
    }

    public ArrayList<PowerPenalty> getDemodulationPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                                 AbstractLinkFormat linkFormat) {
        PowerPenalty pola = new PowerPenalty("Polarization loss", "Demodulator/Receiver", this.polarizationLoss);
        return MoreArrays.getArrayList(pola);
    }

    public ArrayList<PowerConsumption> getDemodulationAndReceptionPowerConsumptions(double opticalPowerAtReceiverdBm,
                                                                                    PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
        ArrayList<PowerConsumption> superPowerCons = super.getDemodulationAndReceptionPowerConsumptions(
                opticalPowerAtReceiverdBm, modelSet, linkFormat);
        double powCon = this.powModel.getPowerConsumptionMW(linkFormat.getWavelengthRate());
        superPowerCons.add(new PowerConsumption("Receiver", false, false, true, powCon));
        return superPowerCons;
    }
}
