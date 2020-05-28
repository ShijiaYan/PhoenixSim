package NASA.Link;

import NASA.Modulator.GrapheneRingModulator;
import PhotonicElements.GratingCouplers.GratingCoupler;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Transmitter {

	/**
	 * Elements: SERializer, Driver, Ring Modulator, Waveguide, input and output grating couplers
	 */
	
	Wavelength inputLambda ;
	WgProperties wgProp ;
	StraightWg wg ;
	double numChannels ;
	GrapheneRingModulator modON, modOFF ;
	GratingCoupler grating ;
	double lambdaNm, channelSpacingNm ;
	
	public Transmitter(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Number of Channels") double numChannels,
			@ParamName(name="Waveguide Properties") WgProperties wgProp
			// need to add driver and serializer to it...
			){
		this.inputLambda = inputLambda ;
		lambdaNm = inputLambda.getWavelengthNm() ;
		this.numChannels = numChannels ;
		channelSpacingNm = 40/numChannels ;
		this.wgProp = wgProp ;
		this.wg = new StraightWg(inputLambda, wgProp, 100*numChannels, false, null, false, null) ;
		this.grating = new GratingCoupler(inputLambda) ;
		modON = new GrapheneRingModulator(inputLambda, 2.5, lambdaNm, 0.3, 6) ;
		modOFF = new GrapheneRingModulator(inputLambda, 2.5, lambdaNm, 0.3, 1) ;
		
	}
	
	public double getChannelSpacingNm(){
		return channelSpacingNm ;
	}
	
	public double getNumberOfChannels(){
		return numChannels ;
	}
	
	public double getWgLossdB(){
		double lossdB = -MoreMath.Conversions.todB(wg.getS21().absSquared()) ;
		return lossdB ;
	}
	
	public double getModulatorILdB(){
		double ILdB = -MoreMath.Conversions.todB(modON.getModulator().getS21(0).absSquared()) ;
		return ILdB ;
	}
	
	public double getModulatorER(){
		double powerON = modON.getModulator().getS21(0).absSquared() ; // at resonance
		double powerOFF = modOFF.getModulator().getS21(0).absSquared() ; // at resonance
		double er = powerON/powerOFF ;
		double erPenalty = -MoreMath.Conversions.todB((er-1)/(er+1)) ;
		return erPenalty ;
	}
	
	public double getModulatorOOK(){
		double powerON = modON.getModulator().getS21(0).absSquared() ; // at resonance
		double powerOFF = modOFF.getModulator().getS21(0).absSquared() ; // at resonance
		double er = powerON/powerOFF ;
		double ookPenalty = -MoreMath.Conversions.todB((er+1)/(2*er)) ;
		return ookPenalty ;
	}
	
	public double getModulatorPenaltydB(){
		double ILdB = -MoreMath.Conversions.todB(modON.getModulator().getS21(0).absSquared()) ;
		double powerON = modON.getModulator().getS21(0).absSquared() ; // at resonance
		double powerOFF = modOFF.getModulator().getS21(0).absSquared() ; // at resonance
		double er = powerON/powerOFF ;
		double erPenalty = -MoreMath.Conversions.todB((er-1)/(er+1)) ;
		double ookPenalty = -MoreMath.Conversions.todB((er+1)/(2*er)) ;
		// additional insertion loss from neighboring channels
		double dphi = channelSpacingNm/40 * 2*Math.PI ;
		double ILdB_from_neighbors = -MoreMath.Conversions.todB(modOFF.getModulator().getS21(dphi).absSquared()) ;
		double totPenalty = 0 ;
		if(numChannels == 1){
			totPenalty = ILdB + erPenalty + ookPenalty ;
		}
		else if(numChannels == 2){
			totPenalty = ILdB + erPenalty+ ookPenalty+ILdB_from_neighbors ;
		}
		else{
			totPenalty = ILdB + erPenalty+ ookPenalty+2*ILdB_from_neighbors ;
		}
		return totPenalty;
	}
	
	public double getGratingLossdB(){
		double lossdB = 2*grating.getLossdB() ;
		return lossdB ;
	}
	
	
	public double getTotalPenalty(){
		double totPenaltydB = getWgLossdB() + getModulatorPenaltydB() + getGratingLossdB() ;
		return totPenaltydB ;
	}
	
	
	
}
