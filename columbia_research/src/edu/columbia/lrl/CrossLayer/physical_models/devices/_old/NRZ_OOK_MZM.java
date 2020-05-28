package edu.columbia.lrl.CrossLayer.physical_models.devices._old;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.devices.FP.AbstractMZM;



public class NRZ_OOK_MZM extends AbstractMZM  {
	// Parameters
	private double armLength;  // length of each arm
	private double waveguideLoss; // dB/cm
	private double junctionLoss; // dB
	private double vPi;
	private double vPeakToPeak;
	private double capModulator ;
	private double bandwidthModulator ;
	private double dataRate ;
	
	// Initialize the parameters with the constructor
	public NRZ_OOK_MZM(
			@ParamName(name = "Modulation Rate (Gb/s)", default_ = "10") double dataRate,
			@ParamName(name = "Length of Arm of MZM (m)", default_ = "300e-6") double armLength,
			@ParamName(name = "Waveguide Loss Factor (dB/cm)", default_ = "5") double waveguideLoss,
			@ParamName(name = "Junction Loss (dB)", default_ = "0.5") double junctionLoss,
			@ParamName(name = "Pi Voltage (volts)", default_ = "1") double vPi,
//			@ParamName(name = "Peak-to-Peak Voltage (volts)", default_ = "1") double vPeakToPeak,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "150") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (GHz)", default_ = "30") double bandwidthModulator) {

		this.dataRate = dataRate ;
		this.armLength = armLength;
		this.waveguideLoss = waveguideLoss;
		this.junctionLoss = junctionLoss;
		this.vPi = vPi;
//		this.vPeakToPeak = vPeakToPeak;
		this.vPeakToPeak = vPi ;
		this.capModulator = capModulator ;
		this.bandwidthModulator = bandwidthModulator ;
	}
	// Write the map
	@Override
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = new SimpleMap<>();
		map.put("Peak-to-Peak Voltage (V)", vPeakToPeak + "");
		map.put("Waveguide Loss Factor (dB/cm)", waveguideLoss + "");
		map.put("Vpp/Vpi", vPeakToPeak/vPi + "");
		map.put("Vpi", vPi + "");
		map.put("Modulator BW", bandwidthModulator + "");
//		map.put("Effective index", effectiveIndex + "");
		return map;
	}
	
	// First we calculate the passive Optical Losses
	@Override
	public double getPassivePowerPenalties() {
		
		// This
		double passiveLoss = 1*waveguideLoss*100*armLength + 2*junctionLoss  ;
		double totPassiveLoss = passiveLoss ;

		return totPassiveLoss ;
		
	}
	
	@Override	
	public double getActivePowerPenalties() {
				
		// This 
		double modInsertionLoss =  Math.pow(Math.sin(Math.PI/4 * (1+ vPeakToPeak/vPi)), 2)  ;
		double modIL = -10*Math.log10(modInsertionLoss) ;
		
		if (vPeakToPeak/vPi != 1){
			
		double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;
		
		double ERPP = -10*Math.log10((er-1)/(er+1)) ;
		double OOKPP =  -10*Math.log10(0.5*(er+1)/er) ;
		
		return modIL+ERPP+OOKPP; }
		
		else {
			return 3 ;
		}
		
	}
	
	//******************************
	public double getModInsertionLoss(){
		double modInsertionLoss =  Math.pow(Math.sin(Math.PI/4 * (1+ vPeakToPeak/vPi)), 2)  ;
		double modIL = -10*Math.log10(modInsertionLoss) ;
		return modIL ;
	}
	
	public double getModERPP(){
		if (vPeakToPeak/vPi != 1){
			double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;	
			double ERPP = -10*Math.log10((er-1)/(er+1)) ;
			return ERPP; }
			else {
				return 0 ;
			}
	}
	
	public double getModOOK(){
		if (vPeakToPeak/vPi != 1){
			double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;
			double OOKPP =  -10*Math.log10(0.5*(er+1)/er) ;
			return OOKPP; }
			else {
				return 3 ;
			}
	}
	
	//******************************
	@Override
	public double getEffectiveModulationRate(){
		return dataRate*1e9;
	}
	
	@Override
	public double getModulationRate(){
		return dataRate * 1e9;
	}
	
	@Override
	public double getModulationRateGbps(){
		return dataRate ;
	}
	
	@Override
	public double getModulationDutyCycle(){
		return 1 ;
	}
	
	// Power and Energy Consumption
	@Override
	public double getModulatorPowerConsumption(){
		
//		double bandwidth = 0.67 * dataRate ;
//		double powerPulseCarver = 0.5 * capPulseCarver *vPulseCarver*vPulseCarver * dataRate*dataRate / bandwidthModulator ;
		double powerModulator = 0.25 * capModulator * vPeakToPeak * vPeakToPeak * dataRate*dataRate / bandwidthModulator ; // This is in micro-watts
		
		double vBias = vPi/2 ;
		double Req = 310 ;
		double statPower = Math.pow(vBias, 2)/Req ; // this is in watts
		double dynPower = powerModulator ;
		
//		double totPower = powerPulseCarver + powerModulator ; // micro watts
		double totPower = powerModulator + statPower * 1e6 ; // Double for differential driving voltage -- which we ignore (we consider single-arm drive)
		
		return totPower ;
//		return dynPower ;
	}



	
	
}








