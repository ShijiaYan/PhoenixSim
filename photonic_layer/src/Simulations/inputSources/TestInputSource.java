package Simulations.inputSources;

import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;


public class TestInputSource implements Experiment{

	AbstractInputSource inputSource ;
	Wavelength inputLambda ;

	
	public TestInputSource(
				@ParamName(name="Set Wavlength (nm)") Wavelength inputLambda,
				@ParamName(name="Choose Input Source") AbstractInputSource inputSource

			){
		this.inputSource = inputSource ;
		this.inputLambda = inputLambda ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		DataPoint dp = new DataPoint() ;
		
		dp.addProperty("Frequency (THz)", inputLambda.getFreqTHz());
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addResultProperty("Input Power Spectrum", inputSource.getPowerAtInputWavelengthMW(inputLambda));
		dp.addResultProperty("normalized OOK", inputSource.getPowerAtInputWavelengthdBm(inputLambda));
		dp.addProperty("State of the Source", inputSource.getInputSourceState());
		
		man.addDataPoint(dp);
	}
	
	public double checkNaN(double x){
		if (x<0){
			if(Double.isInfinite(x)||Double.isNaN(x)){
				return -300 ;
			}
			else{
				return x ;
			}
		}
		else if(x>0){
			if(Double.isInfinite(x)||Double.isNaN(x)){
				return 300 ;
			}
			else{
				return x ;
			}
		}
		else{
			return 0 ;
		}
	}

}
