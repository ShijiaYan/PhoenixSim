package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.AbstractModulator;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;



public class DPSKMachZehnderModulatorModel  {
	// Parameters
	private double armLength;  // length of each arm
	private double waveguideLoss; // dB/cm
	private double junctionLoss; // dB
	private double dutyCycle; // between 0 and 1 (NOT in percent)
	private double vPi;
	private double vPulseCarver;
	private double vPeakToPeak;
	private double capPulseCarver;
	private double capModulator ;
	private double bandwidthModulator ;
//	private double dataRate ;
	
	// Initialize the parameters with the constructor
	public DPSKMachZehnderModulatorModel(
//			@ParamName(name = "Modulation Rate (bits/sec)", default_ = "10e9") double dataRate,
			@ParamName(name = "Length of Arm of MZM (m)", default_ = "1000e-6") double armLength,
			@ParamName(name = "Waveguide Loss Factor (dB/cm)", default_ = "10") double waveguideLoss,
			@ParamName(name = "Junction Loss (dB)", default_ = "0.5") double junctionLoss,
			@ParamName(name = "Duty Cyle of RZ Modulation", default_ = "0.33") double dutyCycle,
			@ParamName(name = "Pi Voltage (volts)", default_ = "5") double vPi,
			@ParamName(name = "Pulse Carver drive voltage (volts)", default_ = "2") double vPulseCarver,
			@ParamName(name = "Peak-to-Peak Voltage (volts)", default_ = "2") double vPeakToPeak,
			@ParamName(name = "Pulse Carver Capacitance (fF)", default_ = "10") double capPulseCarver,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "10") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (Hz)", default_ = "10e9") double bandwidthModulator) {
		super(); // required by the super class (AbstractModulatorArrayModel)
//		this.dataRate = dataRate ;
		this.armLength = armLength;
		this.waveguideLoss = waveguideLoss;
		this.junctionLoss = junctionLoss;
		this.dutyCycle = dutyCycle;
		this.vPi = vPi;
		this.vPulseCarver = vPulseCarver;
		this.vPeakToPeak = vPeakToPeak;
		this.capPulseCarver = capPulseCarver ;
		this.capModulator = capModulator ;
		this.bandwidthModulator = bandwidthModulator ;
	}


	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Length of Arm of MZM (m)", armLength + "");
		map.put("Waveguide Loss Factor (dB/cm)", waveguideLoss + "");
		map.put("Junction Loss (dB)", junctionLoss + "");
		map.put("Duty Cycle of RZ Modulation", dutyCycle + "");
		map.put("Pi Voltage (volts)", vPi + "");
		map.put("Pulse Carver drive voltage (volts)", vPulseCarver + "");
		map.put("Peak-to-Peak Voltage (volts)", vPeakToPeak + "");
		map.put("Pulse Carver Capacitance", capPulseCarver + "");
		map.put("Modulator Capacitance", capModulator + "");
		map.put("Modulator 3dB Bandwidth (Hz)", bandwidthModulator + "");
		return map;
	}

	
	
	// First we calculate the passive Optical Losses
	public ArrayList<PowerPenalty> getPassivePowerPenalties(Constants ct, AbstractLinkFormat linkFormat) {
		
		// This
		double insertionLoss = 2*waveguideLoss*100*armLength + 4*junctionLoss  ;
		double lossDutyCycle = -10*Math.log10(dutyCycle) ; 
		
		// Adding IL 
		PowerPenalty insertionLossPP = new PowerPenalty(PowerPenalty.INSERTIONLOSS, AbstractModulator.MODULATOR,insertionLoss);
		PowerPenalty lossDutyCyclePP = new PowerPenalty(PowerPenalty.DUTY_CYCLE, AbstractModulator.MODULATOR,lossDutyCycle);

		return MoreArrays.getArrayList(insertionLossPP, lossDutyCyclePP);
		
	}
	
		
	public ArrayList<PowerPenalty> getActivePowerPenalties(Constants ct, AbstractLinkFormat linkFormat) {
				
		// This 
		double pulseCarverLoss =  Math.pow(Math.sin(Math.PI * vPulseCarver/vPi), 2)  ;
		double pulseCarverPP = -10*Math.log10(pulseCarverLoss) ;
		PowerPenalty pulseCarverPenalty = new PowerPenalty("Pulse Carver Penalty", AbstractModulator.MODULATOR,pulseCarverPP);

		return MoreArrays.getArrayList(pulseCarverPenalty);
		
	}
	
	public double getEffectiveModulationBitRate(AbstractLinkFormat linkFormat){
		return (linkFormat.getWavelengthRate()/dutyCycle ) ;
	}
	
	public List<PowerConsumption> getDevicePowerConsumptions(PhysicalParameterAndModelsSet modelSet,
            AbstractLinkFormat linkFormat) {
		ArrayList<PowerConsumption> pc = new ArrayList<PowerConsumption>(1);
		
		PowerConsumption p1 = new PowerConsumption("Not implemented for MZ", false, true, true, 0);
		pc.add(p1);
		return pc;
	}	
	
	// Power and Energy Consumption
	
	
}








