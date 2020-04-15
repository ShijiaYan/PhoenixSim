package PhotonicElements.PhaseShifters;

import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class PhaseShifterPNJunction {

	double phaseShiftDegree ;
	double phaseShiftRadian ;
	double DalphaPerCm ;
	double lengthMicron ;
	PlasmaDispersionModel plasmaEffect ;
	StraightWg wg ;
	
	public PhaseShifterPNJunction(
			Wavelength inputLambda,
			WgProperties wgProp,
			double lengthMicron,
			double phaseShiftDegree
			){
		this.phaseShiftDegree = phaseShiftDegree ;
		this.phaseShiftRadian = phaseShiftDegree * Math.PI/180 ;
		double DnSi = this.phaseShiftRadian/(2*Math.PI/inputLambda.getWavelengthMeter() * wgProp.getConfinementFactor() * lengthMicron*1e-6) ;
		this.plasmaEffect = new PlasmaDispersionModel(DnSi, true, 0, false, 0, false) ;
		this.wg = new StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ; // PN phase shifter, no thermal phase shift
	}
	
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		return wg.getS21() ;
	}
	
	public Complex getS12(){
		return wg.getS12() ;
	}
	
	
}
