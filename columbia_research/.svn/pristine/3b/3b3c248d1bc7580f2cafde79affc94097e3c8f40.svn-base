package edu.columbia.lrl.CrossLayer.physical_models.devices.switches;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;



public class MZISwitch1x2 {

	double propLossdBperCm;
	double lengthOfArmMicron ;
	double lengthOfArmCm ;
	double couplerLossdB ;
	double switchingTimeNs ;
	double confinementFactor = 0.8 ; // Confinement of optical mode in the silicon waveguide
	
	
	public MZISwitch1x2(
			@ParamName(name="Waveguide Propagation Loss (dB/cm)", default_="1") double propLossdBperCm,
			@ParamName(name="Length of MZI Arm (micron)", default_="300") double lengthOfArmMicron,
			@ParamName(name="50-50 Coupler Loss (dB)", default_ = "0.5") double couplerLossdB,
			@ParamName(name="Switching Time (ns)", default_ = "5") double switchingTimeNs
			) {
		this.propLossdBperCm = propLossdBperCm ;
		this.lengthOfArmMicron = lengthOfArmMicron ;
		this.switchingTimeNs = switchingTimeNs ; 
		this.couplerLossdB = couplerLossdB ;
		this.lengthOfArmCm = lengthOfArmMicron / 1e4 ;
	}
	

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Waveguide Propagation Loss (dB/cm)", propLossdBperCm + "");
		map.put("Length of MZI Arm (micron)", lengthOfArmMicron + "") ;
		map.put("50-50 Coupler Loss (dB)", couplerLossdB + "") ;
		return map;
	}

	public double getWgPropLossdB(){
		double lossdB = propLossdBperCm * (lengthOfArmMicron*1e-4) ;
		double loss = Math.pow(10, -lossdB/10) ;
		return loss ;
	}
	
	public double getWgPropLoss(){
		double lossdB = propLossdBperCm * (lengthOfArmMicron*1e-4) ;
		double loss = Math.pow(10, -lossdB/10) ;
		return loss ;
	}
	
	// Assuming a 50-50 coupler and independent of wavelength around 1550nm.
	public double getCouplerCoeff(){
		double cSquared = 0.5 * Math.pow(10, -couplerLossdB/10) ;
		double C = Math.sqrt(cSquared) ;
		return C ;
	}
	
	public double getThroughTransmission(Constants ct, double deltaAlphaPerCm){
		double L = getWgPropLoss() ;
		double C = getCouplerCoeff() ;
		double LDelta = Math.exp(-deltaAlphaPerCm * lengthOfArmCm) ;
		double lambda = ct.getCenterWavelength() ;
		double K = 2*Math.PI/lambda ;
		double Dn_Si = getDeltaNfromDeltaAlpha(deltaAlphaPerCm) ;
		double DeltaPhi = K * confinementFactor * (lengthOfArmCm*1e-2) * Dn_Si ; 
		double TransThru = L * Math.pow(C, 4) * (1 + LDelta - 2 * Math.sqrt(LDelta)*Math.cos(DeltaPhi)) ;
		return TransThru ;
	}
	
	public double getThroughTransmissionDB(Constants ct, double deltaAlphaPerCm){
		double trans = getThroughTransmission(ct, deltaAlphaPerCm) ;
		double transDB = 10*Math.log10(trans) ;
		return transDB ;
	}
	
	public double getCrossTransmission(Constants ct, double deltaAlphaPerCm){
		double L = getWgPropLoss() ;
		double C = getCouplerCoeff() ;
		double LDelta = Math.exp(-deltaAlphaPerCm * lengthOfArmCm) ;
		double lambda = ct.getCenterWavelength() ;
		double K = 2*Math.PI/lambda ;
		double Dn_Si = getDeltaNfromDeltaAlpha(deltaAlphaPerCm) ;
		double DeltaPhi = K * confinementFactor * (lengthOfArmCm*1e-2) * Dn_Si ; 
		double TransCross = L * Math.pow(C, 4) * (1 + LDelta + 2 * Math.sqrt(LDelta)*Math.cos(DeltaPhi)) ;
		return TransCross ;
	}
	
	public double getCrossTransmissionDB(Constants ct, double deltaAlphaPerCm){
		double trans = getCrossTransmission(ct, deltaAlphaPerCm) ;
		double transDB = 10*Math.log10(trans) ;
		return transDB ;
	}
	
	public double getOptimumDeltaAlpha(Constants ct){
		double DalphaPerCmStep = 0.01 ;
		double optDeltaAlphaPerCm = 0 ;
		double minCrossTransDB = getCrossTransmissionDB(ct, optDeltaAlphaPerCm) ;
		
		while(minCrossTransDB >= getCrossTransmissionDB(ct, optDeltaAlphaPerCm)){
			minCrossTransDB = getCrossTransmissionDB(ct, optDeltaAlphaPerCm) ;
			optDeltaAlphaPerCm += DalphaPerCmStep ;
		}
		
		return optDeltaAlphaPerCm ;
	}
	
	public double getDeltaNfromDeltaAlpha(double deltaAlphaPerCm){
		double Dn = -1e-4 * (0.607 * deltaAlphaPerCm + 2.5138 * Math.pow(deltaAlphaPerCm, 0.8)) ;
		return Dn ;
	}
	
	public double getBarStateILdB(Constants ct){
		double deltaAlphaOptimum = getOptimumDeltaAlpha(ct) ;
		return getThroughTransmissionDB(ct, deltaAlphaOptimum) ;
	}
	
	public double getBarStateCrosstalkLevel(Constants ct){
		double deltaAlphaOptimum = getOptimumDeltaAlpha(ct) ;
		return getCrossTransmission(ct, deltaAlphaOptimum) ;
	}
	
	public double getBarStateCrosstalkLeveldB(Constants ct){
		double deltaAlphaOptimum = getOptimumDeltaAlpha(ct) ;
		return getCrossTransmissionDB(ct, deltaAlphaOptimum) ;
	}
	
	public double getBarStateCrosstalkPP(Constants ct){
		int qBER = 7 ;
		double PP = -10*Math.log10(1 - getBarStateCrosstalkLevel(ct) * 1/2 * qBER) ; // Assuming infinite extinction ratio (not including the (1+r)/(1-r) factor)
		return PP ;
	}
	
	
	public double getCrossStateILdB(Constants ct){
		double deltaAlphaOptimum = 0 ;
		return getCrossTransmissionDB(ct, deltaAlphaOptimum) ;
	}
	
	public double getCrossStateCrosstalkLevel(Constants ct){
		double deltaAlphaOptimum = 0 ;
		return getThroughTransmission(ct, deltaAlphaOptimum) ;
	}
	
	public double getCrossStateCrosstalkLeveldB(Constants ct){
		double deltaAlphaOptimum = 0 ;
		return getThroughTransmissionDB(ct, deltaAlphaOptimum) ;
	}
	
	public double getCrossStateCrosstalkPP(Constants ct){
		int qBER = 7 ;
		double PP = -10*Math.log10(1 - getCrossStateCrosstalkLevel(ct) * 1/2 * qBER) ; // Assuming infinite extinction ratio (not including the (1+r)/(1-r) factor)
		return PP ;
	}
	

	public double getSwitchingTimeNs(){
		return switchingTimeNs ;
	}
	
		
}
	