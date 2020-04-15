package Simulations.rings.allpass;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.CurvedWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class RingWgDesignSpaceGap implements Experiment {

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
	
	private double getRoundTripLoss(){
		return Cwg.getS21().absSquared() ;
	}
	
	private double getFSRnm(){
		double Rnm = radiusMicron * 1e3 ;
		double lambdaResNm = inputLambda.getWavelengthNm() ;
		return (lambdaResNm*lambdaResNm)/(2*Math.PI*Rnm * ng) ;
	}
	
	private double getKappa(){
		return ringWgCoupling.getS31().abs() ;
	}
	
	private double getT(){
		return ringWgCoupling.getS21().abs() ;
	}
	
	private double getTransmission(double deltaPhi){
		double t = getT() ;
		double L = getRoundTripLoss() ;
		double num = t*t + L - 2*t*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double denum = 1 + t*t*L - 2*t*Math.sqrt(L)*Math.cos(deltaPhi) ;
		double trans = num/denum ;
		return trans ;
	}
	
	private double getInsertionLossAtResonance(){
		double deltaPhi = 0 ;
		return getTransmission(deltaPhi) ;
	}
	
	private double getInsertionLossAtResonancedB(){
		double il = getInsertionLossAtResonance() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ; 
	}
	
	private double getInsertionLossAtHalfFSR(){
		double deltaPhi = Math.PI ;
		return getTransmission(deltaPhi) ;
	}
	
	private double getInsertionLossAtHalfFSRdB(){
		double il = getInsertionLossAtHalfFSR() ;
		double il_dB = 10*Math.log10(il) ;
		return il_dB ;
	}
	
	private double getBandwidthNm(){
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

	private double getBandwidthHz(){
		double DlambdaNm = getBandwidthNm() ;
		return inputLambda.getFreqSpacingHz(DlambdaNm) ;
	}
	
	
	private double getQ(){
		return inputLambda.getWavelengthNm()/getBandwidthNm() ;
	}
	// We can also calculate transmission at detuning
	private double getTransmissionAtDetuningDB(double dfreqGhz){
		double dfreqHz = dfreqGhz * 1e9 ;
		double dlambdaNm = inputLambda.getWavelengthSpacingNm(dfreqHz) ;
		double dPhi = -dlambdaNm/getFSRnm() * 2*Math.PI ;
		double trans = getTransmission(dPhi) ;
		return 10*Math.log10(trans) ;
	}
	
	// Now Calculating all the necessary contours
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
	
		dp.addProperty("radius (um)", radiusMicron) ;
		dp.addProperty("gap (nm)", gapNm);
		
		dp.addResultProperty("alpha (dB/cm)", Cwg.getWgPropLossdBperCm());
		dp.addResultProperty("Kappa", getKappa());
		dp.addResultProperty("T", getT());
		dp.addResultProperty("IL at resonance", getInsertionLossAtResonancedB());
		dp.addResultProperty("IL at FSR/2 resonance", getInsertionLossAtHalfFSRdB());
		dp.addResultProperty("Q Factor (x1000)", getQ()/1000);
		dp.addResultProperty("Bandwidth (GHz)", getBandwidthHz()/1e9);
		dp.addResultProperty("Bandwidth (nm)", getBandwidthNm());
		dp.addResultProperty("Group Index", ng);
		dp.addResultProperty("FSR (nm)", getFSRnm());
		
		dp.addResultProperty("Trans at 100GHz", getTransmissionAtDetuningDB(100));

		man.addDataPoint(dp);
	}

}
