package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;



public class NRZ_QPSK_MZM extends AbstractMZM  {
	// Parameters
	private double armLength;  // length of each arm
	private double waveguideLoss; // dB/cm
	private double junctionLoss; // dB
	private double dutyCycle; // between 0 and 1 (NOT in percent)
	private double vPi;
//	private double vPulseCarver;
	private double vPeakToPeak;
//	private double capPulseCarver;
	private double capModulator ;
	private double bandwidthModulator ;
	private double dataRate ;
	private double phaseShifterLoss ;
	
	// Initialize the parameters with the constructor
	public NRZ_QPSK_MZM(
			@ParamName(name = "Modulation Rate (Gb/s)", default_ = "10") double dataRate,
			@ParamName(name = "Length of Arm of MZM (m)", default_ = "300e-6") double armLength,
			@ParamName(name = "Waveguide Loss Factor (dB/cm)", default_ = "5") double waveguideLoss,
			@ParamName(name = "Junction Loss (dB)", default_ = "0.5") double junctionLoss,
			@ParamName(name = "Phase Shifter Loss (dB)", default_ = "0.5") double phaseShifterLoss,
//			@ParamName(name = "Duty Cyle of RZ Pulses", default_ = "0.33") double dutyCycle,
			@ParamName(name = "Pi Voltage (volts)", default_ = "1") double vPi,
//			@ParamName(name = "Pulse Carver drive voltage (volts)", default_ = "1") double vPulseCarver,
//			@ParamName(name = "Peak-to-Peak Voltage (volts)", default_ = "2") double vPeakToPeak,
//			@ParamName(name = "Pulse Carver Capacitance (fF)", default_ = "150") double capPulseCarver,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "150") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (GHz)", default_ = "150") double bandwidthModulator) {

		this.dataRate = dataRate ;
		this.armLength = armLength;
		this.waveguideLoss = waveguideLoss;
		this.junctionLoss = junctionLoss;
		this.phaseShifterLoss = phaseShifterLoss ;
		this.dutyCycle = 1 ;
		this.vPi = vPi;
//		this.vPulseCarver = vPulseCarver;
//		this.vPeakToPeak = vPeakToPeak;
//		this.capPulseCarver = capPulseCarver ;
		this.capModulator = capModulator ;
		this.bandwidthModulator = bandwidthModulator ;
		
		this.vPeakToPeak = 2*vPi ;
//		this.vPulseCarver = vPi ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Peak-to-Peak Voltage (V)", vPeakToPeak + "");
		map.put("Waveguide Loss Factor (dB/cm)", waveguideLoss + "");
		map.put("Vpp/Vpi", vPeakToPeak/vPi + "");
		map.put("Vpi", vPi + "");
		map.put("Modulator BW", bandwidthModulator + "");
		return map;
	}

	
	// First we calculate the passive Optical Losses
	@Override
	public double getPassivePowerPenalties() {
		
		// This
		double insertionLoss = 2*waveguideLoss*100*armLength + 6*junctionLoss + phaseShifterLoss  ;
		double lossDutyCycle = -10*Math.log10(dutyCycle) ; 
		double totPassiveLoss = insertionLoss + lossDutyCycle ;

		return totPassiveLoss ;
		
	}
	
	@Override	
	public double getActivePowerPenalties() {
				
		// This 
//		double pulseCarverLoss =  Math.pow(Math.sin(Math.PI * vPulseCarver/(2*vPi)), 2)  ;
//		double pulseCarverPP = -10*Math.log10(pulseCarverLoss) ;
		
		double modulatorLoss = Math.pow(Math.sin(Math.PI * vPeakToPeak/(4*vPi)), 2) ;
		double modulatorPP = -10*Math.log10(modulatorLoss) ;
		
		double totPP =  modulatorPP ;
		
		return totPP ;
		
	}
	
	@Override
	public double getEffectiveModulationRate(){
		return (dataRate*1e9/dutyCycle )/2 ;
	}
	
	@Override
	public double getModulationRate(){
		return (dataRate * 1e9 ) ;
	}
	
	@Override
	public double getModulationRateGbps(){
		return dataRate ;
	}
	
	@Override
	public double getModulationDutyCycle(){
		return dutyCycle ;
	}
	
	// Power and Energy Consumption
	@Override
	public double getModulatorPowerConsumption(){
		
		double vBias = vPi * 2 ;
		double Req = 310 ;
		double statPower = Math.pow(vBias, 2)/Req ; // this is in watts
		
		double effdataRate = 0.5 * dataRate ;
//		double powerPulseCarver = 0.5 * capPulseCarver *vPulseCarver*vPulseCarver * dataRate*dataRate / bandwidthModulator ; // pulse carver is independent of modulation format
		double powerModulator = 0.25 * capModulator * vPeakToPeak * vPeakToPeak * effdataRate*effdataRate / bandwidthModulator ;
		double dynPower =  2* powerModulator ;
		
		double totPower =  2*powerModulator + 2 * statPower * 1e6 ; // micro watts -- We have two modulators (I , Q)
		
		return totPower ;
//		return dynPower ;
	}


	
	
}








