package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.WgWgCoupler;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class CopyOfTestDirectionalCoupler implements Experiment {

	WgWgCoupler coupler ;
	AbstractInputSource inputSource ;
	Wavelength inputLambda ;
	
	public CopyOfTestDirectionalCoupler(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Choose input source") AbstractInputSource inputSource,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Length of the coupling region (micron)") double lengthMicron ,
			@ParamName(name="Gap Size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.coupler = new WgWgCoupler(inputLambda, wgProp, lengthMicron, gapNm) ;
		this.inputSource = inputSource ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("Length of Coupling Region (micron)", coupler.getLengthMicron());
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(coupler.getS21().absSquared()));
		dp.addResultProperty("|S21|^2", coupler.getS21().absSquared());
		dp.addResultProperty("|S31|^2 (dB)", 10*Math.log10(coupler.getS31().absSquared()));
		dp.addResultProperty("|S31|^2", coupler.getS31().absSquared());
		
		man.addDataPoint(dp);
	}
	
	
	
}
