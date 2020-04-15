package edu.columbia.lrl.CrossLayer.physical_models.devices.modulations.ook_rz;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.PropertyMap;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.Pair;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;

public class OOK_RZ_MachZehnderModulator extends Abstract_OOK_RZ_Modulator {
	
	// Parameters
	private double armLength;  // length of each arm
	private double vPi;
	private double capModulator ;
	private double bandwidthModulator ;
	private double dutyCycle ;
	private double capPulseCarver ;	
	
	public OOK_RZ_MachZehnderModulator(
			@ParamName(name = "Length of Arm of MZM (um)", default_ = "300") double armLength,
			@ParamName(name = "Duty Cyle of RZ Modulation", default_ = "0.33") double dutyCycle,
			@ParamName(name = "Pi Voltage (volts)", default_ = "1") double vPi,
			@ParamName(name = "Pulse Carver Capacitance (fF)", default_ = "150") double capPulseCarver,
			@ParamName(name = "Modulator Capacitance (fF)", default_ = "150") double capModulator,
			@ParamName(name = "Modulator 3dB Bandwidth (GHz)", default_ = "150") double bandwidthModulator) {
		
		this.armLength = armLength/1e6d;
		this.vPi = vPi;
		this.dutyCycle = dutyCycle;
		this.capModulator = capModulator;
		this.capPulseCarver = capPulseCarver;
		this.bandwidthModulator = bandwidthModulator;		
	}

	@Override
	public Pair<Double, ArrayList<PowerPenalty>> getModulationERAndPowerPenalties(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		PowerPenalty ookPP = new PowerPenalty(PowerPenalty.OOK, MODULATOR, getModOOK());
		PowerPenalty erPP = new PowerPenalty(PowerPenalty.ER, MODULATOR, getModERPP());
		PowerPenalty ilPP = new PowerPenalty(PowerPenalty.INSERTIONLOSS, MODULATOR, getModInsertionLoss());		

		Pair<Double, ArrayList<PowerPenalty>> pair = new Pair<Double, ArrayList<PowerPenalty>>();
		pair.setFirst(getModER());
		pair.setSecond(MoreArrays.getArrayList(ookPP, erPP, ilPP, getPassivePowerPenalty(modelSet), getDutyCycleLoss()));	
		return pair;	
	}



	@Override
	public ArrayList<PowerConsumption> getPowerConsumption(
			PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		double dataRate = linkFormat.getWavelengthRate();
		
		double vPulseCarver = vPi;
		double vPeakToPeak = vPi;		
		
		double vBias = vPi/2 ;
		double Req = 310 ;
		double statPower = Math.pow(vBias, 2)/Req ; // this is in watts
		
		double powerPulseCarver = 0.5 * capPulseCarver *vPulseCarver*vPulseCarver * dataRate*dataRate / bandwidthModulator ;
		double powerModulator = 0.25 * capModulator * vPeakToPeak * vPeakToPeak * dataRate*dataRate / bandwidthModulator ;
		double dynPower = powerPulseCarver + powerModulator ;
		
		double totPower = powerPulseCarver + powerModulator + statPower * 1e6 ; // micro watts
		
		return MoreArrays.getArrayList(new PowerConsumption(MODULATOR, true, true, true, totPower));
	}

	@Override
	public boolean modulatorHasThroughCapability() {
		return true;
	}

	@Override
	public ArrayList<PowerPenalty> getPassbyPowerPenalties(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return MoreArrays.getArrayList(getPassivePowerPenalty(modelSet));
	}
	
	public PowerPenalty getPassivePowerPenalty(PhysicalParameterAndModelsSet modelSet) {	
		double waveguideLoss = modelSet.getWaveguideLoss();
		double junctionLoss = modelSet.getJunctionLoss();
		return new PowerPenalty(PowerPenalty.PASSIVEINSERTIONLOSS, MODULATOR, waveguideLoss*100*armLength + 2*junctionLoss);
	}	
	
	private PowerPenalty getDutyCycleLoss() {
		return new PowerPenalty(PowerPenalty.DUTY_CYCLE, MODULATOR, -10*Math.log10(dutyCycle));
	}
	
	private double getModInsertionLoss(){
		double vPeakToPeak = vPi;		
		
		double modInsertionLoss =  Math.pow(Math.sin(Math.PI/4 * (1+ vPeakToPeak/vPi)), 2)  ;
		double modIL = -10*Math.log10(modInsertionLoss) ;
		return modIL ;
	}
	
	private Double getModER() {
		double vPeakToPeak = vPi;	

		double sin = Math.sin(Math.PI/2 * vPeakToPeak/vPi);		
		return (1 + sin)/(1 - sin);
	}	
	
	private double getModERPP(){
		double vPeakToPeak = vPi;		
		
		if (vPeakToPeak/vPi != 1){
	
			double er = getModER();	
			double ERPP = -10*Math.log10((er-1)/(er+1)) ;
			return ERPP; 
		} else {
			return 0 ;
		}
	}
	
	private double getModOOK(){
		double vPeakToPeak = vPi;		
		
		if (vPeakToPeak/vPi != 1){
			double er = getModER();
			double OOKPP =  -10*Math.log10(0.5*(er+1)/er) ;
			return OOKPP; 
		} else {
			return 3 ;
		}
	}	

	@Override
	public Map<String, String> getAllParameters() {
		PropertyMap map = new PropertyMap();	
		map.put("Arm length", this.armLength);
		map.put("vPi", this.vPi);
		map.put("Duty cycle", this.dutyCycle);
		map.put("Modulator capacitance", this.capModulator);
		map.put("Pulse carver capacitance", this.capPulseCarver);
		map.put("Modulator bandwidth", this.bandwidthModulator);
		return map;
	}

}
