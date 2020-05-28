package PhotonicElements.RingDesignSpace.AllPass;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RingWgDesignSpaceKappa {

	Wavelength inputLambda ;
	WgProperties wgProp;
	CompactCoupler inCoupling ;
	CurvedWg Cwg ;
	double radiusMicron, gapNm, ng ;
	double Kappa ;
	
	// this one for both input kappa and output kappa
	public RingWgDesignSpaceKappa(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Kappa") double Kappa
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
		this.ng = this.Cwg.getGroupIndex() ;
		this.Kappa = Kappa ;
		this.inCoupling = new CompactCoupler(Kappa) ;
	}
	
	
	// this one for Critical coupling and output kappa
	public RingWgDesignSpaceKappa(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Ring Radius (micron)") double radiusMicron
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
		this.ng = this.Cwg.getGroupIndex() ;
		double L = getRoundTripLoss() ;
		this.Kappa = Math.sqrt(1-L) ;
		this.inCoupling = new CompactCoupler(Kappa) ;
	}

	public double getRoundTripLoss(){
		return Cwg.getS21().absSquared() ;
	}
	
	public double getFSRnm(){
		double Rnm = radiusMicron * 1e3 ;
		double lambdaResNm = inputLambda.getWavelengthNm() ;
		return lambdaResNm*lambdaResNm /(2*Math.PI*Rnm * ng) ;
	}
	
	
	public double getInputT(){
		return inCoupling.getS21().abs() ;
	}
	
	public double getInputKappa(){
		return inCoupling.getS31().abs() ;
	}
	
	public double getDeltaPhi(Wavelength lambda){
		double resLambdaNm = inputLambda.getWavelengthNm() ;
		double deltaPhi = - (lambda.getWavelengthNm()-resLambdaNm)/getFSRnm() * 2*Math.PI ;
		return deltaPhi ;
	}
	
	public double getThruTransmission(double deltaPhi){
		double t_in = getInputT() ;
		double L = getRoundTripLoss() ;
		double num = t_in*t_in + L - 2*t_in*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double denum = 1 + t_in*t_in * L - 2*t_in* Math.sqrt(L)*Math.cos(deltaPhi) ;
		double trans = num/denum ;
		return trans ;
	}
	
	public double getThruTransmissiondB(double deltaPhi){
		double trans = getThruTransmission(deltaPhi) ;
		return 10*Math.log10(trans) ;
	}
	
	
	// Characterizing Thru Transmission
	public double getThruInsertionLossAtResonance(){
		double deltaPhi = 0 ;
		return getThruTransmission(deltaPhi) ;
	}
	
	public double getThruInsertionLossAtResonancedB(){
		double il = getThruInsertionLossAtResonance() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ; 
	}
	
	public double getThruInsertionLossAtHalfFSR(){
		double deltaPhi = Math.PI ;
		return getThruTransmission(deltaPhi) ;
	}
	
	public double getThruInsertionLossAtHalfFSRdB(){
		double il = getThruInsertionLossAtHalfFSR() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ;
	}
	
	
	// Characterizing the 3dB Bandwidth
	public double getBandwidthNm(){
		double t_in = getInputT() ;
		double L = getRoundTripLoss() ;
		double FSR = getFSRnm() ;
		double A = t_in*Math.sqrt(L);
		double phase = 1-(1-A)*(1-A)/(2*A) ;
		double DlambdaNm = FSR/Math.PI * Math.acos(phase) ;
		return DlambdaNm ;
	}


	public double getBandwidthHz(){
		double DlambdaNm = getBandwidthNm() ;
		return inputLambda.getFreqSpacingHz(DlambdaNm) ;
	}
	
	public double getQ(){
		return inputLambda.getWavelengthNm()/getBandwidthNm() ;
	}
	
	// We can also calculate transmission at detuning

	public double getThruTransmissionAtDetuningDB(double dfreqGhz){
		double dfreqHz = dfreqGhz * 1e9 ;
		double dlambdaNm = inputLambda.getWavelengthSpacingNm(dfreqHz) ;
		double dPhi = -dlambdaNm/getFSRnm() * 2*Math.PI ;
		double trans = getThruTransmission(dPhi) ;
		return 10*Math.log10(trans) ;
	}
	
	
	// now calculating the scattering parameters
	public Complex getS21(double deltaPhi){
		Complex t = new Complex(getInputT(), 0) ;
		Complex L_squared = new Complex(Math.sqrt(getRoundTripLoss()), 0) ;
		Complex E = new Complex(0, -deltaPhi).exp() ;
		Complex one = new Complex(1,0) ;
		Complex num = t.minus(L_squared.times(E)) ;
		Complex denom = one.minus(t.times(L_squared).times(E)) ;
		Complex S21 = num.divides(denom) ;
		return S21 ;
	}
	
	public Complex getS11(double deltaPhi){
		return new Complex(0,0) ;
	}
	
	public Complex getS22(double deltaPhi){
		return new Complex(0,0) ;
	}
	
	public Complex getS12(double deltaPhi){
		return getS21(deltaPhi) ;
	}
	

}
