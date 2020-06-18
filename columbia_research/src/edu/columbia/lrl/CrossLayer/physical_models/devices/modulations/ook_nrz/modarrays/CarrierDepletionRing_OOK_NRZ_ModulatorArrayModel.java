package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.OOK_DepletionDriverPowerModel_28nm;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.Abstract_OOK_NRZ_Receiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.OOK_NRZ_Receiver_RobertPolster;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.QandERRingModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

import java.util.ArrayList;
import java.util.Map;


public class CarrierDepletionRing_OOK_NRZ_ModulatorArrayModel extends AbstractRingBased_OOK_NRZ_ModulatorArrayModel {

    private double q;
    private double referenceER;
    private double relResShift;
    private double capacitance;
    private double maxDriveVoltage;
    private double effIndex;

    public CarrierDepletionRing_OOK_NRZ_ModulatorArrayModel(
            @ParamName(name = "Receiver", defaultClass_ = OOK_NRZ_Receiver_RobertPolster.class) Abstract_OOK_NRZ_Receiver ookReceiver,
            @ParamName(name = "Modulator Ring", defaultClass_ =
                    CarrierDepletionRing_OOK_NRZ_ModulatorArrayModel.DefaultDepletionModulatorRing.class) AbstractRingModel absRing,
            @ParamName(name = "Power Model", defaultClass_ = OOK_DepletionDriverPowerModel_28nm.class) AbstractRingPNJunctionDriverPowerModel powerModel,
            @ParamName(name = "Desired Shift(relative to half spacing, -1 for max)", default_ = "-1") double resShift,
            @ParamName(name = "Capacitance(fF)", default_ = "65") double capacitance,
            @ParamName(name = "Maximum Driving Voltage(V)", default_ = "-5") double maxDriveVoltage) {
        super(ookReceiver, powerModel);
        this.q = absRing.getQ();
        this.referenceER = absRing.getER();
        this.relResShift = resShift;
        this.capacitance = capacitance;
        this.effIndex = absRing.getGroupIndex();
        this.maxDriveVoltage = maxDriveVoltage;
    }

    public Map<String, String> getRingBasedArrayParameters() {
        Map<String, String> map = new SimpleMap<>();
        map.put("Modulator Q", "" + this.getModulatorPassiveQ());
        map.put("Reference ER(dB)", "" + this.referenceER);
        map.put("Relative resonance shift", "" + this.relResShift);
        map.put("Maximum Drive Voltage(V)", "" + this.getMaxDriveVoltage());
        return map;
    }

