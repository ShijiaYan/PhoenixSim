package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.clazzes.ParamName;

public class AwgDemux {

    // parameters
    private double passiveIL;
    private double polarizationLoss;
    //	private double demuxFSR ;
    //	double numBranch ;
    private double awgStaticPower;
    double Q_min = 500;
    double Q_max = 5000;
    double steps;
    boolean trackDetails;

    public AwgDemux(
            @ParamName(name = "Passive insertion loss (dB)", default_ = "3") double passiveIL,
            @ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
//			@ParamName(name = "Demux FSR (nm)", default_ = "50") double demuxFSR,
            @ParamName(name = "optimization steps", default_ = "100") int steps,
//			@ParamName(name = "Number of Branches", default_ = "20") double numBranch,
            @ParamName(name = "AWG Thermal Tunning Power (mW)", default_ = "0") double awgStaticPower,
            @ParamName(name = "Track Details", default_ = "true") boolean trackDetails) {
        this.passiveIL = passiveIL;
//		this.demuxFSR = demuxFSR ;
//		this.numBranch = numBranch ;
        this.steps = steps;
        this.trackDetails = trackDetails;
        this.polarizationLoss = polarizationLoss;
        this.awgStaticPower = awgStaticPower;

    }


    public double getPowerPenalties(Constants ct, double effectiveRate, int NumberOfChannels, Execution ex) {

        double channelSpacing = ct.wavelengthsToChannelSpacing(NumberOfChannels);

        double dQ = (Q_max - Q_min) / steps;
        int size = (int) Math.ceil((Q_max - Q_min) / dQ);
        double[] Q_vec = new double[size];
        for (int i = 0; i < Q_vec.length; i++) {
            Q_vec[i] = Q_min + i * dQ;
        }
        double[] xtalk_pp1 = new double[size];
        double[] sum_pp = new double[size];
        double[] insertionLoss = new double[size];
        double[] truncationPP = new double[size];

        for (int i = 0; i < Q_vec.length; i++) {
            double Q_demux = Q_vec[i];

            double add_xtalk1 = 0;
            for (int j = (int) -Math.ceil(NumberOfChannels / 2); j <= (int) Math.ceil(NumberOfChannels / 2); j++) {
                if (j != 0) {

                    add_xtalk1 += getXtalkPower(Q_demux, ct.getCenterFrequency(), j * channelSpacing);
                }
            }

            xtalk_pp1[i] = -5 * Math.log10(1 - 6 * add_xtalk1);  // for BER = 10 ^ (-9)

            double pp_trunc = getTruncationPenalty(Q_demux, ct.getCenterFrequency(), effectiveRate);


            insertionLoss[i] = passiveIL + polarizationLoss + 0.05 * NumberOfChannels; // incremental loss 0.05 dB per
            // channel
            truncationPP[i] = pp_trunc;

            sum_pp[i] = xtalk_pp1[i] + pp_trunc + insertionLoss[i];
        }

        double min = Double.MAX_VALUE;
        int bestIndex = 0;
        for (int i = 0; i < sum_pp.length; i++) {
            if (sum_pp[i] < min) {
                min = sum_pp[i];
                bestIndex = i;
            }
        }


        double xtalk = xtalk_pp1[bestIndex];
        double trunc = truncationPP[bestIndex];
        double il = insertionLoss[bestIndex];


        DataPoint basic = new DataPoint();
        basic.addProperty("wavelengths", NumberOfChannels);

        if (ex != null && trackDetails) {

            DataPoint dp1 = basic.getDerivedDataPoint();
            dp1.addResultProperty("ideal filter PP", min);
            dp1.addResultProperty("ideal filter Q", Q_vec[bestIndex]);

            ex.addDataPoint(dp1);

            for (int i = 0; i < sum_pp.length; i++) {
                DataPoint dp = basic.getDerivedDataPoint();
                if (Double.isNaN(xtalk_pp1[i]) || Double.isNaN(truncationPP[i])) {
                    continue;
                }
                dp.addProperty("Q value", Q_vec[i]);
                dp.addResultProperty("Xtalk Penalty(Q)", xtalk_pp1[i]);
                dp.addResultProperty("Total Filter Penalty(Q)", sum_pp[i]);
                dp.addResultProperty("Insertion Loss(Q)", insertionLoss[i]);
                dp.addResultProperty("Truncation Penalty(Q)", truncationPP[i]);
                ex.addDataPoint(dp);
            }
        }

        return xtalk + trunc + il;
    }


    public double getAwgTransmission(double freq, double centerFreq, double Q_demux) {
        double bandwidth = centerFreq / Q_demux;
        double arg =
                Math.sin(Math.PI * 2 * (freq - centerFreq) / bandwidth) / (Math.PI * 2 * (freq - centerFreq) / bandwidth);
        double trans = Math.pow(arg, 2);
        return trans;
    }

    public double getTruncationPenalty(double Q_demux, double centerFreq, double rate) {
        double bandwidth = centerFreq / Q_demux;
        double alpha = bandwidth / rate;
        double A = Math.abs(alpha - 2);
        double B = Math.abs(alpha + 2);
        double penalty = 1 / (24 * alpha) * (-2 * Math.pow(alpha, 3) - 16 + Math.pow(A, 3) + Math.pow(B, 3));
        double penaltyDB = -5 * Math.log10(penalty);
        return penaltyDB;
    }

    public double getXtalkPower(double Q_demux, double centerFreq, double channelSpacing) {
        double freq = centerFreq + channelSpacing;
        return getAwgTransmission(freq, centerFreq, Q_demux);
    }

    // Power Consumption?

    public double getAwgDeMuxPowerConsumption(double dataRateGbps) {

        double powerConsumption = awgStaticPower / dataRateGbps; // scaling power consumption with bit rate at 10 Gbps
        return powerConsumption * 1e3; // return power in micro watts
    }
}
