package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;



public class RZ_OOK_MZM extends AbstractMZM  {
	// Parameters
	private double armLength;  // length of each arm
	private double waveguideLoss; // dB/cm
	private double junctionLoss; // dB
	private double vPi;
//	private double vPeakToPeak;
	private double capModulator ;
	private double bandwidthModulator ;
	private double dataRate ;
	private double dutyCycle ;
//	private double vPulseCarver ;
	private double capPulseCarver ;
	
	// Initialize the parameters with the constructor
	public RZ_OOK_MZM(
			@ParamName(name = "Modulation Rate (Gb/s)", default_ = "10") double dataRate,
			@ParamName(name = "Length of Arm of MZM (m)", default_ = "300e-6") double armLength,
			@ParamName(name = "Waveguide Loss Factor (dB/cm)", default_ = "5") double waveguideLoss,
			@ParamName(name = "Junction Loss (dB)", default_ = "0.5") double junctionLoss,
			@ParamName(name = "Duty Cyle of RZ Modulation", default_ = "0.33") double dutyCycle,
			@ParamName(name = "Pi Voltage (volts)", default_ = "1") double vPi,
//			@ParamName(name = "Pulse Carver drive voltage (volts)", default_ = "1") double vPulseCarver,
//			@ParamName(name = "Peak-to-Peak Voltage (volts)", default_ = "1") double vPeakToPeak,
			@ParamName(name = "Pulse Carver Capacitance (fF)", default_ = "150") double capPulseCarver,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "150") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (GHz)", default_ = "150") double bandwidthModulator) {

		this.dataRate = dataRate ;
		this.armLength = armLength;
		this.waveguideLoss = waveguideLoss;
		this.junctionLoss = junctionLoss;
		this.vPi = vPi;
		this.dutyCycle = dutyCycle ;
//		this.vPeakToPeak = vPeakToPeak;
//		this.vPulseCarver = vPulseCarver ;
//		this.vPeakToPeak = vPi ;
		this.capModulator = capModulator ;
		this.capPulseCarver = capPulseCarver ;
		this.bandwidthModulator = bandwidthModulator ;
		
//		this.vPulseCarver = vPi ;
//		this.vPeakToPeak = vPi ;
	}
	// Write the map
	@Override
	public Map<String, String> getAllParameters() {
		double vPeakToPeak = vPi;
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
		double passiveLoss = 1*waveguideLoss*100*armLength + 2*junctionLoss  ;
		double lossDutyCycle = -10*Math.log10(dutyCycle) ; 
		double totPassiveLoss = passiveLoss + lossDutyCycle ;

		return totPassiveLoss ;
		
	}
	
	@Override	
	public double getActivePowerPenalties() {
		
		double vPulseCarver = vPi;
		double vPeakToPeak = vPi;
		
		double pulseCarverLoss =  Math.pow(Math.sin(Math.PI * vPulseCarver/(2*vPi)), 2)  ;
		double pulseCarverPP = -10*Math.log10(pulseCarverLoss) ;
				
		// This 
		double modInsertionLoss =  Math.pow(Math.sin(Math.PI/4 * (1+ vPeakToPeak/vPi)), 2)  ;
		double modIL = -10*Math.log10(modInsertionLoss) ;
		
		if (vPeakToPeak/vPi != 1){
			
		double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;
		
		double ERPP = -10*Math.log10((er-1)/(er+1)) ;
		double OOKPP =  -10*Math.log10(0.5*(er+1)/er) ;
		
		return (modIL+ERPP+OOKPP+pulseCarverPP) ; }
		
		else {
			return (3 + pulseCarverPP) ;
		}
		
	}
	
	//******************************
	public double getModInsertionLoss(){
		double vPeakToPeak = vPi;		
		
		double modInsertionLoss =  Math.pow(Math.sin(Math.PI/4 * (1+ vPeakToPeak/vPi)), 2)  ;
		double modIL = -10*Math.log10(modInsertionLoss) ;
		return modIL ;
	}
	
	public double getModERPP(){
		double vPeakToPeak = vPi;		
		
		if (vPeakToPeak/vPi != 1){
			double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;	
			double ERPP = -10*Math.log10((er-1)/(er+1)) ;
			return (ERPP) ; }
			else {
				return 0 ;
			}
	}
	
	public double getModOOK(){
		double vPeakToPeak = vPi;		
		
		if (vPeakToPeak/vPi != 1){
			double er = (1 + Math.sin(Math.PI/2 * vPeakToPeak/vPi))/(1 - Math.sin(Math.PI/2 * vPeakToPeak/vPi)) ;
			double OOKPP =  -10*Math.log10(0.5*(er+1)/er) ;
			return (OOKPP) ; }
			else {
				return 3 ;
			}
	}
	
	//******************************
	@Override
	public double getEffectiveModulationRate(){
		return (dataRate*1e9/dutyCycle ) ;
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
		
		double vPulseCarver = vPi;
		double vPeakToPeak = vPi;		
		
		double vBias = vPi/2 ;
		double Req = 310 ;
		double statPower = Math.pow(vBias, 2)/Req ; // this is in watts
		
		double powerPulseCarver = 0.5 * capPulseCarver *vPulseCarver*vPulseCarver * dataRate*dataRate / bandwidthModulator ;
		double powerModulator = 0.25 * capModulator * vPeakToPeak * vPeakToPeak * dataRate*dataRate / bandwidthModulator ;
		double dynPower = powerPulseCarver + powerModulator ;
		
		double totPower = powerPulseCarver + powerModulator + statPower * 1e6 ; // micro watts
		
		return totPower ;
//		return dynPower ;
	}



	
	
}








