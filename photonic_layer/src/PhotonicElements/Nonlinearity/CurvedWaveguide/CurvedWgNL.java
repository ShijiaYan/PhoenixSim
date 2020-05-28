package PhotonicElements.Nonlinearity.CurvedWaveguide;

import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CurvedWgNL {
	
	CurvedWgKerr CWgKerr;
	CurvedWgTPA CWgTPA;
	CurvedWgThermal CWgThermal;
	WgProperties wgProp ;
	Wavelength inputLambda ;
	double radiusMicron, angleDegree, lengthMicron,lambdaNm,pin_mW;
	CurvedWg CWg;
	
	//************************************************************************************
	public CurvedWgNL(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties for Linear") WgProperties wgProp,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Includes Heater?") boolean includeHeater,
			@ParamName(name="Simple Heater") SimpleHeater H,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
	this.wgProp = wgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.inputLambda = inputLambda;
	this.pin_mW = pin_mW;
	lambdaNm = inputLambda.getWavelengthNm();
	this.CWg = new CurvedWg(inputLambda, wgProp, radiusOfCurvatureMicron ,angleOfCurvatureDegree, false, null, includeHeater, H);
	this.CWgKerr = new CurvedWgKerr(inputLambda,wgProp,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW);
	this.CWgTPA = new CurvedWgTPA(inputLambda,wgProp,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW);
	this.CWgThermal = new CurvedWgThermal(inputLambda,wgProp,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW);
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	}
	//*************************************************************************************
	public CurvedWgNL(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties for Linear") WgProperties wgProp,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Includes Heater?") boolean includeHeater,
			@ParamName(name="Simple Heater") SimpleHeater H,
			@ParamName(name="Input Optical Power (mW)") double pin_mW,
			@ParamName(name="Carrier life time for TPA") double tau
			
			){
	this.wgProp = wgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.inputLambda = inputLambda;
	this.pin_mW = pin_mW;
	lambdaNm = inputLambda.getWavelengthNm();
	this.CWg = new CurvedWg(inputLambda, wgProp, radiusOfCurvatureMicron ,angleOfCurvatureDegree, false, null, includeHeater, H);
	this.CWgKerr = new CurvedWgKerr(inputLambda,wgProp,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW);
	this.CWgTPA = new CurvedWgTPA(inputLambda,wgProp,5e-12,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW,tau);
	this.CWgThermal = new CurvedWgThermal(inputLambda,wgProp,radiusOfCurvatureMicron,angleOfCurvatureDegree,pin_mW);
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	}
	//************************************************************************************
	public double getPinMw(){
		return pin_mW ;
	}
	
	public double getRadiusMicron(){
		return radiusMicron ;
	}
	
	public double getAngleDegree(){
		return angleDegree ;
	}
	
	public double getWgLengthMicron(){
		return lengthMicron ;
	}
	
	public double getExcessPhaseNL(){
		return CWgKerr.getExcessPhaseKerr()+ CWgThermal.getExcessPhaseThermal()+ CWgTPA.getExcessPhaseTPA();
	}
	
	public double getExcessLoss(){
		return CWgKerr.getExcessLossKerr()*CWgTPA.getExcessLossTPA();
	}
	public double getDnEff(){
		return CWgKerr.getDnEffKerr() + CWgTPA.getDnEffTPA() + CWgThermal.getDnEffThermal();
	}
	
	public double getDalphaNL(){
		return CWgKerr.getDalphaKerr() + CWgTPA.getDalphaTPA() + CWgThermal.getDalphaThermal();
	}
	
	// Now calculating the scattering parameters
	
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		Complex minusJ = new Complex(0,-1) ;
		Complex phiComplex = new Complex(getExcessPhaseNL(), -getDalphaNL()/2*lengthMicron*1e-6) ; // complex phase including loss
		return phiComplex.times(minusJ).exp().times(CWg.getS21()) ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
	// Calculate the output field at each port
	
	public Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(getS11()) ;
		Complex T2 = port2In.times(getS12()) ;
		return T1.plus(T2) ;
	}
	
	public Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(getS21()) ;
		Complex T2 = port2In.times(getS22()) ;
		return T1.plus(T2) ;
	}
	
	
}
