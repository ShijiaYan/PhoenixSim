package Simulations.couplers;


import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestRingWgCoupler implements Experiment {

	RingWgCoupler coupler ;
	AbstractInputSource inputSource ;
	Wavelength inputLambda ;
	double gapNm ;
	
	public TestRingWgCoupler(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Choose input source") AbstractInputSource inputSource,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius of the Ring (micron)") double lengthMicron ,
			@ParamName(name="Gap Size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.coupler = new RingWgCoupler(inputLambda, wgProp, lengthMicron, gapNm) ;
		this.inputSource = inputSource ;
		this.gapNm = gapNm ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("Radius (micron)", coupler.getRadiusMicron());
		dp.addProperty("Gap size (nm)", gapNm);
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(coupler.getS21().absSquared()));
		dp.addResultProperty("|S21|^2", coupler.getS21().absSquared());
		dp.addResultProperty("|S21|", coupler.getS21().abs());
		dp.addResultProperty("|S31|^2 (dB)", 10*Math.log10(coupler.getS31().absSquared()));
		dp.addResultProperty("|S31|^2", coupler.getS31().absSquared());
		dp.addResultProperty("|S31|", coupler.getS31().abs());
		
		man.addDataPoint(dp);
	}
	
	
	
}
