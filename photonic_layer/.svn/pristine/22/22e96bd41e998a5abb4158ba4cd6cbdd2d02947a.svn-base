package Simulations.rings.ringSwitch;

import PhotonicElements.Switches.Switch2x2.Switch2x2Ring;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class Switch2x2RingEO implements Experiment {

	Switch2x2Ring ringSwitch ;

	public Switch2x2RingEO(
			Switch2x2Ring ringSwitch
			){
		this.ringSwitch = ringSwitch ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", ringSwitch.inputLambda.getWavelengthNm());
		dp.addProperty("radius (um)", ringSwitch.Cwg1.getRadiusOfCurvatureMicron());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S21.absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S31.absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S41.absSquared()));
		dp.addResultProperty("|S32|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S32.absSquared()));
		dp.addResultProperty("|S42|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S42.absSquared()));
		dp.addResultProperty("|S43|^2 (dB)", MoreMath.Conversions.todB(ringSwitch.S43.absSquared()));
		man.addDataPoint(dp);
	}

}
