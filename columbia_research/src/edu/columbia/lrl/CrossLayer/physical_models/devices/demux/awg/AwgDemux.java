//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.awg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.AbstractDemux;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class AwgDemux extends AbstractDemux {

	double passiveIL;
	double polarizationLoss;
	double demuxFSR;
	double numBranch;
	double awgStaticPower;
	double Q_min = 1000.0D;
	double Q_max = 5000.0D;
	double steps;
	boolean trackDetails;
	private AbstractXtalkPPModel xtalkModel;

	public AwgDemux(
			@ParamName(name = "Xtalk model", defaultClass_ = MeisamJLTXtalkModel.class) AbstractXtalkPPModel xtalkModel,
			@ParamName(name = "Passive insertion loss (dB)", default_ = "5") double passiveIL,
			@ParamName(name = "Polarization loss (dB)", default_ = "0.5") double polarizationLoss,
			@ParamName(name = "optimization steps", default_ = "100") int steps,
			@ParamName(name = "Number of Branches", default_ = "20") double numBranch,
			@ParamName(name = "AWG Thermal Tunning Power (mW)", default_ = "0") double awgStaticPower,
			@ParamName(name = "Track Details", default_ = "true") boolean trackDetails) {
		this.passiveIL = passiveIL;
		this.numBranch = numBranch;
		this.steps = (double) steps;
		this.trackDetails = trackDetails;
		this.xtalkModel = xtalkModel;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Passive insertion loss (dB)", String.valueOf(this.passiveIL));
		map.put("Polarization Loss (dB)", String.valueOf(this.polarizationLoss));
		map.put("AWG Demux FSR", String.valueOf(this.demuxFSR));
		map.put("Number of Branches", String.valueOf(this.numBranch));
		map.put("AWG Thermal Tunning Power", String.valueOf(this.awgStaticPower));
		map.put("Xtalk model", this.xtalkModel.getClass().getSimpleName());
		map.putAll(this.xtalkModel.getAllParameters());
		return map;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
		Constants ct = modelSet.getConstants();
		int wavelengths = linkFormat.getNumberOfChannels();
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double rate = linkFormat.getWavelengthRate();
		double dQ = (this.Q_max - this.Q_min) / this.steps;
		int size = (int) Math.ceil((this.Q_max - this.Q_min) / dQ);
		double[] Q_vec = new double[size];

		for (int i = 0; i < Q_vec.length; ++i) {
			Q_vec[i] = this.Q_min + (double) i * dQ;
		}

		double[] xtalk_pp1 = new double[size];
		double[] sum_pp = new double[size];
		double[] insertionLoss = new double[size];
		double[] truncationPP = new double[size];

		for (int i = 0; i < Q_vec.length; ++i) {
			double Q_demux = Q_vec[i];
			double add_xtalk1 = 0.0D;

			for (int j = (int) (-Math.ceil((double) (wavelengths / 2))); j <= (int) Math
					.ceil((double) (wavelengths / 2)); ++j) {
				if (j != 0) {
					add_xtalk1 += this.getXtalkPower(Q_demux, ct.getCenterFrequency(), (double) j * channelSpacing);
				}
			}

			xtalk_pp1[i] = this.xtalkModel.getXtalkPP_DB(add_xtalk1, modelSet.getBer(), modulationER);
			double pp_trunc = this.getTruncationPenalty(Q_demux, ct.getCenterFrequency(), rate);
			insertionLoss[i] = this.passiveIL + this.polarizationLoss;
			truncationPP[i] = pp_trunc;
			sum_pp[i] = xtalk_pp1[i] + pp_trunc + insertionLoss[i];
		}

		double min = 1.7976931348623157E308D;
		int bestIndex = 0;

		for (int i = 0; i < sum_pp.length; ++i) {
			if (sum_pp[i] < min) { min = sum_pp[i]; bestIndex = i; }
		}

		PowerPenalty xtalk = new PowerPenalty("Crosstalk", "Demux", xtalk_pp1[bestIndex]);
		PowerPenalty trunc = new PowerPenalty("Truncation", "Demux", truncationPP[bestIndex]);
		PowerPenalty il = new PowerPenalty("Insertion loss", "Demux", insertionLoss[bestIndex]);
		DataPoint basic = new DataPoint();
		basic.addProperty("wavelengths", wavelengths);
		basic.addProperties(this.getAllParameters());
		basic.addProperty("Filter array model", this.getClass().getSimpleName());
		if (ex != null && this.trackDetails) {
			DataPoint dp1 = basic.getDerivedDataPoint();
			dp1.addResultProperty("ideal filter PP", min);
			dp1.addResultProperty("ideal filter Q", Q_vec[bestIndex]);
			ex.addDataPoint(dp1);

			for (int i = 0; i < sum_pp.length; ++i) {
				DataPoint dp = basic.getDerivedDataPoint();
				if (xtalk_pp1[i] != 0.0D / 0.0 && truncationPP[i] != 0.0D / 0.0) {
					dp.addProperty("Q value", Q_vec[i]);
					dp.addResultProperty("Xtalk Penalty(Q)", xtalk_pp1[i]);
					dp.addResultProperty("Total Filter Penalty(Q)", sum_pp[i]);
					dp.addResultProperty("Insertion Loss(Q)", insertionLoss[i]);
					dp.addResultProperty("Truncation Penalty(Q)", truncationPP[i]);
					dp.addResultProperty("Data Rate (Gb/s)", linkFormat.getWavelengthRate() / 1.0E9D);
					ex.addDataPoint(dp);
				}
			}
		}

		return MoreArrays.getArrayList(new PowerPenalty[] { xtalk, trunc, il });
	}

	public double getAwgTransmission(double freq, double centerFreq, double Q_demux) {
		double bandwidth = centerFreq / Q_demux;
		double arg = Math.sin(6.283185307179586D * (freq - centerFreq) / bandwidth)
				/ (6.283185307179586D * (freq - centerFreq) / bandwidth);
		double trans = Math.pow(arg, 2.0D);
		return trans;
	}

	public double getTruncationPenalty(double Q_demux, double centerFreq, double rate) {
		double bandwidth = centerFreq / Q_demux;
		double alpha = bandwidth / rate;
		double A = Math.abs(alpha - 2.0D);
		double B = Math.abs(alpha + 2.0D);
		double penalty = 1.0D / (24.0D * alpha)
				* (-2.0D * Math.pow(alpha, 3.0D) - 16.0D + Math.pow(A, 3.0D) + Math.pow(B, 3.0D));
		double penaltyDB = -5.0D * Math.log10(penalty);
		return penaltyDB;
	}

	public double getXtalkPower(double Q_demux, double centerFreq, double channelSpacing) {
		double freq = centerFreq + channelSpacing;
		double xtalk = this.getAwgTransmission(freq, centerFreq, Q_demux);
		return xtalk;
	}

	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<>(1);
		if (this.awgStaticPower != 0.0D) {
			throw new IllegalStateException("Not implemented yet");
		} else {
			return pc;
		}
	}

	public boolean hasThroughCapability() {
		return false;
	}

	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("Cannot be passedby");
	}
}
