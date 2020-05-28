//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.switches;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math.complex.Complex;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.Abstract_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.truncation.PolynomBased_SincSquare_Lorentzian_TruncationModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class Switch1x2 extends AbstractRingBasedSwitch {

	double lossCoefficient;
	boolean trackDetails;
	double neff;
	Abstract_SincSquare_Lorentzian_TruncationModel truncationPowerPenalty;

	public Switch1x2(@ParamName(name = "Loss coefficient", default_ = "4") double lossCoefficient,
			@ParamName(name = "Effective Index", default_ = "4.393") double neff,
			@ParamName(name = "Track details", default_ = "false") boolean trackDetails,
			@ParamName(name = "Switch ring driving power (mW)", default_ = "5") double switchRingDrivingPower,
			@ParamName(name = "Truncation model to use", defaultClass_ = PolynomBased_SincSquare_Lorentzian_TruncationModel.class) Abstract_SincSquare_Lorentzian_TruncationModel truncModel) {
		super(switchRingDrivingPower);
		this.lossCoefficient = lossCoefficient;
		this.trackDetails = trackDetails;
		this.neff = neff;
		this.truncationPowerPenalty = truncModel;
	}

	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Loss coefficient", String.valueOf(this.lossCoefficient),
				"Truncation model at 1x2 switch", this.truncationPowerPenalty.getClass().getSimpleName());
	}

	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList(1);
		PowerConsumption p = new PowerConsumption("1x2 Switches", false, false, false,
				modelSet.getDefaultSingleRingTTPowerMW() + 0.5D * this.drivingPower);
		pc.add(p);
		return pc;
	}

	public ArrayList<PowerPenalty> getPowerPenalties(Constants ct, AbstractLinkFormat linkFormat, int numberDrop,
			int numberThrough) {
		double rate = linkFormat.getWavelengthRate();
		double c0 = ct.getSpeedOfLight();
		double lambda0 = ct.getCenterWavelength();
		int wavelengths = linkFormat.getNumberOfChannels();
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double radius = c0 / (6.283185307179586D * this.neff * channelSpacing);
		double L = 6.283185307179586D * radius;
		double fsr_wavelength = Math.pow(lambda0, 2.0D) / (this.neff * L);
		double fsr_freq = c0 / Math.pow(lambda0, 2.0D) * fsr_wavelength;
		double shift;
		if (fsr_freq < 5.0E11D) {
			shift = fsr_freq / 2.0D;
		} else {
			shift = 2.5E11D;
		}

		double ring_loss_dB = 6.283185307179586D * radius * this.lossCoefficient * 100.0D;
		double alpha1 = Math.sqrt(Math.pow(10.0D, -ring_loss_dB / 10.0D));
		double half_alpha = Math.sqrt(Math.pow(10.0D, -(ring_loss_dB / 2.0D) / 10.0D));
		double delta_alpha = this.getLossFromInjection(ct.getCenterFrequency(), shift, this.neff);
		double alpha_with_carriers = alpha1 * Math.exp(-delta_alpha * L);
		double half_alpha_with_carriers = half_alpha * Math.exp(-delta_alpha * L);
		int numSamples = 500;
		double[] t_vec = new double[numSamples];

		for (int i = 0; i < t_vec.length; ++i) {
			t_vec[i] = 0.5D + (double) i * (0.5D / (double) numSamples);
		}

		double[] total_loss_vec = new double[numSamples];
		double[] through_IL_vec = new double[numSamples];
		double[] drop_IL_vec = new double[numSamples];
		double[] FWHM_freq_vec = new double[numSamples];
		double[] pp_trunc_vec = new double[numSamples];
		int numPhaseSamples = 1258;
		double[] phase_vec = new double[numPhaseSamples];

		int iterations;
		for (iterations = 0; iterations < phase_vec.length; ++iterations) {
			phase_vec[iterations] = (double) iterations * (3.141592653589793D / (double) numPhaseSamples);
		}

		iterations = t_vec.length;

		for (int i = 0; i < iterations; ++i) {
			double through_power = this.getThroughIL(t_vec[i], alpha1, phase_vec[0], shift, 1, channelSpacing);
			through_IL_vec[i] = 10.0D * Math.log10(through_power);
			double drop_power = this.getDropIL(t_vec[i], alpha_with_carriers, half_alpha_with_carriers, phase_vec[0]);
			drop_IL_vec[i] = 10.0D * Math.log10(drop_power);
			double[] drop_port_response = new double[phase_vec.length];

			for (int j = 0; j < phase_vec.length; ++j) {
				drop_power = this.getDropIL(t_vec[i], alpha_with_carriers, half_alpha_with_carriers, phase_vec[j]);
				drop_port_response[j] = drop_power;
			}

			double max_drop = MoreArrays.max(drop_port_response);
			double[] normalized_drop_port = MoreArrays.product(drop_port_response, 1.0D / max_drop);
			int half_ind = 0;
			double half_val = 0.0D;

			for (int j = 0; j < normalized_drop_port.length; ++j) {
				if (normalized_drop_port[j] < 0.5D) { half_ind = j; half_val = normalized_drop_port[j]; break; }
			}

			if (half_ind == 0) { break; }

			double distance_minus = Math.abs(0.5D - half_val);
			double distance_plus = Math.abs(0.5D - normalized_drop_port[half_ind - 1]);
			if (distance_plus < distance_minus) { --half_ind; }

			double FWHM_radian = phase_vec[half_ind] * 2.0D;
			FWHM_freq_vec[i] = FWHM_radian / 6.283185307179586D * fsr_freq;
			double switchQ = ct.getCenterFrequency() / FWHM_freq_vec[i];
			double pp_trunc = this.truncationPowerPenalty.getPowerPenalty(ct, rate, switchQ);
			pp_trunc_vec[i] = pp_trunc;
			total_loss_vec[i] = (double) numberDrop * -drop_IL_vec[i] + (double) numberThrough * -through_IL_vec[i]
					+ pp_trunc;
		}

		Pair<Double, Integer> min_indexP = MoreArrays.minAndIndex(total_loss_vec);
		int min_index = min_indexP.getSecond();
		if (min_index >= 0) {
			PowerPenalty through = new PowerPenalty("1x2 switch through", "switch", -through_IL_vec[min_index])
					.multiply(numberThrough);
			PowerPenalty drop = new PowerPenalty("1x2 switch drop", "switch", -drop_IL_vec[min_index])
					.multiply(numberDrop);
			PowerPenalty trunc = new PowerPenalty("1x2 switch trunc", "switch", pp_trunc_vec[min_index]);
			return MoreArrays.getArrayList(through, drop, trunc);
		} else {
			return MoreArrays.getArrayList(
					new PowerPenalty("1x2 switch", "switch", 1.7976931348623157E308D));
		}
	}

	private double getLossFromInjection(double v0, double resonanceShift, double effectiveIndex) {
		double dn_eff = -effectiveIndex / v0 * resonanceShift;
		double[] x = new double[10000];
		double[] y = new double[x.length];

		int greater_inds;
		for (greater_inds = 0; greater_inds < x.length; ++greater_inds) {
			x[greater_inds] = (double) (greater_inds + 1) * 1.0E15D;
			y[greater_inds] = dn_eff + 8.8E-22D * x[greater_inds] + 8.5E-18D * Math.pow(x[greater_inds], 0.8D);
		}

		greater_inds = -1;
		int smaller_inds = 2147483647;

		for (int i = 0; i < x.length; ++i) {
			if (y[i] > 0.0D && greater_inds == -1) { greater_inds = i; }

			if (y[i] < 0.0D) { smaller_inds = i; }
		}

		double[] x_refined = new double[1000];
		double diff = (x[greater_inds] - x[smaller_inds]) / (double) x_refined.length;

		for (int i = 0; i < x_refined.length; ++i) {
			x_refined[i] = x[smaller_inds] + (double) i * diff;
		}

		double[] y_refined = new double[x_refined.length];

		int biggerIn;
		for (biggerIn = 0; biggerIn < y_refined.length; ++biggerIn) {
			y_refined[biggerIn] = dn_eff + 8.8E-22D * x_refined[biggerIn]
					+ 8.5E-18D * Math.pow(x_refined[biggerIn], 0.8D);
		}

		biggerIn = -1;

		for (int i = 0; i < y_refined.length; ++i) {
			if (y_refined[i] < 0.0D) { biggerIn = i; }
		}

		double dN = x_refined[biggerIn];
		return 8.5E-18D * dN + 6.0E-18D * dN;
	}

	private double getThroughIL(double t, double alpha, double phase, double shift, int do_shift,
			double channelSpacing) {
		Complex one = new Complex(1.0D, 0.0D);
		Complex alphaC = new Complex(alpha, 0.0D);
		Complex tC = new Complex(t, 0.0D);
		Complex t2C = new Complex(t * t, 0.0D);
		Complex et1;
		Complex thetaI;
		if (do_shift == 1) {
			double theta = shift / channelSpacing * 2.0D * 3.141592653589793D + phase;
			thetaI = new Complex(0.0D, theta);
			Complex expThetaI = thetaI.exp();
			Complex alphaExpThetaI = expThetaI.multiply(alphaC);
			et1 = one.subtract(alphaExpThetaI).multiply(tC).divide(one.subtract(t2C.multiply(alphaExpThetaI)));
		} else {
			Complex phaseI = new Complex(0.0D, phase);
			Complex expPhaseI = phaseI.exp();
			thetaI = expPhaseI.multiply(alphaC);
			et1 = one.subtract(thetaI).multiply(tC).divide(one.subtract(t2C.multiply(thetaI)));
		}

		return Math.pow(et1.abs(), 2.0D);
	}

	private double getDropIL(double t, double alpha, double half_alpha, double phase) {
		Complex one = new Complex(1.0D, 0.0D);
		double minusK2 = -(1.0D - Math.pow(t, 2.0D));
		Complex a = new Complex(minusK2 * half_alpha, 0.0D);
		Complex b = new Complex(Math.pow(t, 2.0D) * alpha, 0.0D);
		Complex phaseI = new Complex(0.0D, phase);
		Complex expPhaseI = phaseI.exp();
		Complex et2 = a.multiply(expPhaseI).divide(one.subtract(b.multiply(expPhaseI)));
		return Math.pow(et2.abs(), 2.0D);
	}
}
