package PhotonicElements.RingDesignSpace.AddDrop;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.LinearInterpolation;

public class AddDropDesignSpaceGap {

	public Wavelength inputLambda ;
	public WgProperties wgProp;
	public RingWgCoupler outCoupling, inCoupling ;
	public CurvedWg Cwg ;
	public double radiusMicron, inputGapNm, outputGapNm, ng , inputKappa, outputKappa ;
	
	
	// this one for both input and output gaps
	public AddDropDesignSpaceGap(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Input Gap Size (nm)") double inputGapNm,
			@ParamName(name="Output Gap Size (nm)") double outputGapNm
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.outputGapNm = outputGapNm ;
		this.outCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, outputGapNm) ;
		this.outputKappa = outCoupling.getS31().abs() ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
		this.ng = this.Cwg.getGroupIndex() ;
		this.inputGapNm = inputGapNm ;
		this.inCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, inputGapNm) ;
		this.inputKappa = inCoupling.getS31().abs() ;
	}
	
//	// this one is for critical coupling based on the input gap and loss of the ring (radius)
//	public AddDropDesignSpaceGap(
//			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
//			@ParamName(name="Waveguide Properties") WgProperties wgProp,
//			@ParamName(name="Ring Radius (micron)") double radiusMicron,
//			@ParamName(name="Input Gap Size (nm)") double inputGapNm
//			){
//		this.inputLambda = inputLambda ;
//		this.wgProp = wgProp ;
//		this.radiusMicron = radiusMicron ;
//		this.inputGapNm = inputGapNm ;
//		this.inCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, inputGapNm) ;
//		this.inputKappa = inCoupling.getS31().abs() ;
//		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
//		this.ng = this.Cwg.getGroupIndex() ;
//		double t_in = getInputT() ;
//		double L = getRoundTripLoss() ;
//		double t_out = t_in/Math.sqrt(L) ;
//		this.inputKappa = Math.sqrt(1-t_out*t_out) ;
//	}
	
	// this one is for critical coupling based on the output gap and loss of the ring (radius)
	public AddDropDesignSpaceGap(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Ring Radius (micron)") double radiusMicron,
			@ParamName(name="Output Gap Size (nm)") double outputGapNm
			){
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.radiusMicron = radiusMicron ;
		this.outputGapNm = outputGapNm ;
		this.outCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, outputGapNm) ;
		this.outputKappa = outCoupling.getS31().abs() ;
		this.Cwg = new CurvedWg(inputLambda, wgProp, radiusMicron, 360, false, null, false, null);
		this.ng = this.Cwg.getGroupIndex() ;
		double t_out = getOutputT() ;
		double L = getRoundTripLoss() ;
		this.inputKappa = Math.sqrt(1-t_out*t_out*L) ;
//		this.inputGapNm = findGapNm(k_in) ;
//		this.inCoupling = new RingWgCoupler(inputLambda, wgProp, radiusMicron, inputGapNm) ;
	}
	
	public double findGapNm(double kappa){
		int n = 500 ;
		double[] gapArrayNm = MoreMath.linspace(50, 500, n) ;
		double[] kappaArray = new double[n] ;
		for(int i=0; i<n; i++){
			RingWgCoupler coupler = new RingWgCoupler(inputLambda, wgProp, radiusMicron, gapArrayNm[i]) ;
			kappaArray[i] = coupler.getS31().abs() ;
		}
		LinearInterpolation linGap = new LinearInterpolation(kappaArray, gapArrayNm) ;
		double gapNm = linGap.interpolate(kappa) ;
		return gapNm ;
	}

	public double getRoundTripLoss(){
		return Cwg.getS21().absSquared() ;
	}
	
	public double getBendingLossdBperCm(){
		return wgProp.getBendLossModel().getLossdBperCm(radiusMicron) ;
	}
	
	public double getFSRnm(){
		double Rnm = radiusMicron * 1e3 ;
		double lambdaResNm = inputLambda.getWavelengthNm() ;
		return lambdaResNm*lambdaResNm /(2*Math.PI*Rnm * ng) ;
	}
	
	public double getOutputKappa(){
		return outputKappa;
	}
	
	public double getOutputT(){
		double outputT = Math.sqrt(1-outputKappa*outputKappa) ;
		return outputT ;
	}
	
	public double getInputKappa(){
		return inputKappa ;
	}
	
	public double getInputT(){
		double inputT = Math.sqrt(1-inputKappa*inputKappa) ;
		return inputT ;
	}
	
	public double getThruTransmission(double deltaPhi){
		double t_out = getOutputT() ;
		double t_in = getInputT() ;
		double L = getRoundTripLoss() ;
		double num = t_in*t_in + t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double trans = num/denum ;
		return trans ;
	}
	
	public double getThruTransmissiondB(double deltaPhi){
		double trans = getThruTransmission(deltaPhi) ;
		return 10*Math.log10(trans) ;
	}
	
	public double getDropTransmission(double deltaPhi){
		double t_out = getOutputT() ;
		double t_in = getInputT() ;
		double k_out = getOutputKappa() ;
		double k_in = getInputKappa() ;
		double L = getRoundTripLoss() ;
		double num = k_in * k_in * (k_out * k_out) * Math.sqrt(L);
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double trans = num/denum ;
		return trans ;
	}
	
	public double getDropTransmissiondB(double deltaPhi){
		double drop = getDropTransmission(deltaPhi) ;
		return 10*Math.log10(drop) ;
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
	
	// Characterizing Drop Transmission
	public double getDropInsertionLossAtResonance(){
		double deltaPhi = 0 ;
		return getDropTransmission(deltaPhi) ;
	}
	
	public double getDropInsertionLossAtResonancedB(){
		double il = getDropInsertionLossAtResonance() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ; 
	}
	
	public double getDropInsertionLossAtHalfFSR(){
		double deltaPhi = Math.PI ;
		return getDropTransmission(deltaPhi) ;
	}
	
	public double getDropInsertionLossAtHalfFSRdB(){
		double il = getDropInsertionLossAtHalfFSR() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ;
	}
	
	// Characterizing the 3dB Bandwidth
	public double getBandwidthNm(){
		double t_in = getInputT() ;
		double t_out = getOutputT() ;
		double L = getRoundTripLoss() ;
		double FSR = getFSRnm() ;
		double A = t_in*t_out*Math.sqrt(L);
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
	
	public double getDropTransmissionAtDetuningDB(double dfreqGhz){
		double dfreqHz = dfreqGhz * 1e9 ;
		double dlambdaNm = inputLambda.getWavelengthSpacingNm(dfreqHz) ;
		double dPhi = -dlambdaNm/getFSRnm() * 2*Math.PI ;
		double trans = getDropTransmission(dPhi) ;
		return 10*Math.log10(trans) ;
	}
	

}
