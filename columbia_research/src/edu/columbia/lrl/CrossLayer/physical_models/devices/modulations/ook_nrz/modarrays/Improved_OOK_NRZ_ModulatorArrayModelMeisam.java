//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.SimpleRingPNJunctionDriverPowerModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;


public class Improved_OOK_NRZ_ModulatorArrayModelMeisam extends AbstractRingBased_OOK_NRZ_ModulatorArrayModel {

	private double ringRadius;
	private double q;
	private double qWithCarriers;
	private double passiveIL;
	private double referenceER;
	private double extinctionWithCarriers;
	private double capacitance;
	private double voltage;

	public Improved_OOK_NRZ_ModulatorArrayModelMeisam(Abstract_OOK_NRZ_Receiver ookReceiver,
			@ParamName(name = "Ring radius", default_ = "1.87e-6") double ringRadius,
			@ParamName(name = "Modulator Q", default_ = "12000") double q,
			@ParamName(name = "Modulator Q with carriers", default_ = "5600") double qWithCarriers,
			@ParamName(name = "Passive insertion loss (dB)", default_ = "0.5") double passiveIL,
			@ParamName(name = "Passive resonance extinction (dB)", default_ = "20") double referenceER,
			@ParamName(name = "Resonance extinction with carriers (dB)", default_ = "8") double extinctionWithCarriers,
			@ParamName(name = "Power model", defaultClass_ = SimpleRingPNJunctionDriverPowerModel.class) AbstractRingPNJunctionDriverPowerModel powerModel,
			@ParamName(name = "Driving voltage", default_ = "2") double voltage,
			@ParamName(name = "Capacitance (fF)", default_ = "145") double capacitance) {
		super(ookReceiver, powerModel);
		this.ringRadius = ringRadius;
		this.q = q;
		this.qWithCarriers = qWithCarriers;
		this.passiveIL = passiveIL;
		this.referenceER = referenceER;
		this.extinctionWithCarriers = extinctionWithCarriers;
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
		map.put("Extinction ratio (dB)", String.valueOf(this.extinctionWithCarriers));
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
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double resShiftHz = channelSpacing / 2.0D;
		double[] centerFreqs = new double[wavelengths];
		double transmission = 1.0D;

		for (int i = 0; i < centerFreqs.length; ++i) {
			centerFreqs[i] = centerHz + (double) i * ct.wavelengthsToFreqSpacing(wavelengths);
			if (i == 0) {
				centerFreqs[i] = centerHz + resShiftHz;
				transmission *= this.getTransmission(centerHz, centerFreqs[i], this.qWithCarriers,
						this.extinctionWithCarriers);
			} else {
				transmission *= this.getTransmission(centerHz, centerFreqs[i], this.q, this.referenceER);
			}
		}

		double insertionLoss = -10.0D * Math.log10(transmission);
		PowerPenalty insertionLossPP = new PowerPenalty("Array induced IL", "Modulator", insertionLoss);
		return MoreArrays.getArrayList(insertionLossPP);
	}

	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		Constants ct = modelSet.getConstants();
		double centerHz = ct.getSpeedOfLight() / ct.getCenterWavelength();
		int wavelengths = linkFormat.getNumberOfChannels();
		double channelSpacing = ct.wavelengthsToFreqSpacing(wavelengths);
		double resShiftHz = channelSpacing / 2.0D;
		double powON = this.getTransmission(centerHz, centerHz + resShiftHz, this.qWithCarriers,
				this.extinctionWithCarriers);
		double powOFF = this.getTransmission(centerHz, centerHz, this.q, this.referenceER);
		double er = powON / powOFF;
		double erPP = -10.0D * Math.log10((er - 1.0D) / (er + 1.0D));
		double ookPP = -10.0D * Math.log10((er + 1.0D) / (2.0D * er));
		PowerPenalty erPP_ = new PowerPenalty("Extinction ratio", "Modulator", erPP);
		PowerPenalty ookPP_ = new PowerPenalty("OOK", "Modulator", ookPP);
		double averageER = (this.referenceER + this.extinctionWithCarriers) / 2.0D;
		double xtalk = this.getTransmission(centerHz + channelSpacing - resShiftHz, centerHz, this.qWithCarriers,
				averageER);
		double xtalkPP = -10.0D * Math.log10(xtalk);
		PowerPenalty xtalkPenalty = new PowerPenalty("Crosstalk", "Modulator", xtalkPP);
		Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<>();
		pair.setFirst(er);
		pair.setSecond(MoreArrays.getArrayList(erPP_, ookPP_, xtalkPenalty));
		return pair;
	}

	public double getTransmission(double freq, double resFreq, double qFactor, double resExtinctionDB) {
		double FWHM = resFreq / qFactor;
		double resExtinction = Math.pow(10.0D, -resExtinctionDB / 10.0D);
		double trans = (Math.pow(2.0D * (freq - resFreq) / FWHM, 2.0D) + resExtinction)
				/ (Math.pow(2.0D * (freq - resFreq) / FWHM, 2.0D) + 1.0D);
		return trans;
	}
}
