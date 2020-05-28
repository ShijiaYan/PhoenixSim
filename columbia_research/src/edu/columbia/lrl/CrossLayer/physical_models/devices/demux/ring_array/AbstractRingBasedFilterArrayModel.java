//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.AbstractDemux;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public abstract class AbstractRingBasedFilterArrayModel extends AbstractDemux {

    protected boolean trackDetails;
    protected Abstract_SincSquare_Lorentzian_TruncationModel truncationPowerPenalty;
    protected AbstractXtalkPPModel xtalkModel;

    public AbstractRingBasedFilterArrayModel(boolean trackDetails,
                                             Abstract_SincSquare_Lorentzian_TruncationModel truncModel,
                                             AbstractXtalkPPModel xtalkModel) {
        this.trackDetails = trackDetails;
        this.truncationPowerPenalty = truncModel;
        this.xtalkModel = xtalkModel;
    }

    public Map<String, String> getAllParameters() {
        Map<String, String> map = new SimpleMap<>();
        map.put("Truncation model at demux", this.truncationPowerPenalty.getClass().getSimpleName());
        map.put("Xtalk model", this.xtalkModel.getClass().getSimpleName());
        map.putAll(this.xtalkModel.getAllParameters());
        return map;
    }

    public int storeData(AbstractLinkFormat linkFormat, double[] insertionLoss, double[] Q_vec, double[] xtalk_pp,
                         double[] trunc_pp, Execution ex) {
        double min = 1.7976931348623157E308D;
        int bestIndex = 0;
        double[] sum_pp = new double[xtalk_pp.length];

        for (int i = 0; i < xtalk_pp.length; ++i) {
            sum_pp[i] = xtalk_pp[i] + trunc_pp[i] + insertionLoss[i];
            if (sum_pp[i] < min) {
                min = sum_pp[i];
                bestIndex = i;
            }
        }

        DataPoint basic = new DataPoint();
        basic.addProperty("wavelengths", linkFormat.getNumberOfChannels());
        basic.addProperties(this.getAllParameters());
        basic.addProperty("Filter array model", this.getClass().getSimpleName());
        basic.addProperty("Data Rate (Gb/s)", linkFormat.getWavelengthRate() / 1.0E9D);
        if (ex != null && this.trackDetails) {
            DataPoint dp1 = basic.getDerivedDataPoint();
            dp1.addResultProperty("ideal filter PP", sum_pp[bestIndex]);
            dp1.addResultProperty("ideal filter Q", Q_vec[bestIndex]);
            ex.addDataPoint(dp1);

            for (int i = 0; i < xtalk_pp.length; ++i) {
                DataPoint dp = basic.getDerivedDataPoint();
                if (!Double.isNaN(xtalk_pp[i]) && !Double.isNaN(trunc_pp[i])) {
                    dp.addProperty("Q value", Q_vec[i]);
                    dp.addResultProperty("Xtalk Penalty(Q)", xtalk_pp[i]);
                    dp.addResultProperty("Insertion Loss(Q)", insertionLoss[i]);
                    dp.addResultProperty("Total Filter Penalty(Q)", sum_pp[i]);
                    dp.addResultProperty("Truncation Penalty(Q)", trunc_pp[i]);
                    ex.addDataPoint(dp);
                }
            }
        }

        return bestIndex;
    }

    public static class AdaptiveILModel extends AbstractRingBasedFilterArrayModel.InsertionLossModel {

        private double lossFactor;
        private double effIndex;

        public AdaptiveILModel(@ParamName(name = "Ring Loss Factor (dB/cm)", default_ = "1") double lossFactor,
                               @ParamName(name = "Effective Index", default_ = "4.393") double effIndex) {
            this.lossFactor = lossFactor;
            this.effIndex = effIndex;
        }

        public double calculateInsertionLoss(Constants ct, double q, int stages) {
            double Qi = 6.283185307179586D * this.effIndex / (23.0D * ct.getCenterWavelength() * this.lossFactor);
            double IL = -10.0D * Math.log10(1.0D - 2.0D * q / Qi);
            return IL;
        }

        public Map<String, String> getAllParameters() {
            return SimpleMap.getMap("Ring loss factor in dB/cm", lossFactor + "",
                    "Demux ring Neff", effIndex + "");
        }
    }

    public static class ConstantILModel extends AbstractRingBasedFilterArrayModel.InsertionLossModel {

        private double constant;

        public ConstantILModel(double constant) {
            this.constant = constant;
        }

        public double calculateInsertionLoss(Constants ct, double q, int stages) {
            return this.constant;
        }

        public Map<String, String> getAllParameters() {
            return SimpleMap.getMap();
        }
    }

    public abstract static class InsertionLossModel {

        public InsertionLossModel() {
        }

        public abstract double calculateInsertionLoss(Constants var1, double var2, int var4);

        public abstract Map<String, String> getAllParameters();
    }
}
