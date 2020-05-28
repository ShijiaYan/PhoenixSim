package PhotonicElements.Nonlinearity.StraightWaveguide;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class StraightWgNL {
	
	// Step 1: Create a linear waveguide
	// Step 2: Add the nonlinear contribution from Kerr
	// Step 3: Add the nonlinear contribution from TPA
	// Step 4: Add the contribution from self-heating
	
	WgProperties wgProp;
	Wavelength inputLambda ;
	StraightWgKerr wgKerr;
	StraightWgThermal wgThermal;
	StraightWgTPA wgTPA;
	StraightWg wg ;
	double lengthMicron, pin_mW;
	
	public StraightWgNL(
			@ParamName(name="Input wavelength (micron)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Length of Waveguide (micron)") double lengthMicron,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
	this.wgProp = wgProp;
	this.inputLambda = inputLambda ;
	this.lengthMicron = lengthMicron;
	this.pin_mW = pin_mW;
	this.wgKerr = new StraightWgKerr(inputLambda, wgProp, lengthMicron, pin_mW);
	this.wgThermal = new StraightWgThermal(inputLambda, wgProp, lengthMicron, pin_mW);
	this.wgTPA = new StraightWgTPA(inputLambda, wgProp, lengthMicron, pin_mW);
	this.wg = new StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
	}
	
	public double getPinMw(){
		return pin_mW ;
	}
	
	public double getWgLengthMicron(){
		return lengthMicron ;
	}
	
	public double getExcessPhaseNL(){
		return wgKerr.getExcessPhaseKerr()+wgThermal.getExcessPhaseThermal()+ wgTPA.getExcessPhaseTPA();
	}
	
	public double getExcessLoss(){
		return wgKerr.getExcessLossKerr()*wgTPA.getExcessLossTPA();
	}
	
	public double getDalphaNL(){
		return wgKerr.getDalphaKerr() + wgTPA.getDalphaTPA() + wgThermal.getDalphaThermal();
	}
	
	// Now calculating the scattering parameters of the nonlinear waveguide
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		Complex minusJ = new Complex(0,-1) ;
		Complex phiComplex = new Complex(getExcessPhaseNL(), -getDalphaNL()/2*lengthMicron*1e-6) ; // complex phase including loss
		return phiComplex.times(minusJ).exp().times(wg.getS21()) ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
}
