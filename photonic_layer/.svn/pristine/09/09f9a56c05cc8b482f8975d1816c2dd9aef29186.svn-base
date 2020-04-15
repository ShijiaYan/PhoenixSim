package Simulations.mzi;

import PhotonicElements.MachZehnder.MZI;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestMZI implements Experiment {

	MZI mzi ;
	
	public TestMZI(
			MZI mzi
			){
		this.mzi = mzi ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Delta alpha (1/cm) [Plasma]", mzi.plasmaEffect.getDalphaPerCm());
		dp.addProperty("Wavelength (nm)", mzi.inputLambda.getWavelengthNm());
		dp.addResultProperty("|S11|^2 (dB)", MoreMath.Conversions.todB(mzi.S11.absSquared()));
		dp.addResultProperty("|S12|^2 (dB)", MoreMath.Conversions.todB(mzi.S12.absSquared()));
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(mzi.S21.absSquared()));
		dp.addResultProperty("|S22|^2 (dB)", MoreMath.Conversions.todB(mzi.S22.absSquared()));
		man.addDataPoint(dp);
	}

}
