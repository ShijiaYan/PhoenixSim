package PhotonicElements.Nonlinearity.CurvedWaveguide;

import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.PhysicalConstants;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CurvedWgTPA {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	double  betaTPA, radiusMicron, angleDegree, lengthMicron, pin_mW, lambdaNm, k0, Nc,v0,nTPA;
	double alphaLinear, alphaTPA, Iin,tau ;
	PlasmaDispersionModel Plasma; 
	
	// This class implements Free-carrier Absorption (FCA) and Free-carrier Dispersion (FCD)
	//step1 calculate the change of neff and the change of alpha caused by TPA
	
	public CurvedWgTPA(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Beta TPA coefficient (m/W)", default_="5e-12") double betaTPA, 
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW,
			@ParamName(name="Carrier lifetime (s)") double tau
			){
	this.tau = tau;
	this.inputLambda = inputLambda;
	this.wgProp = wgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.betaTPA = betaTPA;
	this.pin_mW = pin_mW ;
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	lambdaNm = inputLambda.getWavelengthNm();
	v0 = inputLambda.getFreqHz() ; // frequency in Hz (it's better to use Wavelength class to do all the frequency conversions)
	k0 = inputLambda.getK0() ; // free-space wave number
	alphaLinear = wgProp.getBendLossModel().getLossPerMeter(radiusOfCurvatureMicron);
	Iin = (pin_mW*1e-3)/(wgProp.getCrossSectionAreaMeterSquare()) ; // input intensity (W/m^2)
	double Nc_perMeterCubed = tau*betaTPA*Math.pow(Iin, 2)/(2*PhysicalConstants.getPlanckConstant()*v0); // this is in 1/m^3, we should convert it in 1/(cm)^3 !!
	Nc = Nc_perMeterCubed * 1e-6 ; //  now this is in units of 1/(cm^3) 
	this.Plasma = new PlasmaDispersionModel(0,false,0,false,Nc,true);
	this.alphaTPA = getAlphaTPA(); // this is per meter
	this.nTPA = Plasma.getDnSi(); // this is unit less
	}
	//************************************************************************************
	public CurvedWgTPA(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius Of Curvature (micron)") double radiusOfCurvatureMicron,
			@ParamName(name="Angle of Curvature (degree)") double angleOfCurvatureDegree,
			@ParamName(name="Input Optical Power (mW)") double pin_mW
			){
	this.tau = 10e-9;
	this.inputLambda = inputLambda;
	this.wgProp = wgProp;
	this.radiusMicron = radiusOfCurvatureMicron ;
	this.angleDegree = angleOfCurvatureDegree ;
	this.betaTPA = 5e-12;
	this.pin_mW = pin_mW ;
	this.lengthMicron = radiusOfCurvatureMicron * angleOfCurvatureDegree * Math.PI/180 ;
	lambdaNm = inputLambda.getWavelengthNm();
	v0 = 3e8/(lambdaNm*1e-9) ;
	k0 = 2*Math.PI/(lambdaNm*1e-9);
	alphaLinear = wgProp.getBendLossModel().getLossPerMeter(radiusOfCurvatureMicron);
	Iin = (pin_mW*1e-3)/(wgProp.getCrossSectionAreaMeterSquare()) ;
	double Nc_perMeterCubed = tau*betaTPA*Math.pow(Iin, 2)/(2*PhysicalConstants.getPlanckConstant()*v0); // this is in 1/m^3, we should convert it in 1/(cm)^3 !!
	Nc = Nc_perMeterCubed * 1e-6 ; //  now this is in units of 1/(cm^3) 
	this.Plasma = new PlasmaDispersionModel(0,false,0,false,Nc,true);
	this.alphaTPA = getAlphaTPA();
	this.nTPA = Plasma.getDnSi();
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
	
	public double getAlphaTPA(){
		return Plasma.getDalphaPerMeter();
	}
	
	public double getDalphaTPA(){
		return alphaTPA*wgProp.getConfinementFactor();
	}
	
	public double getDnTPA(){
		return nTPA ;
	}
	public double gettau(){
		return this.tau;
	}
	
	public double getDnEffTPA(){
		return nTPA*wgProp.getConfinementFactor();
	}
	
	public double getExcessLossTPA(){
		return Math.exp(-getDalphaTPA()*lengthMicron*1e-6) ;
	}
	
	public double getExcessPhaseTPA(){
		return getDnEffTPA()*k0*lengthMicron*1e-6 ; // for phase we should use nEff not nSi
	}
	
	public Complex getS11(){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(){
		return new Complex(0,0) ;
	}
	
	public Complex getS21(){
		Complex minusJ = new Complex(0,-1) ;
		Complex phiPTAComplex = new Complex(getExcessPhaseTPA(), -getDalphaTPA()/2*lengthMicron*1e-6) ; // complex phase including loss
		return phiPTAComplex.times(minusJ).exp() ;
	}
	
	public Complex getS12(){
		return getS21() ;
	}
	
	
}
