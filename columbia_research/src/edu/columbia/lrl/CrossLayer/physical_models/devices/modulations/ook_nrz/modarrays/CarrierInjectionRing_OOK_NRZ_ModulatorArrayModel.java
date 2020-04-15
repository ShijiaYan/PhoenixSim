//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.modarrays;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz.Abstract_OOK_NRZ_Receiver;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.AbstractRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.QandERRingModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RateDependentRingPNJunctionDepletionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class CarrierInjectionRing_OOK_NRZ_ModulatorArrayModel extends AbstractRingBased_OOK_NRZ_ModulatorArrayModel {

	private double q;
	private double Neff;
	private double referenceER;
	private double relResShift;
	private double capacitance;
	private double voltageTemp;
	private double currentTemp;
	private double resShiftTemp;

	public CarrierInjectionRing_OOK_NRZ_ModulatorArrayModel(Abstract_OOK_NRZ_Receiver ookReceiver,
			@ParamName(name = "Modulator ring model", defaultClass_ = CarrierInjectionRing_OOK_NRZ_ModulatorArrayModel.DefaulInjectiontModulatorRing.class) AbstractRingModel absRing,
			@ParamName(name = "Power model", defaultClass_ = RateDependentRingPNJunctionDepletionDriverPowerModel.class) AbstractRingPNJunctionDriverPowerModel powerModel,
			@ParamName(name = "Desired shift(relative to half spacing, -1 for max)", default_ = "-1") double resShift,
			@ParamName(name = "Capacitance(fF)", default_ = "145") double capacitance) {
		super(ookReceiver, powerModel);
		this.q = absRing.getQ();
		this.referenceER = absRing.getER();
		this.relResShift = resShift;
		this.capacitance = capacitance;
		this.Neff = absRing.getNeff();
	}

	public Map<String, String> getRingBasedArrayParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Modulator Q", String.valueOf(this.q));
		map.put("Reference ER (dB)", String.valueOf(this.referenceER));
		map.put("Relative res shift", String.valueOf(this.relResShift));
		map.put("Driving voltage", String.valueOf(this.voltageTemp));
		map.put("Driving current", String.valueOf(this.currentTemp));
		map.put("Res shift in nm", String.valueOf(this.resShiftTemp));
		return map;
	}

	public ArrayList<PowerPenalty> getPassbyModulationBankPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		return MoreArrays
				.getArrayList(new PowerPenalty[] { this.getInsertionLossPowerPenalties(modelSet, linkFormat, false) });
	}

	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		Constants ct = modelSet.getConstants();
		int wavelengths = linkFormat.getNumberOfChannels();
		double resLambdaNm = ct.getCenterWavelength() * 1.0E9D;
		double channelSpacingNm = ct.getFullFSR() / (double) wavelengths * 1.0E9D;
		double[] calc = this.calculateQPrime_resShift_erWithCarrier(ct, linkFormat);
		double Qprime = calc[0];
		double resShiftNm = calc[1];
		double extinctionWithCarriers = calc[2];
		double powON = this.getTransmission(resLambdaNm, resLambdaNm + resShiftNm, Qprime, extinctionWithCarriers);
		double powOFF = this.getTransmission(resLambdaNm, resLambdaNm, this.q, this.referenceER);
		double er = powON / powOFF;
		double erPP = -10.0D * Math.log10((er - 1.0D) / (er + 1.0D));
		double ookPP = -10.0D * Math.log10((er + 1.0D) / (2.0D * er));
		PowerPenalty erPP_ = new PowerPenalty("Extinction ratio", "Modulator", erPP);
		PowerPenalty ookPP_ = new PowerPenalty("OOK", "Modulator", ookPP);
		PowerPenalty insertionLoss = this.getInsertionLossPowerPenalties(modelSet, linkFormat, true);
		double xtalk = this.getTransmission(resLambdaNm - channelSpacingNm, resLambdaNm + resShiftNm, Qprime,
				this.referenceER);
		double xtalkPP = -5.0D * Math.log10(xtalk);
		PowerPenalty xtalkPenalty = new PowerPenalty("Crosstalk", "Modulator", xtalkPP);
		Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<>();
		pair.setFirst(er);
		pair.setSecond(MoreArrays.getArrayList(new PowerPenalty[] { erPP_, ookPP_, xtalkPenalty, insertionLoss }));
		return pair;
	}

	public double getResShift(Constants ct, AbstractLinkFormat linkFormat) {
		int wavelengths = linkFormat.getNumberOfChannels();
		double channelSpacing = ct.getFullFSR() / (double) wavelengths * 1.0E9D;
		double resShift = 0.0D;
		if (this.relResShift >= 0.0D && this.relResShift <= 1.0D) {
			resShift = channelSpacing / 2.0D * this.relResShift;
		} else {
			resShift = channelSpacing / 2.0D;
			if (resShift > 2.0D) { resShift = 2.0D; }
		}

		resShift = -resShift;
		return resShift;
	}

	private double[] calculateQPrime_resShift_erWithCarrier(Constants ct, AbstractLinkFormat linkFormat) {
		double resHz = ct.getCenterFrequency();
		double effectiveIndex = this.Neff;
		double resLambda = ct.getCenterWavelength() * 1.0E9D;
		double resShift = this.getResShift(ct, linkFormat);
		double t0 = Math.pow(10.0D, -this.referenceER / 10.0D);
		double FWHM = resHz / this.q;
		double tau_i = 2.0D / (3.141592653589793D * FWHM * (1.0D + Math.sqrt(t0)));
		double tau_c = (1.0D + Math.sqrt(t0)) / (1.0D - Math.sqrt(t0)) * tau_i;
		double alpha = 2.0D * effectiveIndex / (ct.getSpeedOfLight() * 100.0D * tau_i);
		double Gamma = 0.8D;
		double DeltaNsilicon = effectiveIndex / Gamma * resShift / resLambda;
		double deltaAlpha = this.getDeltaAlphaFromDeltaN(DeltaNsilicon);
		double alphaPrime = alpha + deltaAlpha;
		double tau_i_prime = 2.0D * effectiveIndex / (ct.getSpeedOfLight() * 100.0D * alphaPrime);
		double FWHMprime = 0.3183098861837907D * (1.0D / tau_i_prime + 1.0D / tau_c);
		double resHzPrime = ct.getSpeedOfLight() / ((resLambda + resShift) * 1.0E-9D);
		double Qprime = resHzPrime / FWHMprime;
		double t0Prime = Math.pow((1.0D - tau_i_prime / tau_c) / (1.0D + tau_i_prime / tau_c), 2.0D);
		double extinctionWithCarriers = -10.0D * Math.log10(t0Prime);
		return new double[] { Qprime, resShift, extinctionWithCarriers };
	}

	public PowerPenalty getInsertionLossPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, boolean active) {
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
		PowerPenalty insertionLossPP = new PowerPenalty("Insertion loss", "Modulator", insertionLoss);
		return insertionLossPP;
	}

	public double getTransmission(double lambda, double resLambda, double qFactor, double resExtinctionDB) {
		double FWHM = resLambda / qFactor;
		double resExtinction = Math.pow(10.0D, -resExtinctionDB / 10.0D);
		double trans = (Math.pow(2.0D * (lambda - resLambda) / FWHM, 2.0D) + resExtinction)
				/ (Math.pow(2.0D * (lambda - resLambda) / FWHM, 2.0D) + 1.0D);
		return trans;
	}

	public double getDrivingCapacitance() {
		return this.capacitance;
	}

	public double getDrivingVoltage(Constants ct, AbstractLinkFormat linkFormat) {
		double current = this.getCurrentFromResShift(ct, linkFormat);
		this.currentTemp = current;
		this.voltageTemp = this.getVoltageFromCurrent(current);
		return this.voltageTemp;
	}

	private double getCurrentFromResShift(Constants ct, AbstractLinkFormat linkFormat) {
		double resShift = this.getResShift(ct, linkFormat);
		this.resShiftTemp = resShift;
		return Math.pow(resShift, 4.0D) * 0.2061D + Math.pow(resShift, 3.0D) * 0.76063D
				+ Math.pow(resShift, 2.0D) * 1.1475D + resShift * 0.076D;
	}

	private double getVoltageFromCurrent(double ImA) {
		double R = 0.25D;
		double Vbi = 0.7D;
		double n = 0.62D;
		double Vthermal = 0.026D;
		double Is = 9.0E-5D;
		double V = Vbi + R * ImA + 1.0D / n * Vthermal * Math.log(ImA / Is + 1.0D);
		if (Double.isNaN(V)) {
			throw new WrongExperimentException();
		} else {
			return V;
		}
	}

	private double getDeltaAlphaFromDeltaN(double DeltaN) {
		double DeltaAlpha = -2347.4D * DeltaN;
		return DeltaAlpha;
	}

	public static class DefaulInjectiontModulatorRing extends QandERRingModel {

		public DefaulInjectiontModulatorRing(@ParamName(name = "Modulator Q", default_ = "12000") double q,
				@ParamName(name = "Modulator Ring Neff", default_ = "4.393") double Neff,
				@ParamName(name = "Modulator ER in dB", default_ = "20") double er) {
			super(q, Neff, er);
		}
	}
}
