package People.Sebastien.LambdaRouter.Tests;

import People.Sebastien.LambdaRouter.Switch2x2RingPassive;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestSwitch2x2RingPassive implements Experiment {

	Switch2x2RingPassive sw ;
	
	public TestSwitch2x2RingPassive(
			Switch2x2RingPassive sw
			){
		this.sw = sw ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", sw.getInputLambda().getWavelengthNm());
		dp.addProperty("Radius (um)", sw.getRadiusMicron());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(sw.getS21().absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(sw.getS31().absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(sw.getS41().absSquared()));
		man.addDataPoint(dp);
	}

}
