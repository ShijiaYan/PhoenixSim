package PhotonicElements.RingDesignSpace.AllPass;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RingWgDesignSpaceGap {

	// Need to use distributed nonuniform coupling model for the ring-wg coefficients
	
	Wavelength inputLambda ;
	WgProperties wgProp;
	RingWgCoupler ringWgCoupling ;
//	RingWgCoupler_Rib ringWgCoupling ;
	CurvedWg Cwg ;
	double radiusMicron, gapNm, ng ;

	public RingWgDesignSpaceGap(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Input Gap Size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.gapNm = gapNm ;
		this.ringWgCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, gapNm) ;
//		this.ringWgCoupling = new RingWgCoupler_Rib(inputLambda, wgProp, radiusMicron, gapNm) ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
		this.ng = this.Cwg.getGroupIndex() ;
	}
	
	public double getRoundTripLoss(){
		return Cwg.getS21().absSquared() ;
	}
	
	public double getFSRnm(){
		double Rnm = radiusMicron * 1e3 ;
		double lambdaResNm = inputLambda.getWavelengthNm() ;
		return lambdaResNm*lambdaResNm /(2*Math.PI*Rnm * ng) ;
	}
	
	public double getKappa(){
		return ringWgCoupling.getS31().abs() ;
	}
	
	public double getT(){
		return ringWgCoupling.getS21().abs() ;
	}
	
	public double getTransmission(double deltaPhi){
		double t = getT() ;
		double L = getRoundTripLoss() ;
		double num = t*t + L - 2*t*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double denum = 1 + t*t*L - 2*t*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double trans = num/denum ;
		return trans ;
	}
	
	public double getInsertionLossAtResonance(){
		double deltaPhi = 0 ;
		return getTransmission(deltaPhi) ;
	}
	
	public double getInsertionLossAtResonancedB(){
		double il = getInsertionLossAtResonance() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ; 
	}
	
	public double getInsertionLossAtHalfFSR(){
		double deltaPhi = Math.PI ;
		return getTransmission(deltaPhi) ;
	}
	
	public double getInsertionLossAtHalfFSRdB(){
		double il = getInsertionLossAtHalfFSR() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ;
	}
	
	public double getBandwidthNm(){
		double t = getT() ;
		double L = getRoundTripLoss() ;
		double FSR = getFSRnm() ;
		double A = 1/(2*t*Math.sqrt(L)) ;
		double xi = 1 + getInsertionLossAtResonance() ;
		double B = t*t + L - (xi/(2-xi)) * (1-t*t)*(1-L) ;
		double phase = A*B ;
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
	public  double getTransmissionAtDetuningDB(double dfreqGhz){
		double dfreqHz = dfreqGhz * 1e9 ;
		double dlambdaNm = inputLambda.getWavelengthSpacingNm(dfreqHz) ;
		double dPhi = -dlambdaNm/getFSRnm() * 2*Math.PI ;
		double trans = getTransmission(dPhi) ;
		return 10*Math.log10(trans) ;
	}

}
