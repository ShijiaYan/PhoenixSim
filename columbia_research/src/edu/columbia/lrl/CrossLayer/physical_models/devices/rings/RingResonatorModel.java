//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.rings;

import java.util.Map;
import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.utils.SimpleMap;


public class RingResonatorModel {

	private double criticalCouplingDev;
	private RingResonatorModel.AbstractBendingLossModel bendLossMod;
	private double inputGapNm;
	private double outputGapNm;
	private double radiusMicron;
	private double radiusNm;
	private double radiusMicronInner;
	private double radiusNmInner;
	private double widthNm = 450.0D;
	private double lambdaNm = 1550.0D;
	private static final double N_Eff = 2.3596D;
	private double nGroup = 4.2873D;
	private double nSi = 3.4777D;
	private double nSiO2 = 1.444D;
	private double detuningNm;
	private double outOverInp;

	public RingResonatorModel(@ParamName(name = "Radius (micron)") double radiusMicron,
			@ParamName(name = "input gap size (nm) < 500 nm", default_ = "200") double inputGapNm,
			@ParamName(name = "output gap size (nm) < 500 nm", default_ = "200") double outputGapNm,
			@ParamName(name = "Detuning from Resonance (nm)", default_ = "0") double detuningNm,
			RingResonatorModel.AbstractBendingLossModel bendLossMod) {
		this.radiusMicron = radiusMicron;
		this.inputGapNm = inputGapNm;
		this.outputGapNm = outputGapNm;
		this.radiusMicronInner = radiusMicron - this.widthNm / 1000.0D / 2.0D;
		this.radiusNm = radiusMicron * 1000.0D;
		this.radiusNmInner = this.radiusMicronInner * 1000.0D;
		this.detuningNm = detuningNm;
		this.bendLossMod = bendLossMod;
	}

	@ConstructorDef(def = "Symmetric ring")
	public RingResonatorModel(@ParamName(name = "Radius (micron)") double radiusMicron,
			@ParamName(name = "input gap size (nm) < 500 nm", default_ = "200") double inputGapNm,
			@ParamName(name = "Detuning from Resonance (nm)", default_ = "0") double detuningNm,
			RingResonatorModel.AbstractBendingLossModel bendLossMod) {
		this.radiusMicron = radiusMicron;
		this.inputGapNm = inputGapNm;
		this.outputGapNm = inputGapNm;
		this.radiusMicronInner = radiusMicron - this.widthNm / 1000.0D / 2.0D;
		this.radiusNm = radiusMicron * 1000.0D;
		this.radiusNmInner = this.radiusMicronInner * 1000.0D;
		this.detuningNm = detuningNm;
		this.bendLossMod = bendLossMod;
	}

	@ConstructorDef(def = "Define ring with asymmetry coeff")
	public RingResonatorModel(@ParamName(name = "Radius (micron)") Double radiusMicron,
			@ParamName(name = "input gap size (nm) < 500 nm", default_ = "200") Double inputGapNm,
			@ParamName(name = "output gap/input gap", default_ = "1") Double outOverInp,
			@ParamName(name = "Detuning from Resonance (nm)", default_ = "0") double detuningNm,
			RingResonatorModel.AbstractBendingLossModel bendLossMod) {
		this.radiusMicron = radiusMicron;
		this.inputGapNm = inputGapNm;
		this.outputGapNm = inputGapNm * outOverInp;
		this.radiusMicronInner = radiusMicron - this.widthNm / 1000.0D / 2.0D;
		this.radiusNm = radiusMicron * 1000.0D;
		this.radiusNmInner = this.radiusMicronInner * 1000.0D;
		this.detuningNm = detuningNm;
		this.bendLossMod = bendLossMod;
		this.outOverInp = outOverInp;
	}

	@ConstructorDef(def = "Fix radius, output gap, coupling and detuning")
	public RingResonatorModel(@ParamName(name = "Radius (micron)") double radiusMicron,
			@ParamName(name = "output gap size (nm) < 500 nm", default_ = "200") Double outputGapNm,
			@ParamName(name = "Coupling ratio (1 = critical)", default_ = "1") Double critCouplingDev,
			@ParamName(name = "Detuning from Resonance (nm)", default_ = "0") double detuningNm,
			RingResonatorModel.AbstractBendingLossModel bendLossMod) {
		this.radiusMicron = radiusMicron;
		this.outputGapNm = outputGapNm;
		this.radiusMicronInner = radiusMicron - this.widthNm / 1000.0D / 2.0D;
		this.radiusNm = radiusMicron * 1000.0D;
		this.radiusNmInner = this.radiusMicronInner * 1000.0D;
		this.detuningNm = detuningNm;
		this.bendLossMod = bendLossMod;
		this.criticalCouplingDev = critCouplingDev;
		double tOut = this.getTcoeff(outputGapNm);
		double loss = this.getRoundTripLoss();
		double tin = tOut * this.criticalCouplingDev * Math.sqrt(loss);
		if (tin > 1.0D) {
			throw new WrongExperimentException("Impossible to get this ring at critical coupling");
		} else {
			double minGap = 0.0D;
			double maxGap = 4000.0D;
			double tempGap = 0.0D;
			double tInTemp = 1.0D;

			for (int iterationIndex = 0; Math.abs(tin - tInTemp) > 1.0E-5D && iterationIndex < 3000; ++iterationIndex) {
				tempGap = minGap + (maxGap - minGap) / 2.0D;
				tInTemp = this.getTcoeff(tempGap);
				if (tInTemp < tin) {
					minGap = tempGap;
				} else {
					maxGap = tempGap;
				}
			}

			this.inputGapNm = tempGap;
		}
	}

