//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.ResidualsBased_SincSquare_Lorentzian_Model;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RingResonatorModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RingResonatorModel.AbstractBendingLossModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RingResonatorModel.UBCModelOptimistic;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.AbstractXtalkPPModel;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class RingBasedFilterArrayModelAdaptiveIL extends AbstractRingBasedFilterArrayModel {

	protected double minGap;
	protected double maxGap;
	protected int steps;
	private AbstractBendingLossModel bendLossModel;
	public static double lastGap;

	public RingBasedFilterArrayModelAdaptiveIL(
			@ParamName(name = "Track details", default_ = "false") boolean trackDetails,
			@ParamName(name = "min Gap (nm)", default_ = "100") double minGap,
			@ParamName(name = "max Gap (nm)", default_ = "100") double maxGap,
			@ParamName(name = "optimization steps", default_ = "2") int steps,
			@ParamName(name = "Truncation model to use", defaultClass_ = ResidualsBased_SincSquare_Lorentzian_Model.class) Abstract_SincSquare_Lorentzian_TruncationModel truncModel,
			@ParamName(name = "Bending loss model") AbstractBendingLossModel bendLossModel,
			@ParamName(name = "Xtalk model", defaultClass_ = MeisamJLTXtalkModel.class) AbstractXtalkPPModel xtalkModel) {
		super(trackDetails, truncModel, xtalkModel);
		this.minGap = minGap;
		this.maxGap = maxGap;
		this.steps = steps;
		this.bendLossModel = bendLossModel;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.putAll(this.bendLossModel.getAllParameters());
		map.put("min Gap (demuxRing)", String.valueOf(this.minGap));
		map.put("max Gap (demuxRing)", String.valueOf(this.maxGap));
		map.put("Optimization steps", String.valueOf(this.steps));
		map.put("Bend loss model", this.bendLossModel.getClass().getSimpleName());
		return map;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, Execution ex, double modulationER) {
		Constants constants = modelSet.getConstants();
		double wavelengths = (double) linkFormat.getNumberOfChannels();
		double fullFSR = constants.getFullFSR();
		double channelSpacingNm = 1.0E9D * constants.getFullFSR() / wavelengths;
		double lambda0 = constants.getCenterWavelength();
		double rate = linkFormat.getWavelengthRate();
		double gapIncrement;
		if (this.steps >= 2) {
			gapIncrement = (this.maxGap - this.minGap) / (double) (this.steps - 1);
		} else {
			this.steps = 1;
			gapIncrement = 0.0D;
		}

		double[] xtalk_pp = new double[this.steps];
		double[] dropInsertionLoss = new double[this.steps];
		double[] throughInsertionLoss = new double[this.steps];
		double[] trunc_pp = new double[this.steps];
		double[] demuxq = new double[this.steps];
		double[] gapVec = new double[this.steps];
		double radiusMicron = 1000000.0D * lambda0 * lambda0
				/ (6.283185307179586D * RingResonatorModel.getNeff() * fullFSR);

		int bestIndex;
		double truncFinal;
		for (bestIndex = 0; bestIndex < this.steps; ++bestIndex) {
			Double outputGapNM = this.minGap + (double) bestIndex * gapIncrement;
			gapVec[bestIndex] = outputGapNM;
			throughInsertionLoss[bestIndex] = 1.0D;
			RingResonatorModel ringModel = new RingResonatorModel(radiusMicron, outputGapNM, (Double) 1.0D, 0.0D,
					this.bendLossModel);
			demuxq[bestIndex] = ringModel.getQualityFactor();
			truncFinal = 0.0D;

			for (int j = (int) (-Math.ceil(wavelengths / 2.0D)); j <= (int) Math.ceil(wavelengths / 2.0D); ++j) {
				if (j != 0) {
					double detuning = constants.getSpeedOfLight() / 1.55E-6D
							- constants.getSpeedOfLight() / (1550.0D + (double) j * channelSpacingNm) / 1.0E-9D;
					detuning = Math.abs(detuning);
					double p1 = this.getXtalkPenalty(demuxq[bestIndex], constants.getCenterFrequency(), detuning, rate);
					truncFinal += p1;
					double throughTrans = ringModel.getThroughTransmission((double) j * channelSpacingNm);
					throughInsertionLoss[bestIndex] *= throughTrans;
				}
			}

			xtalk_pp[bestIndex] = this.xtalkModel.getXtalkPP_DB(truncFinal, modelSet.getBer(), modulationER);
			if (Double.isInfinite(xtalk_pp[bestIndex]) || Double.isNaN(xtalk_pp[bestIndex])) {
				System.out.println("radiusMicron is " + radiusMicron);
				System.out.println("lambda0 is " + lambda0);
				System.out.println("Neff is " + RingResonatorModel.getNeff());
				System.out.println("xtalk power[" + bestIndex + "] is " + truncFinal + ", xtalk_pp [" + bestIndex
						+ "] is " + xtalk_pp[bestIndex]);
			}

			trunc_pp[bestIndex] = this.truncationPowerPenalty.getPowerPenalty(constants, rate, demuxq[bestIndex]);
			dropInsertionLoss[bestIndex] = ringModel.getDropInsertionLossdB();
			throughInsertionLoss[bestIndex] = -10.0D * Math.log10(throughInsertionLoss[bestIndex]);
		}

		bestIndex = this.findBestIndexAndStoreDataLocal(linkFormat, dropInsertionLoss, gapVec, xtalk_pp, trunc_pp,
				throughInsertionLoss, demuxq, radiusMicron, ex);
		lastGap = gapVec[bestIndex];
		double xtalkFinal;
		if (Double.isNaN(xtalk_pp[bestIndex])) {
			xtalkFinal = 1.0D / 0.0;
		} else {
			xtalkFinal = xtalk_pp[bestIndex];
		}

		if (Double.isNaN(trunc_pp[bestIndex])) {
			truncFinal = 1.0D / 0.0;
		} else {
			truncFinal = trunc_pp[bestIndex];
		}

		PowerPenalty xtalk = new PowerPenalty("Crosstalk", "Demux", xtalkFinal);
		PowerPenalty trunc = new PowerPenalty("Truncation", "Demux", truncFinal);
		PowerPenalty il = new PowerPenalty("Drop insertion loss", "Demux", dropInsertionLoss[bestIndex]);
		PowerPenalty ilT = new PowerPenalty("Through insertion loss", "Demux", throughInsertionLoss[bestIndex]);
		return MoreArrays.getArrayList(new PowerPenalty[] { xtalk, trunc, il, ilT });
	}

	public int findBestIndexAndStoreDataLocal(AbstractLinkFormat linkFormat, double[] insertionLoss, double[] gapVec,
			double[] xtalk_pp, double[] trunc_pp, double[] throughInsertionLoss, double[] demuxq, double ringRadius,
			Execution ex) {
		double min = 1.7976931348623157E308D;
		int bestIndex = 0;
		double[] sum_pp = new double[xtalk_pp.length];

		for (int i = 0; i < xtalk_pp.length; ++i) {
			sum_pp[i] = xtalk_pp[i] + trunc_pp[i] + insertionLoss[i] + throughInsertionLoss[i];
			if (sum_pp[i] < min) { min = sum_pp[i]; bestIndex = i; }
		}

		DataPoint basic = new DataPoint();
		basic.addProperty("wavelengths", linkFormat.getNumberOfChannels());
		basic.addProperty("Ring radius", ringRadius);
		basic.addProperties(this.getAllParameters());
		basic.addProperty("Filter array model", this.getClass().getSimpleName());
		basic.addProperty("Data Rate (Gb/s)", linkFormat.getWavelengthRate() / 1.0E9D);
		if (ex != null && this.trackDetails) {
			DataPoint dp1 = basic.getDerivedDataPoint();
			dp1.addResultProperty("ideal filter PP", sum_pp[bestIndex]);
			dp1.addResultProperty("ideal filter gap", gapVec[bestIndex]);
			dp1.addResultProperty("ideal filter Q", demuxq[bestIndex]);
			ex.addDataPoint(dp1);

			for (int i = 0; i < xtalk_pp.length; ++i) {
				DataPoint dp = basic.getDerivedDataPoint();
				if (xtalk_pp[i] != 0.0D / 0.0 && trunc_pp[i] != 0.0D / 0.0) {
					dp.addProperty("gap value", gapVec[i]);
					dp.addResultProperty("Xtalk Penalty(gap)", xtalk_pp[i]);
					dp.addResultProperty("Insertion Loss(gap)", insertionLoss[i] + throughInsertionLoss[i]);
					dp.addResultProperty("Total Filter Penalty(gap)", sum_pp[i]);
					System.out.println("Total filter penalty this time:" + sum_pp[i]);
					dp.addResultProperty("Truncation Penalty(gap)", trunc_pp[i]);
					ex.addDataPoint(dp);
				}
			}
		}

		return bestIndex;
	}

	private double getXtalkPenalty(double filterQ, double v0, double deltaV, double rate) {
		double FWHM = v0 / filterQ;
		double beta = 2.0D * deltaV / FWHM;
		double nu = FWHM / (2.0D * rate);
		double theta = 6.283185307179586D * nu * beta;
		double realPart = 1.0D - beta * beta;
		realPart += -1.0D * Math.exp(-6.283185307179586D * nu) * Math.cos(theta) * (1.0D - beta * beta);
		realPart += Math.exp(-6.283185307179586D * nu) * Math.sin(theta) * 2.0D * beta;
		realPart /= Math.pow(1.0D + beta * beta, 2.0D);
		double xtalk = 1.0D / (1.0D + beta * beta) - 1.0D / (6.283185307179586D * nu) * realPart;
		return xtalk;
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

	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		throw new IllegalStateException("Not implemented yet");
	}

	public static void main(String[] args) {
		AbstractXtalkPPModel xtalkPPModel = new MeisamJLTXtalkModel(true);
		RingBasedFilterArrayModelAdaptiveIL adaptiveIL = new RingBasedFilterArrayModelAdaptiveIL(false, 100.0D, 400.0D,
				100, new ResidualsBased_SincSquare_Lorentzian_Model(), new UBCModelOptimistic(), xtalkPPModel);
		double xtalk_pp = 0.0D;

		for (double channelSpacing = 3.2E-10D; channelSpacing <= 3.2E-9D; channelSpacing += 3.2E-10D) {
			Constants constants = new Constants(0.8D, 1.55E-6D);
			double Q_demux = 6500.0D;
			double add_xtalk1 = 0.0D;
			int wavelengths = 10;
			int center = 5;

			for (int j = -center + 1; j < wavelengths - center; ++j) {
				if (j != 0) {
					double detuning = constants.getSpeedOfLight() / 1.55E-6D
							- constants.getSpeedOfLight() / (1.55E-6D + (double) j * channelSpacing);
					detuning = Math.abs(detuning);
					double p1 = adaptiveIL.getXtalkPenalty(Q_demux, constants.getCenterFrequency(), detuning, 1.0E10D);
					add_xtalk1 += p1;
				}
			}

			System.out.println("XTALK PWR =" + add_xtalk1);
			xtalk_pp = xtalkPPModel.getXtalkPP_DB(add_xtalk1, 9, 5.223D);
			System.out.println(channelSpacing + " is Channel Spacing, " + xtalk_pp + " is XTALK PP");
		}

	}
}
