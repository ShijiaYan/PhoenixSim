//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_nrz;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class OOK_NRZ_MachZehnderModulator extends Abstract_OOK_NRZ_Modulator {

	private double armLength;
	private double vPi;
	private double capModulator;
	private double bandwidthModulator;

	public OOK_NRZ_MachZehnderModulator(
			@ParamName(name = "Length of Arm of MZM (um)", default_ = "300") double armLength,
			@ParamName(name = "Pi Voltage (volts)", default_ = "1") double vPi,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "150") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (GHz)", default_ = "150") double bandwidthModulator) {
		this.armLength = armLength / 1000000.0D;
		this.vPi = vPi;
		this.capModulator = capModulator;
		this.bandwidthModulator = bandwidthModulator;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Vpp/Vpi", String.valueOf(this.vPi / this.vPi));
		map.put("Vpi", String.valueOf(this.vPi));
		map.put("Modulator BW", String.valueOf(this.bandwidthModulator));
		return map;
	}

	private double getPassivePowerPenalty(PhysicalParameterAndModelsSet modelSet) {
		double passiveLoss = 1.0D * modelSet.getWaveguideLoss() * 100.0D * this.armLength
				+ 2.0D * modelSet.getJunctionLoss();
		return passiveLoss;
	}

	private double getModInsertionLoss() {
		double vPeakToPeak = this.vPi;
		double modInsertionLoss = Math.pow(Math.sin(0.7853981633974483D * (1.0D + vPeakToPeak / this.vPi)), 2.0D);
		double modIL = -10.0D * Math.log10(modInsertionLoss);
		return modIL;
	}

	private double getModERPP() {
		double vPeakToPeak = this.vPi;
		if (vPeakToPeak / this.vPi != 1.0D) {
			double er = this.getModER();
			double ERPP = -10.0D * Math.log10((er - 1.0D) / (er + 1.0D));
			return ERPP;
		} else {
			return 0.0D;
		}
	}

	private Double getModER() {
		double vPeakToPeak = this.vPi;
		return (1.0D + Math.sin(1.5707963267948966D * vPeakToPeak / this.vPi))
				/ (1.0D - Math.sin(1.5707963267948966D * vPeakToPeak / this.vPi));
	}

	private double getModOOK() {
		double vPeakToPeak = this.vPi;
		if (vPeakToPeak / this.vPi != 1.0D) {
			double er = this.getModER();
			double OOKPP = -10.0D * Math.log10(0.5D * (er + 1.0D) / er);
			return OOKPP;
		} else {
			return 3.0D;
		}
	}

	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		PowerPenalty ookPP = new PowerPenalty("OOK", "Modulator", this.getModOOK());
		PowerPenalty erPP = new PowerPenalty("Extinction ratio", "Modulator", this.getModERPP());
		PowerPenalty ilPP = new PowerPenalty("Insertion loss", "Modulator", this.getModInsertionLoss());
		PowerPenalty passiveILPP = new PowerPenalty("Passive insertion loss", "Modulator",
				this.getPassivePowerPenalty(modelSet));
		Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<>();
		pair.setFirst(this.getModER());
		pair.setSecond(MoreArrays.getArrayList(new PowerPenalty[] { ookPP, erPP, ilPP, passiveILPP }));
		return pair;
	}

	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerPenalty passiveILPP = new PowerPenalty("Passive insertion loss", "Modulator",
				this.getPassivePowerPenalty(modelSet));
		return MoreArrays.getArrayList(new PowerPenalty[] { passiveILPP });
	}

	public ArrayList<PowerConsumption> getPowerConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		double dataRateGbs = linkFormat.getWavelengthRate() / 1.0E9D;
		double vPeakToPeak = this.vPi;
		double powerModulator = 0.25D * this.capModulator * vPeakToPeak * vPeakToPeak * dataRateGbs * dataRateGbs
				/ this.bandwidthModulator;
		double vBias = this.vPi / 2.0D;
		double Req = 310.0D;
		double statPower = Math.pow(vBias, 2.0D) / Req;
		double totPower = powerModulator + statPower * 1000000.0D;
		totPower /= 1000.0D;
		PowerConsumption pc = new PowerConsumption("Modulator", true, true, true, totPower);
		return MoreArrays.getArrayList(new PowerConsumption[] { pc });
	}

	public boolean modulatorHasThroughCapability() {
		return true;
	}
}
