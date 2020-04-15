//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class RingBasedDoubleStageFilterArrayModelAdaptiveIL extends AbstractRingBasedFilterArrayModel {

	protected double Q_min;
	protected double Q_max;
	protected int steps;
	private InsertionLossModel ilModel;

	public RingBasedDoubleStageFilterArrayModelAdaptiveIL(
			@ParamName(name = "Track details", default_ = "false") boolean trackDetails,
			@ParamName(name = "Q min", default_ = "1000") double Qmin,
			@ParamName(name = "Q max", default_ = "40000") double Qmax,
			@ParamName(name = "optimization steps", default_ = "100") int steps,
			@ParamName(name = "Xtalk model", defaultClass_ = MeisamJLTXtalkModel.class) AbstractXtalkPPModel xtalkModel,
			@ParamName(name = "Insertion loss model", defaultClass_ = AdaptiveILModel.class) InsertionLossModel ilModel) {
		super(trackDetails, (Abstract_SincSquare_Lorentzian_TruncationModel) null, xtalkModel);
		this.ilModel = ilModel;
		this.Q_min = Qmin;
		this.Q_max = Qmax;
		this.steps = steps;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.putAll(this.ilModel.getAllParameters());
		map.put("Insertion loss model", this.ilModel.getClass().getSimpleName());
		map.put("Qmin", String.valueOf(this.Q_min));
		map.put("Qmax", String.valueOf(this.Q_max));
		map.put("Optimization steps Q demux", String.valueOf(this.steps));
		return map;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
		Constants ct = modelSet.getConstants();
		int wavelengths = linkFormat.getNumberOfChannels();
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double rate = linkFormat.getWavelengthRate();
		double dQ;
		int size;
		if (this.steps >= 2) {
			dQ = (this.Q_max - this.Q_min) / (double) this.steps;
			size = (int) Math.ceil((this.Q_max - this.Q_min) / dQ);
		} else {
			dQ = 0.0D;
			size = 1;
		}

		double[] Q_vec = new double[size];

		for (int i = 0; i < Q_vec.length; ++i) {
			Q_vec[i] = this.Q_min + (double) i * dQ;
		}

		double[] xtalk_pp = new double[size];
		double[] sum_pp = new double[size];
		double[] insertionLoss = new double[size];
		double[] trunc_pp = new double[size];

		int i;
		for (i = 0; i < Q_vec.length; ++i) {
			double Q_demux = Q_vec[i];
			double add_xtalk1 = 0.0D;

			for (int j = (int) (-Math.ceil((double) (wavelengths / 2))); j <= (int) Math
					.ceil((double) (wavelengths / 2)); ++j) {
				if (j != 0) {
					double p2 = 1.0D / (1.0D
							+ Math.pow((double) (2 * j) * channelSpacing * Q_demux / ct.getCenterFrequency(), 2.0D));
					add_xtalk1 += p2 * p2;
				}
			}

			xtalk_pp[i] = this.xtalkModel.getXtalkPP_DB(add_xtalk1, modelSet.getBer(), modulationER);
			trunc_pp[i] = this.getTruncationPowerPenalty(ct, rate, Q_demux);
			insertionLoss[i] = 2.0D * this.ilModel.calculateInsertionLoss(ct, Q_demux, 2);
			sum_pp[i] = xtalk_pp[i] + trunc_pp[i] + insertionLoss[i];
		}

		i = this.storeData(linkFormat, insertionLoss, Q_vec, xtalk_pp, trunc_pp, ex);
		PowerPenalty xtalk = new PowerPenalty("Crosstalk", "Demux", xtalk_pp[i]);
		PowerPenalty trunc = new PowerPenalty("Truncation", "Demux", trunc_pp[i]);
		PowerPenalty il = new PowerPenalty("Insertion loss", "Demux", insertionLoss[i]);
		return MoreArrays.getArrayList(new PowerPenalty[] { xtalk, trunc, il });
	}

	private double getTruncationPowerPenalty(Constants ct, double rate, double filterQ) {
		double FWHM = ct.getCenterFrequency() / filterQ;
		double alpha = FWHM / (2.0D * rate);
		double eyeClosure = 1.0D - 2.0D * (1.0D + 6.283185307179586D * alpha) * Math.exp(-6.283185307179586D * alpha);
		double trunPenaltydB = -20.0D * Math.log10(eyeClosure);
		return trunPenaltydB;
	}

	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<>(1);
		PowerConsumption p2 = new PowerConsumption("Demux rings", false, true, true,
				2.0D * modelSet.getDefaultSingleRingTTPowerMW());
		pc.add(p2);
		return pc;
	}

	public boolean hasThroughCapability() {
		return false;
	}

	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("Not implemented yet");
	}
}