	public RingResonatorModel(@ParamName(name = "Radius (micron)") double radiusMicron,
			@ParamName(name = "input gap size (nm) < 500 nm", default_ = "200") double inputGapNm,
			@ParamName(name = "Coupling ratio (1 = critical)", default_ = "1") Double critCouplingDev,
			@ParamName(name = "Detuning from Resonance (nm)", default_ = "0") double detuningNm,
			RingResonatorModel.AbstractBendingLossModel bendLossMod) {
		this.radiusMicron = radiusMicron;
		this.inputGapNm = inputGapNm;
		this.radiusMicronInner = radiusMicron - this.widthNm / 1000.0D / 2.0D;
		this.radiusNm = radiusMicron * 1000.0D;
		this.radiusNmInner = this.radiusMicronInner * 1000.0D;
		this.detuningNm = detuningNm;
		this.bendLossMod = bendLossMod;
		this.criticalCouplingDev = critCouplingDev;
		double tIn = this.getTcoeff(inputGapNm);
		double loss = this.getRoundTripLoss();
		double tOut = tIn * Math.sqrt(1.0D / loss) / critCouplingDev;
		if (tOut > 1.0D) {
			throw new WrongExperimentException("Impossible to get this ring at critical coupling");
		} else {
			double minGap = 0.0D;
			double maxGap = 4000.0D;
			double tempGap = 0.0D;
			double tOutTemp = 1.0D;

			for (int iterationIndex = 0; Math.abs(tOut - tOutTemp) > 1.0E-5D
					&& iterationIndex < 3000; ++iterationIndex) {
				tempGap = minGap + (maxGap - minGap) / 2.0D;
				tOutTemp = this.getTcoeff(tempGap);
				if (tOutTemp < tOut) {
					minGap = tempGap;
				} else {
					maxGap = tempGap;
				}
			}

			this.outputGapNm = tempGap;
		}
	}

	public static double getNeff() {
		return 2.3596D;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap();
		map.put("Ring radius", String.valueOf(this.radiusMicron));
		map.put("input gap size", String.valueOf(this.inputGapNm));
		map.put("output gap size", String.valueOf(this.outputGapNm));
		map.put("Detuning from Resonance (nm)", String.valueOf(this.detuningNm));
		map.put("Alpha - bending model", this.bendLossMod.getClass().getSimpleName());
		map.put("Critical coupling dev", String.valueOf(this.criticalCouplingDev));
		map.putAll(this.bendLossMod.getAllParameters());
		map.put("out over inp", String.valueOf(this.outOverInp));
		return map;
	}

	public double getAlphaLossdBperCm() {
		return this.bendLossMod.getLossdBCm(this.radiusMicron);
	}

	public double getKappacoeff(double gapNm) {
		double aE = 0.177967D;
		double aO = 0.04991D;
		double gammaE = 0.011898D;
		double gammaO = 0.006601D;
		double xE = gammaE * (this.radiusNm + this.widthNm / 2.0D);
		double bE = Math.sqrt(6.283185307179586D * xE);
		double argE = aE / gammaE * Math.exp(-gammaE * gapNm) * bE;
		double xO = gammaO * (this.radiusNm + this.widthNm / 2.0D);
		double bO = Math.sqrt(6.283185307179586D * xO);
		double argO = aO / gammaO * Math.exp(-gammaO * gapNm) * bO;
		double kappa = Math.sin(3.141592653589793D / this.lambdaNm * (argE + argO));
		return kappa;
	}

	public double getTcoeff(double gapNm) {
		double k = this.getKappacoeff(gapNm);
		double t = Math.sqrt(1.0D - k * k);
		return t;
	}

	public double getRoundTripLossdB() {
		double radiusCm = this.radiusMicron * 1.0E-4D;
		double LossdB = 6.283185307179586D * radiusCm * this.getAlphaLossdBperCm();
		return LossdB;
	}

