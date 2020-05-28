package edu.columbia.lrl.CrossLayer.physical_models;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.ad_hoc.InterfaceWaveguideLengthModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractLaserModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.DefaultLaserModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.AbstractSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.signaling.WDMSignallingModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;

import java.util.Map;

@SuppressWarnings("unused")
public class PhysicalParameterAndModelsSet {

    private Constants constants;
    private AbstractSignallingModel signallingModel;
    private InterfaceWaveguideLengthModel interfaceWaveguideLengthModel;
    private AbstractLaserModel laserModel;
    private double maxOptPowerdBm;
    private double waveguideLoss;
    private double couplerLoss;
    private double bendLoss;
    private double crossingLoss;
    private double junctionLoss;
    private double defaultSingleRingTTPower;
    private int ber;

    public PhysicalParameterAndModelsSet(@ParamName(name = "Constants") Constants constants,
                                         @ParamName(name = "Signaling Model", defaultClass_ =
                                                 WDMSignallingModel.class) AbstractSignallingModel signallingModel,
                                         @ParamName(name = "Laser Model", defaultClass_ = DefaultLaserModel.class) AbstractLaserModel laserModel,
                                         @ParamName(name = "Element Positioning Model") InterfaceWaveguideLengthModel interfaceWaveguideLengthModel,
                                         @ParamName(name = "Maximum Aggregated Optical Power(dBm)", default_ = "20") double maxOptPowerdBm,
                                         @ParamName(name = "Default Ring Tuning/Trimming Power(mW)", default_ = "1") double defaultSingleRingTTPower,
                                         @ParamName(name = "Waveguide Loss(dB/cm)", default_ = "1") double waveguideLoss,
                                         @ParamName(name = "Coupler Loss(dB)", default_ = "1") double couplerLoss,
                                         @ParamName(name = "Waveguide Bending loss(dB/bend)", default_ = "0.02") double bendLoss,
                                         @ParamName(name = "Y Junction Loss(dB)", default_ = "0.28") double junctionLoss,
                                         @ParamName(name = "Waveguide Crossing Loss(dB)", default_ = "0.028") double crossingLoss,
                                         @ParamName(name = "BER Index(10^-x)", default_ = "9") int ber) {
        this.constants = constants;
        this.signallingModel = signallingModel;
        this.interfaceWaveguideLengthModel = interfaceWaveguideLengthModel;
        this.maxOptPowerdBm = maxOptPowerdBm;
        this.laserModel = laserModel;
        this.defaultSingleRingTTPower = defaultSingleRingTTPower;
        this.waveguideLoss = waveguideLoss;
        this.couplerLoss = couplerLoss;
        this.bendLoss = bendLoss;
        this.junctionLoss = junctionLoss;
        this.crossingLoss = crossingLoss;
        this.ber = ber;
    }

    public Map<String, String> getAllParameters() {
        Map<String, String> map = new SimpleMap<>();
        map.put("Waveguide loss (dB/cm)", String.valueOf(this.waveguideLoss));
        map.put("Coupler loss (dB)", String.valueOf(this.couplerLoss));
        map.put("Default ring TT power", String.valueOf(this.defaultSingleRingTTPower));
        map.put("Target BER", String.valueOf(this.ber));
        map.putAll(this.constants.getAllParameters());
        map.putAll(this.laserModel.getAllParameters());
        map.putAll(this.signallingModel.getAllParameters());
        return map;
    }

    public static double mWtodBm(double mw) {
        return 10.0D * Math.log10(mw);
    }

    public static double dBmTomW(double dBm) {
        return Math.pow(10.0D, dBm / 10.0D);
    }

    public String toString() {
        return "Parameterizable device set";
    }

    public InterfaceWaveguideLengthModel getInterfaceWaveguideLengthModel() {
        return this.interfaceWaveguideLengthModel;
    }

    public int getBer() {
        return this.ber;
    }

    public Constants getConstants() {
        return this.constants;
    }

    public AbstractLaserModel getLaserModel() {
        return this.laserModel;
    }

    public AbstractSignallingModel getSignallingModel() {
        return this.signallingModel;
    }

    public double getMaxOptPowerdBm() {
        return this.maxOptPowerdBm;
    }

    public double getCouplerLoss() {
        return this.couplerLoss;
    }

    public double getBendLoss() {
        return this.bendLoss;
    }

    public double getCrossingLoss() {
        return this.crossingLoss;
    }

    public double getDefaultSingleRingTTPower() {
        return this.defaultSingleRingTTPower;
    }

    public double getDefaultSingleRingTTPowerMW() {
        return this.defaultSingleRingTTPower;
    }

    public double getJunctionLoss() {
        return this.junctionLoss;
    }

    public double getWaveguideLoss() {
        return this.waveguideLoss;
    }

    public PowerPenalty getWaveguidePenalty() {
        return new PowerPenalty("Waveguide", "link", this.waveguideLoss);
    }

    public PowerPenalty getCouplerPenalty() {
        return new PowerPenalty("Couplers", "link", this.couplerLoss);
    }

    public PowerPenalty getBendPenalty() {
        return new PowerPenalty("Bendings", "link", this.bendLoss);
    }

    public PowerPenalty getCrossingPenalty() {
        return new PowerPenalty("Crossings", "link", this.crossingLoss);
    }

    public double getMaximumAggregatedOpticalPower() {
        return this.maxOptPowerdBm;
    }

    public PowerConsumption getMinimalSingleLaserConsumption(AbstractLinkFormat linkFormat,
                                                             LayoutWorseCaseProperties properties) {
        return this.getMinimalSingleLaserConsumption(this.getLaserModel(), this.getSignallingModel(), linkFormat,
                properties);
    }

    public PowerConsumption getMinimalSingleLaserConsumption(AbstractLaserModel laserModel,
                                                             AbstractSignallingModel signalingModel,
															 AbstractLinkFormat linkFormat,
															 LayoutWorseCaseProperties properties) {
        double minimal_dBm =
				signalingModel.getReceiverSensitivity(this, linkFormat) + properties.getTotalPowerPenalty();
        double mw = dBmTomW(minimal_dBm);
        return this.getSingleLaserConsumption(laserModel, mw, linkFormat.getNumberOfChannels());
    }

    public PowerConsumption getSingleLaserConsumption(AbstractLaserModel laserModel,
                                                      double inputLaserPowerPerWavelength_mW, int nbChannels) {
        double laserPower_mW = laserModel.getLaserWallPlugConsumption(inputLaserPowerPerWavelength_mW, nbChannels);
        return new PowerConsumption("Laser", false, true, true, laserPower_mW);
    }
}
