package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.Abstract_OOK_NRZ_Receiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RateDependentRingPNJunctionDepletionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.RingResonance;


public class Old_OOK_NRZ_ModulatorArrayModel extends AbstractRingBased_OOK_NRZ_ModulatorArrayModel {

	private double ringRadius;
	private double q;
	private double qWithCarriers;
	private double passiveIL;
	private double referenceER;
	private double extinctionRatio;
	private double capacitance;
	private double voltage;
	private double Neff;

	public Old_OOK_NRZ_ModulatorArrayModel(Abstract_OOK_NRZ_Receiver ookReceiver,
			@ParamName(name = "Ring Radius", default_ = "1.87e-6") double ringRadius,
			@ParamName(name = "Modulator Neff", default_ = "4.393") double Neff,
			@ParamName(name = "Modulator Q", default_ = "12000") double q,
			@ParamName(name = "Modulator Q With Carriers", default_ = "5600") double qWithCarriers,
			@ParamName(name = "Passive Insertion Loss (dB)", default_ = "1") double passiveIL,
			@ParamName(name = "Reference Extinction Ratio (dB)", default_ = "20") double referenceER,
			@ParamName(name = "Extinction Ratio (dB)", default_ = "8") double extinctionRatio,
			@ParamName(name = "Power Model", defaultClass_ = RateDependentRingPNJunctionDepletionDriverPowerModel.class) AbstractRingPNJunctionDriverPowerModel powerModel,
			@ParamName(name = "Driving Voltage", default_ = "2") double voltage,
			@ParamName(name = "Capacitance(fF)", default_ = "145") double capacitance) {
		super(ookReceiver, powerModel);
		this.ringRadius = ringRadius;
		this.Neff = Neff;
		this.q = q;
		this.qWithCarriers = qWithCarriers;
		this.passiveIL = passiveIL;
		this.referenceER = referenceER;
		this.extinctionRatio = extinctionRatio;
		this.voltage = voltage;
		this.capacitance = capacitance;
	}

	public Map<String, String> getRingBasedArrayParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Ring radius", String.valueOf(this.ringRadius));
		map.put("Modulator Q", String.valueOf(this.q));
		map.put("Modulator Q with carriers", String.valueOf(this.qWithCarriers));
		map.put("Passive IL (dB)", String.valueOf(this.passiveIL));
		map.put("Reference ER (dB)", String.valueOf(this.referenceER));
		map.put("Extinction ratio (dB)", String.valueOf(this.extinctionRatio));
		return map;
	}

	public double getDrivingCapacitance() {
		return this.voltage;
	}

	public double getDrivingVoltage(Constants ct, AbstractLinkFormat linkFormat) {
		return this.capacitance;
	}

	public ArrayList<PowerPenalty> getPassbyModulationBankPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		Constants ct = modelSet.getConstants();
		int wavelengths = linkFormat.getNumberOfChannels();
		double centerHz = ct.getSpeedOfLight() / ct.getCenterWavelength();
		double fsrHz = ct.getSpeedOfLight() / (6.283185307179586D * this.ringRadius * this.Neff);
		RingResonance resonance = new RingResonance(centerHz, fsrHz, this.q);
		int max = 2147483647;

		for (int i = resonance.getNumSamples() - 1; i >= 0; --i) {
			if (resonance.at(i) >= 0.5D) { max = i; break; }
		}

		double v_laser = resonance.getFrequencyAtIndex(max);
		double signal_mul_IL = 1.0D;
		double avg_loss = 0.0D;
		int ext = (int) Math.ceil((double) wavelengths / 2.0D);
		double[] losses = new double[2 * ext];
		double[] accum = new double[2 * ext];
		double[] ringRes1 = new double[2 * ext];
		double[] ringRes2 = new double[2 * ext];
		double[] indexes = new double[2 * ext];
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double shiftHz = channelSpacing / 2.0D;
		if (shiftHz > 2.5E11D) {
			shiftHz = 2.5E11D;
		} else if (shiftHz < 1.0E10D) { shiftHz = 1.0E10D; }

		for (int i = -ext; i < ext; ++i) {
			indexes[i + ext] = i;
			if (i != 0) {
				double r1 = RingResonance.getResonance(centerHz, fsrHz, this.q, v_laser + (double) i * channelSpacing);
				double r2 = RingResonance.getResonance(centerHz - shiftHz, fsrHz, this.qWithCarriers,
						v_laser + (double) i * channelSpacing);
				if (r1 > 0.999D) { r1 = 1.0D; }

				if (r2 > 0.999D) { r2 = 1.0D; }

				ringRes1[i + ext] = r1;
				ringRes2[i + ext] = r2;
				avg_loss = (ringRes1[i + ext] + ringRes2[i + ext]) / 2.0D;
				losses[i + ext] = 1.0D - avg_loss;
				signal_mul_IL *= 1.0D - avg_loss;
				accum[i + ext] = -10.0D * Math.log10(signal_mul_IL);
			} else {
				accum[i + ext] = accum[i + ext - 1];
			}
		}

		PowerPenalty intermod = new PowerPenalty("Crosstalk", "Modulator", -10.0D * Math.log10(signal_mul_IL));
		PowerPenalty passivePP = new PowerPenalty("Passive insertion loss", "Modulator", this.passiveIL);
		return MoreArrays.getArrayList(intermod, passivePP);
	}

	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		double ref_r = Math.pow(10.0D, this.referenceER / 10.0D);
		double ref_er_pp = this.getPowerPenalty(ref_r);
		double er_r = Math.pow(10.0D, this.extinctionRatio / 10.0D);
		double er_pp = this.getPowerPenalty(er_r) - ref_er_pp;
		double ook_il = -10.0D * Math.log10(0.5D + 0.5D / er_r);
		PowerPenalty ook = new PowerPenalty("OOK", "Modulator", ook_il);
		PowerPenalty erPP = new PowerPenalty("Extinction ratio", "Modulator", er_pp);
		ArrayList<PowerPenalty> losses = this.getPassbyModulationBankPowerPenalties(modelSet, linkFormat);
		losses.add(ook);
		losses.add(erPP);
		Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<>();
		pair.setFirst(this.extinctionRatio);
		pair.setSecond(losses);
		return pair;
	}

	private double getPowerPenalty(double r) {
		double a = r - 1.0D;
		double b = r + 1.0D;
		double c = Math.sqrt(b);
		double d = Math.sqrt(r) + 1.0D;
		return -10.0D * Math.log10(a / b * c / d);
	}
}
