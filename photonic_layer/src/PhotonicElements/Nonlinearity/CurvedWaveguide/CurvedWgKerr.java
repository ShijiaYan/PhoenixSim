package PhotonicElements.Nonlinearity.CurvedWaveguide;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;



public class CurvedWgKerr {

	// This class implements Kerr nonlinearity in Silicon waveguide
	
	// step 1: identify Kerr coefficients of Si (ignore SiO2 effect)
	// step 2: calculate the DnSi (change of index) and Dalpha (change of loss)
	
	Wavelength inputLambda ;
	WgProperties wgProp ;
	double n2, betaTPA, radiusMicron, angleDegree, lengthMicron, pin_mW, lambdaNm, k0, r ;
	double alphaCurve, alphaKerr, Iin ;
	CurvedWg CurvedWg;
	
	//************************************************************************************
	public CurvedWgKerr(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Kerr n2 coefficient (m^2/W)", default_="3e-18") double n2 ,
			@ParamName(name="Beta TPA coefficient (m/W)", default_="5e-12") double betaTPA, 
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.n2 = n2 ;
		this.betaTPA = betaTPA ;
		this.radiusMicron = radiusOfCurvatureMicron ;
		this.angleDegree = angleOfCurvatureDegree ;
		this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
		this.pin_mW = pin_mW ;
		// calculate other parameters
		lambdaNm = inputLambda.getWavelengthNm() ;
		k0 = 2*Math.PI/(lambdaNm*1e-9) ; 
		alphaCurve = wgProp.getBendLossModel().getLossPerMeter(radiusOfCurvatureMicron); // per meter
		Iin = (pin_mW*1e-3)/(wgProp.getCrossSectionAreaMeterSquare()) ; // input intensity of the optical mode
		alphaKerr = betaTPA * Iin ; // per meter
	}
	//************************************************************************************
	public CurvedWgKerr(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusOfCurvatureMicron ;
		this.angleDegree = angleOfCurvatureDegree ;
		this.n2 = 3e-18 ;
		this.betaTPA = 5e-12 ;
		this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ; ;
		this.pin_mW = pin_mW ;
		// calculate other parameters
		lambdaNm = inputLambda.getWavelengthNm() ;
		k0 = 2*Math.PI/(lambdaNm*1e-9) ; 
		alphaCurve = wgProp.getBendLossModel().getLossPerMeter(radiusOfCurvatureMicron); // per meter
		Iin = (pin_mW*1e-3)/(wgProp.getCrossSectionAreaMeterSquare()) ; // input intensity of the optical mode
		alphaKerr = betaTPA * Iin ; // per meter
		
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
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getDnSiKerr(){
		return (n2*Iin) ;
	}
	
	public double getDnEffKerr(){
		return getDnSiKerr()*wgProp.getConfinementFactor() ;
	}
	
	public double getDalphaKerr(){
		return alphaKerr*wgProp.getConfinementFactor() ;
	}
	
	private double getGammaKerr(){
		double gamma = k0*wgProp.getConfinementFactor()*n2/wgProp.getCrossSectionAreaMeterSquare() ;
		return gamma ;
	}

	public double getExcessLossKerr(){
		return Math.exp(-getDalphaKerr()*lengthMicron*1e-6) ;
	}
	
	
	
	public double getExcessPhaseKerr(){
		return getGammaKerr()*pin_mW*1e-3*lengthMicron*1e-6 ;
	}
	
	// Now calculate S-paramters for Kerr contribution
	
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		Complex minusJ = new Complex(0,-1) ;
		Complex phiKerrComplex = new Complex(getExcessPhaseKerr(), -getDalphaKerr()/2*lengthMicron*1e-6) ; // complex phase including loss
		return phiKerrComplex.times(minusJ).exp() ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
}
