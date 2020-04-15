package Simulations.rings.ringSwitch;

import PhotonicElements.Switches.Switch1x2.Switch1x2Ring;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class Switch1x2RingEO implements Experiment {

	Switch1x2Ring sw ;

	public Switch1x2RingEO(
			Switch1x2Ring sw
			){
		this.sw = sw ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", sw.inputLambda.getWavelengthNm());
		dp.addProperty("Delta Alpha (1/cm)", sw.plasmaEffect.getDalphaPerCm());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(sw.S21.absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(sw.S31.absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(sw.S41.absSquared()));
		dp.addResultProperty("|S32|^2 (dB)", MoreMath.Conversions.todB(sw.S32.absSquared()));
		dp.addResultProperty("|S42|^2 (dB)", MoreMath.Conversions.todB(sw.S42.absSquared()));
		dp.addResultProperty("|S43|^2 (dB)", MoreMath.Conversions.todB(sw.S43.absSquared()));
		man.addDataPoint(dp);
	}

}
