package Simulations.rings.allpass;

import PhotonicElements.RingStructures.AllPass.RingWg;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class RingWgGeneralSim implements Experiment {

	RingWg ringWg ;

	public RingWgGeneralSim(
			@ParamName(name="Ring Wg Model") RingWg ringWg
			){
		this.ringWg = ringWg ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", ringWg.getWavelength().getWavelengthNm());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(ringWg.getS21().absSquared()));
		dp.addResultProperty("|S12|^2 (dB)", MoreMath.Conversions.todB(ringWg.getS12().absSquared()));
		dp.addResultProperty("Phase(S21)-rad", ringWg.getS21().phaseMinusPiToPi());
		dp.addResultProperty("Phase(S12)-rad", ringWg.getS12().phaseMinusPiToPi());
		man.addDataPoint(dp);
	}

}
