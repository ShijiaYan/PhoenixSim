package PhotonicElements.Nonlinearity.CurvedWaveguide;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CurvedWgThermal {
	CurvedWgKerr wgKerr;
	CurvedWgTPA wgTPA;
	Wavelength inputLambda ;
	WgProperties wgProp ;
	double dndT, radiusMicron, angleDegree, lengthMicron, Pabs, lambdaNm, k0;
	double Ksi,pin_mW,Dnthermal;
	double radiusOfCurvatureMicron;
	
	// This class implements index change due to change in temperature
	public CurvedWgThermal(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties WgProp,
			@ParamName(name="dn/dT (1/K)",default_= "1.86e-4") double dndT,
			@ParamName(name="Thermal Resistivity (Kcm/W)",default_="0.76923077") double Ksi,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
	this.inputLambda = inputLambda;
	this.lambdaNm = inputLambda.getWavelengthNm() ;
	this.wgProp = WgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.dndT = dndT;
	k0 = inputLambda.getK0() ; 
	this.radiusOfCurvatureMicron = radiusOfCurvatureMicron;
	this.Ksi = Ksi ;
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	this.wgKerr = new CurvedWgKerr(inputLambda, WgProp, 3e-18, 5e-12,radiusOfCurvatureMicron, angleOfCurvatureDegree , pin_mW);
	this.wgTPA = new CurvedWgTPA(inputLambda, WgProp, 5e-12, radiusOfCurvatureMicron, angleOfCurvatureDegree,pin_mW, 10e-9); 
	this.pin_mW = pin_mW;
	this.Pabs = getPowerAbsW();
	this.Dnthermal = getDnThermal();
	}
	//************************************************************************************
	public CurvedWgThermal(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties WgProp,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
	this.inputLambda = inputLambda;
	this.lambdaNm = inputLambda.getWavelengthNm() ;
	this.wgProp = WgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.dndT = 1.86e-4;
	k0 = 2*Math.PI/(lambdaNm*1e-9) ;
	this.radiusOfCurvatureMicron = radiusOfCurvatureMicron;
	this.Ksi = 0.76923077 ;
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	this.wgKerr = new CurvedWgKerr(inputLambda, WgProp, 3e-18, 5e-12, radiusOfCurvatureMicron, angleOfCurvatureDegree , pin_mW);
	this.wgTPA = new CurvedWgTPA(inputLambda, WgProp, 5e-12, radiusOfCurvatureMicron, angleOfCurvatureDegree,pin_mW, 10e-9); 
	this.pin_mW = pin_mW;
	this.Pabs = getPowerAbsW();
	this.Dnthermal = getDnThermal();
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
	
	public double getDalphaThermal(){
		return 0 ;
	}
	
	public double getPowerAbsW(){
		return pin_mW * 1e-3 * (1 - wgKerr.getExcessLossKerr() * wgTPA.getExcessLossTPA() * getLossLinear());
	}
	
	public double getLossLinear(){
		return Math.exp(-wgProp.getBendLossModel().getAbsorptionLossPerMeter(radiusOfCurvatureMicron)*lengthMicron*1e-6);
	}
	
	private double getLengthCm(){
		return lengthMicron*1e-4;
	}
	
	public double getDeltaT(){
		return (Ksi*getLengthCm()/wgProp.getCrossSectionAreaCmSquare()) *Pabs ;
	}
	
	public double getDnThermal(){
		return dndT*getDeltaT();
	}
	
	public double getDnEffThermal(){
		return getDnThermal()*wgProp.getConfinementFactor() ;
	}
	
	public double getExcessPhaseThermal(){
		return getDnEffThermal()*k0*lengthMicron*1e-6;
	}
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		Complex minusJ = new Complex(0,-1) ;
		Complex phiPTAComplex = new Complex(getExcessPhaseThermal(), -getDalphaThermal()/2) ; // complex phase including loss
		return phiPTAComplex.times(minusJ).exp() ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
	
}