	public double getRoundTripLoss() {
		double LossdB = this.getRoundTripLossdB();
		double Loss = Math.pow(10.0D, -LossdB / 10.0D);
		return Loss;
	}

	public double getFSRnm() {
		double FSRnm = this.lambdaNm * this.lambdaNm / (6.283185307179586D * this.radiusNm * this.nGroup);
		return FSRnm;
	}

	public double get3dbBWhz() {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double A = 6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM;
		double x = this.getInputKappacoeff() * this.getInputKappacoeff() / A;
		double y = this.getOutputKappacoeff() * this.getOutputKappacoeff() / A;
		double c = 3.0E8D;
		double tauI = 2.0D * this.nGroup / (c * alphaM);
		double FWHMihz = 1.0D / (3.141592653589793D * tauI);
		double FWHMhz = FWHMihz * (1.0D + x + y);
		return FWHMhz;
	}

	public double get3dbBWnm() {
		double FWHMhz = this.get3dbBWhz();
		double c = 3.0E8D;
		double FWHMnm = this.lambdaNm * this.lambdaNm * 1.0E-9D / c * FWHMhz;
		return FWHMnm;
	}

	public double getQualityFactor() {
		double Q = this.lambdaNm / this.get3dbBWnm();
		return Q;
	}

	public double getFinesse() {
		double Finesse = this.getFSRnm() / this.get3dbBWnm();
		return Finesse;
	}

	public double getDropTransmission(double detuningNm) {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double A = 6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM;
		double x = this.getInputKappacoeff() * this.getInputKappacoeff() / A;
		double y = this.getOutputKappacoeff() * this.getOutputKappacoeff() / A;
		double D0 = 4.0D * x * y / Math.pow(1.0D + x + y, 2.0D);
		double dropTrans = D0 / (1.0D + Math.pow(2.0D * detuningNm / this.get3dbBWnm(), 2.0D));
		return dropTrans;
	}

	public double getDropTransmissiondB(double detuningNm) {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double A = 6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM;
		double L = Math.exp(-A);
		double kin = this.getInputKappacoeff();
		double kout = this.getOutputKappacoeff();
		double kin2 = kin * kin;
		double kout2 = kout * kout;
		double tin2 = 1.0D - kin2;
		double tout2 = 1.0D - kout2;
		double tin = Math.sqrt(tin2);
		double tout = Math.sqrt(tout2);
		double result2 = kin * kin * kout * kout * Math.sqrt(L) / Math.pow(1.0D - tin * tout * Math.sqrt(L), 2.0D);
		double x = this.getInputKappacoeff() * this.getInputKappacoeff() / A;
		double y = this.getOutputKappacoeff() * this.getOutputKappacoeff() / A;
		double D0 = 4.0D * x * y / Math.pow(1.0D + x + y, 2.0D);
		double divider = 1.0D + Math.pow(2.0D * detuningNm / this.get3dbBWnm(), 2.0D);
		double var10000 = D0 / divider;
		return 10.0D * Math.log10(result2);
	}

	public double getDropInsertionLossdB() {
		double dropILdB = -10.0D * Math.log10(this.getDropTransmission(0.0D));
		return dropILdB;
	}

	public double getThroughTransmission(double detuningNm) {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double A = 6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM;
		double x = this.getInputKappacoeff() * this.getInputKappacoeff() / A;
		double y = this.getOutputKappacoeff() * this.getOutputKappacoeff() / A;
		double T0 = Math.pow(1.0D + y - x, 2.0D) / Math.pow(1.0D + x + y, 2.0D);
		double ThruTrans = (T0 + Math.pow(detuningNm / this.get3dbBWnm(), 2.0D))
				/ (1.0D + Math.pow(detuningNm / this.get3dbBWnm(), 2.0D));
		return ThruTrans;
	}

	public double getThroughTransmissiondB(double detuningNm) {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double A = 6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM;
		double x = this.getInputKappacoeff() * this.getInputKappacoeff() / A;
		double y = this.getOutputKappacoeff() * this.getOutputKappacoeff() / A;
		double T0 = Math.pow(1.0D + y - x, 2.0D) / Math.pow(1.0D + x + y, 2.0D);
		double ThruTrans = (T0 + Math.pow(detuningNm / this.get3dbBWnm(), 2.0D))
				/ (1.0D + Math.pow(detuningNm / this.get3dbBWnm(), 2.0D));
		return 10.0D * Math.log10(ThruTrans);
	}

