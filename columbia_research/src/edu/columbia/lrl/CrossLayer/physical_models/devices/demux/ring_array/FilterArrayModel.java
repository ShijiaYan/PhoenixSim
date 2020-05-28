package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.PolynomBased_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.RingResonance;


public class FilterArrayModel extends AbstractRingBasedFilterArrayModel {

    double passiveDropIL;
    double ringRadius;
    double Neff;

    public FilterArrayModel(
            @ParamName(name = "Passive drop insertion loss (dB)", default_ = "0.5") double passiveDropIL,
            @ParamName(name = "Ring radius (m)", default_ = "1.87e-6") double ringRadius,
            @ParamName(name = "Effective Index", default_ = "4.393") double Neff,
            @ParamName(name = "Truncation model to use", defaultClass_ =
					PolynomBased_SincSquare_Lorentzian_TruncationModel.class) Abstract_SincSquare_Lorentzian_TruncationModel truncModel,
            @ParamName(name = "Xtalk model", defaultClass_ = MeisamJLTXtalkModel.class) AbstractXtalkPPModel xtalkModel,
            @ParamName(name = "Track details", default_ = "false") boolean trackDetails) {
        super(trackDetails, truncModel, xtalkModel);
        this.passiveDropIL = passiveDropIL;
        this.ringRadius = ringRadius;
        this.Neff = Neff;
    }

    public Map<String, String> getAllParameters() {
        Map<String, String> map = super.getAllParameters();
        map.put("Passive insertion loss (dB)", String.valueOf(this.passiveDropIL));
        map.put("Ring radius (m)", String.valueOf(this.ringRadius));
        map.put("Filter Effective Index", String.valueOf(this.Neff));
        return map;
    }

    public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                     AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
        Constants ct = modelSet.getConstants();
        double v0 = ct.getCenterFrequency();
        double dataRate = linkFormat.getWavelengthRate();
        double Q_min = v0 / dataRate / 3.0D;
        double Q_max = v0 / dataRate / 0.7D;
        double dQ = (Q_max - Q_min) / 99.0D;
        int size = (int) Math.ceil((Q_max - Q_min) / dQ);
        double[] Q_vec = new double[size];

        for (int i = 0; i < Q_vec.length; ++i) {
            Q_vec[i] = Q_min + (double) i * dQ;
        }

        double[] xtalk_pp = new double[size];
        double[] trunc_pp = new double[size];
        double[] insertionLoss = new double[size];
        Arrays.fill(insertionLoss, this.passiveDropIL);

        int bestIndex;
        for (bestIndex = 0; bestIndex < Q_vec.length; ++bestIndex) {
            double Q_demux = Q_vec[bestIndex];
            double[] results = this.calculateForQ(modelSet, linkFormat, Q_demux, modulationER);
            xtalk_pp[bestIndex] = results[0];
            trunc_pp[bestIndex] = results[1];
        }

        bestIndex = this.storeData(linkFormat, insertionLoss, Q_vec, xtalk_pp, trunc_pp, ex);
        PowerPenalty trunc = new PowerPenalty("Truncation", "Demux", trunc_pp[bestIndex]);
        PowerPenalty xtalk = new PowerPenalty("Crosstalk", "Demux", xtalk_pp[bestIndex]);
        PowerPenalty il = new PowerPenalty("Insertion loss", "Demux", this.passiveDropIL);
        return MoreArrays.getArrayList(trunc, xtalk, il);
    }

    protected double[] calculateForQ(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat,
                                     double Q_demux, double modulationER) {
        Constants ct = modelSet.getConstants();
        int wavelengths = linkFormat.getNumberOfChannels();
        double rate = linkFormat.getWavelengthRate();
        double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
        double fsrHz = ct.getSpeedOfLight() / (2 * Math.PI * this.ringRadius * this.Neff);
        double v0 = ct.getCenterFrequency();
        RingResonance resonance = new RingResonance(v0, fsrHz, Q_demux);
        double[] v = resonance.getFrequencies();
        Pair<Double, Integer> p = MoreArrays.maxAndIndex(resonance.getResonance());
        double center_v = v[p.getSecond()];
        double add_xtalk1 = 0.0D;

        for (int j = (int) -Math.ceil(wavelengths / 2); j <= (int) Math
                .ceil(wavelengths / 2); ++j) {
            if (j != 0) {
                double p1 = RingResonance.getResonance(v0, fsrHz, Q_demux, center_v + (double) j * channelSpacing);
                add_xtalk1 += p1;
            }
        }

        double xtalk_pp = this.xtalkModel.getXtalkPP_DB(add_xtalk1, modelSet.getBer(), modulationER);
        double pp_trunc = this.truncationPowerPenalty.getPowerPenalty(ct, rate, Q_demux);
        return new double[]{xtalk_pp, pp_trunc};
    }

    public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
                                                             AbstractLinkFormat linkFormat) {
        ArrayList<PowerConsumption> pc = new ArrayList<>(1);
        PowerConsumption p2 = new PowerConsumption("Demux rings", false, true, true,
                modelSet.getDefaultSingleRingTTPowerMW());
        pc.add(p2);
        return pc;
    }

    public boolean hasThroughCapability() {
        return false;
    }

    public ArrayList<PowerPenalty> getPassByPowerPenalties(PhysicalParameterAndModelsSet modelSet,
                                                           AbstractLinkFormat linkFormat) {
        throw new IllegalStateException("To be implemented");
    }
}