    public ArrayList<PowerPenalty> getPassbyModulationBankPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                                         AbstractLinkFormat linkFormat) {
        return MoreArrays
                .getArrayList(this.getInsertionLossPowerPenalties(modelSet, linkFormat));
    }

    public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
            PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
        Constants ct = modelSet.getConstants();
        double resLambda = ct.getCenterWavelength() * 1.0E9D;
        int wavelengths = linkFormat.getNumberOfChannels();
        double channelSpacing = ct.getFullFSR() / (double) wavelengths * 1.0E9D;
        double[] calc = this.calculateQPrime_resShift_erWithCarrier(ct, linkFormat);
        double Qprime = calc[0];
        double resShift = calc[1];
        double extinctionWithCarriers = calc[2];
        double powON = getTransmission(resLambda, resLambda + resShift, Qprime, extinctionWithCarriers);
        double powOFF = getTransmission(resLambda, resLambda, this.q, this.referenceER);
        double er = powON / powOFF;
        double erPP = -10 * Math.log10((er - 1) / (er + 1));
        double ookPP = -10 * Math.log10((er + 1) / (2 * er));
        PowerPenalty erPP_ = new PowerPenalty("Extinction ratio", "Modulator", erPP);
        PowerPenalty ookPP_ = new PowerPenalty("OOK", "Modulator", ookPP);
        PowerPenalty insertionLoss = this.getInsertionLossPowerPenalties(modelSet, linkFormat);
        double xtalk = this.getTransmission(resLambda - channelSpacing, resLambda + resShift, Qprime, this.referenceER);
        double xtalkPP = -5 * Math.log10(xtalk);
        PowerPenalty xtalkPenalty = new PowerPenalty("Crosstalk", "Modulator", xtalkPP);
        Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<>();
        pair.setFirst(er);
        pair.setSecond(MoreArrays.getArrayList(erPP_, ookPP_, xtalkPenalty, insertionLoss));
        return pair;
    }

    public double getResShift(Constants ct, AbstractLinkFormat linkFormat) {
        int wavelengths = linkFormat.getNumberOfChannels();
        double channelSpacing = ct.getFullFSR() / (double) wavelengths * 1.0E9D;
        double resShift;
        if (this.relResShift >= 0 && this.relResShift <= 1) {
            resShift = channelSpacing / 2 * this.relResShift;
        } else {
            resShift = channelSpacing / 2;
            if (resShift > 2) {
                resShift = 2;
            }
        }

        double maxResShiftNm = this.getMaxResonanceShiftNm(ct);
        return Math.min(resShift, maxResShiftNm);
    }

    public double[] calculateQPrime_resShift_erWithCarrier(Constants ct, AbstractLinkFormat linkFormat) {
        double resHz = ct.getCenterFrequency();
        double resLambda = ct.getCenterWavelength() * 1E9;
        double resShift = this.getResShift(ct, linkFormat);
        double t0 = Math.pow(10, -this.referenceER / 10);
        double FWHM = resHz / this.q;
        double tau_i = 2 / (Math.PI * FWHM * (1.0D + Math.sqrt(t0)));
        double tau_c = (1 + Math.sqrt(t0)) / (1.0D - Math.sqrt(t0)) * tau_i;
        double alpha = 2 * this.effIndex / (ct.getSpeedOfLight() * 100 * tau_i);
        double Gamma = 0.8D;
        double DeltaNSilicon = this.effIndex / Gamma * resShift / resLambda;
        double deltaAlpha = this.getDeltaAlphaFromDeltaN(DeltaNSilicon);
        double alphaPrime = alpha + deltaAlpha;
        double tau_i_prime = 2.0D * this.effIndex / (ct.getSpeedOfLight() * 100.0D * alphaPrime);
        double FWHMPrime = 0.3183098861837907D * (1.0D / tau_i_prime + 1.0D / tau_c);
        double resHzPrime = ct.getSpeedOfLight() / ((resLambda + resShift) * 1.0E-9D);
        double Qprime = resHzPrime / FWHMPrime;
        double t0Prime = Math.pow((1.0D - tau_i_prime / tau_c) / (1.0D + tau_i_prime / tau_c), 2.0D);
        double extinctionWithCarriers = -10.0D * Math.log10(t0Prime);
        return new double[]{Qprime, resShift, extinctionWithCarriers};
    }

    public PowerPenalty getInsertionLossPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                       AbstractLinkFormat linkFormat) {
        Constants ct = modelSet.getConstants();
        int wavelengths = linkFormat.getNumberOfChannels();
        double channelSpacing = ct.getFullFSR() / (double) wavelengths * 1.0E9D;
        double resLambda = ct.getCenterWavelength() * 1.0E9D;
        double[] calc = this.calculateQPrime_resShift_erWithCarrier(ct, linkFormat);
        double Qprime = calc[0];
        double resShift = calc[1];
        double extinctionWithCarriers = calc[2];
        double[] resLambdas = new double[wavelengths];
        double transmission = 1.0D;

        for (int i = 0; i < wavelengths / 2; ++i) {
            resLambdas[i] = resLambda + (double) i * channelSpacing;
            if (i == 0) {
                resLambdas[i] = resLambda + resShift;
                transmission *= this.getTransmission(resLambda, resLambdas[i], Qprime, extinctionWithCarriers);
            } else {
                transmission *= Math.pow(this.getTransmission(resLambda, resLambdas[i], this.q, this.referenceER),
                        2.0D);
            }
        }

        double insertionLoss = -10.0D * Math.log10(transmission);
        return new PowerPenalty("Insertion loss", "Modulator", insertionLoss);
    }

    public double getTransmission(double lambda, double resLambda, double qFactor, double resExtinctionDB) {
        double FWHM = resLambda / qFactor;
        double resExtinction = Math.pow(10.0D, -resExtinctionDB / 10.0D);
        final double pow = Math.pow(2.0D * (lambda - resLambda) / FWHM, 2.0D);
        double trans = (pow + resExtinction)
                / (pow + 1.0D);
        return trans;
    }

    public double getDrivingCapacitance() {
        return this.capacitance;
    }

    public double getDrivingVoltage(Constants ct, AbstractLinkFormat linkFormat) {
        double resShiftNm = this.getResShift(ct, linkFormat);
        double Gamma = 0.8D;
        double Na = 5.0E17D;
        double Nd = 1.0E18D;
        double eps_si = 12.0409D;
        double eps0 = 8.85E-14D;
        double nf = 4.25E-21D;
        double q = 1.6E-19D;
        double ng = 4.2D;
        double w = 5.0E-7D;
        double Vbi = 0.7D;
        double resWavelengthNm = ct.getCenterWavelength() * 1.0E9D;
        double A = resWavelengthNm * Gamma * nf / ng * 1.0D / (w * 100.0D);
        double B = Math.sqrt(2.0D * eps_si * eps0 / q * Na * Nd / (Na + Nd));
        double V = Vbi - Math.pow(resShiftNm / (A * B) + Math.sqrt(Vbi), 2.0D);
        return V;
    }

    public double getMaxResonanceShiftNm(Constants ct) {
        double Gamma = 0.8;
        double Na = 5.0E17;
        double Nd = 1.0E18;
        double eps_si = 12.0409;
        double eps0 = 8.85E-14;
        double nf = 4.25E-21;
        double q = 1.6E-19;
        double ng = 4.2;
        double w = 5.0E-7;
        double Vbi = 0.7;
        double resWavelengthNm = ct.getCenterWavelength() * 1.0E9D;
        double A = resWavelengthNm * Gamma * nf / ng * 1.0D / (w * 100.0D);
        double B = Math.sqrt(2.0D * eps_si * eps0 / q * Na * Nd / (Na + Nd));
        double maxResShiftNm = A * B * (Math.sqrt(Vbi - this.maxDriveVoltage) - Math.sqrt(Vbi));
        return maxResShiftNm;
    }

    private double getDeltaAlphaFromDeltaN(double DeltaN) {
        return 2347.4 * DeltaN;
    }

    public double getMaxDriveVoltage() {
        return this.maxDriveVoltage;
    }

    public double getModERdB(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
        Constants ct = modelSet.getConstants();
        double resLambda = ct.getCenterWavelength() * 1.0E9D;
        double[] calc = this.calculateQPrime_resShift_erWithCarrier(ct, linkFormat);
        double Qprime = calc[0];
        double resShift = calc[1];
        double extinctionWithCarriers = calc[2];
        double powON = this.getTransmission(resLambda, resLambda + resShift, Qprime, extinctionWithCarriers);
        double powOFF = this.getTransmission(resLambda, resLambda, this.q, this.referenceER);
        double er = powON / powOFF;
        double modERdB = 10.0D * Math.log10(er);
        return modERdB;
    }

    public double getModulatorPassiveQ() {
        return this.q;
    }

    public static class DefaultDepletionModulatorRing extends QandERRingModel {

        public DefaultDepletionModulatorRing(@ParamName(name = "Modulator Q", default_ = "12000") double q,
                                             @ParamName(name = "Effective Index", default_ = "4.393") double effIndex,
                                             @ParamName(name = "Modulator ER in dB", default_ = "20") double er) {
            super(q, effIndex, er);
        }
    }

}