	public double getThroughTransmissionPeriodic(double detuningNm) {
		double alphaM = this.getAlphaLossdBperCm() * 23.0D;
		double Lsquared = Math.exp(-6.283185307179586D * this.radiusMicron * 1.0E-6D * alphaM);
		double L = Math.sqrt(Lsquared);
		double tIn = this.getInputTcoeff();
		double tOut = this.getOutputTcoeff();
		double A = L * tIn * tOut;
		double B = L * tOut;
		double m = (double) Math.round(6.283185307179586D * this.radiusNm * this.nGroup / this.lambdaNm);
		double phi = 6.283185307179586D * m * (detuningNm / this.lambdaNm);
		double numerator = tIn * tIn + B * B - 2.0D * A * Math.cos(phi);
		double denomerator = 1.0D + A * A - 2.0D * A * Math.cos(phi);
		double ThruTrans = numerator / denomerator;
		return ThruTrans;
	}

	public double getThruInsertionLossdB() {
		double ThruILdB = -10.0D * Math.log10(this.getThroughTransmission(0.0D));
		return ThruILdB;
	}

	public double getERdB() {
		double fsr = this.getFSRnm();
		return this.getThruInsertionLossdB() - this.getThroughTransmissiondB(fsr / 2.0D);
	}

	public double getInputGapNm() {
		return this.inputGapNm;
	}

	public double getOutputGapNm() {
		return this.outputGapNm;
	}

	public double getInputTcoeff() {
		double tInput = this.getTcoeff(this.inputGapNm);
		return tInput;
	}

	public double getOutputTcoeff() {
		double tOutput = this.getTcoeff(this.outputGapNm);
		return tOutput;
	}

	public double getInputKappacoeff() {
		double kInput = this.getKappacoeff(this.inputGapNm);
		return kInput;
	}

	public double getOutputKappacoeff() {
		double kOutput = this.getKappacoeff(this.outputGapNm);
		return kOutput;
	}

	public double getDetuningNm() {
		return this.detuningNm;
	}

	public abstract static class AbstractBendingLossModel {

		public AbstractBendingLossModel() {
		}

		public abstract double getLossdBCm(double var1);

		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap(new String[0]);
		}
	}

	public static class ExponentialIdeal extends RingResonatorModel.AbstractBendingLossModel {

		double K;

		public ExponentialIdeal(double K) {
			this.K = K;
		}

		public double getLossdBCm(double radiusMicron) {
			double a = this.K;
			double b = -2.55D;
			double c = 1.2D;
			return c + a * Math.exp(b * radiusMicron);
		}

		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap(new Object[] { "K", this.K });
		}
	}

	public static class ExponentialUBCModel extends RingResonatorModel.AbstractBendingLossModel {

		public ExponentialUBCModel() {
		}

		public double getLossdBCm(double radiusMicron) {
			double a = 50130.0D;
			double b = -1.185D;
			double c = 2.0D;
			return c + a * Math.exp(b * radiusMicron);
		}
	}

	public static class ExponentialUBCModelOpt extends RingResonatorModel.AbstractBendingLossModel {

		public ExponentialUBCModelOpt() {
		}

		public double getLossdBCm(double radiusMicron) {
			double a = 163700.0D;
			double b = -1.566D;
			double c = 1.2D;
			return c + a * Math.exp(b * radiusMicron);
		}
	}

	public static class NoExtraBendingLossModel extends RingResonatorModel.AbstractBendingLossModel {

		protected double alphaProp;

		public NoExtraBendingLossModel(
				@ParamName(name = "Ring Propagation Loss (dB/cm)", default_ = "1") double alphaProp) {
			this.alphaProp = alphaProp;
		}

		public double getLossdBCm(double radiusMicron) {
			return this.alphaProp;
		}

		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap(new Object[] { "Alpha prop in ring", this.alphaProp });
		}
	}

	public static class PowerlawBendingLossModel extends RingResonatorModel.AbstractBendingLossModel {

		double a;
		double b;
		double c;

		public PowerlawBendingLossModel(double a, double b, double c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public double getLossdBCm(double radiusMicron) {
			double temp = Math.pow(radiusMicron, -this.b);
			temp *= this.a;
			temp += this.c;
			return temp;
		}

		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap(new Object[] { "a", this.a, "b", this.b, "c", this.c });
		}
	}

	public static class UBCModel extends RingResonatorModel.AbstractBendingLossModel {

		public UBCModel() {
		}

		public double getLossdBCm(double radiusMicron) {
			double a = 4.8411E7D;
			double b = 7.8016D;
			double c = 2.0D;
			double alphaBending = a * Math.pow(1.0D / radiusMicron, b);
			return alphaBending + c;
		}
	}

	public static class UBCModelOptimistic extends RingResonatorModel.AbstractBendingLossModel {

		public UBCModelOptimistic() {
		}

		public double getLossdBCm(double radiusMicron) {
			double a = 1.044E9D;
			double b = 10.13D;
			double c = 1.2D;
			double alphaBending = a * Math.pow(1.0D / radiusMicron, b);
			return alphaBending + c;
		}
	}
}
