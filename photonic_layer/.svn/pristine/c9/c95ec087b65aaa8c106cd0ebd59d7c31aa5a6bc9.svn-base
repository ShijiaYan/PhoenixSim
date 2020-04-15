package Simulations.rings.backscattering;

import PhotonicElements.RingStructures.BackScattering.AddDropBackScatteringAbstract;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class AddDropBackScatteringSim implements Experiment {

	AddDropBackScatteringAbstract adr ;

	public AddDropBackScatteringSim(
			AddDropBackScatteringAbstract adr
			) {
		this.adr = adr ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Back scattering strength", Math.pow(adr.LR.refAmp, 2));
		dp.addProperty("Back scattering phase (rad)", adr.LR.refPhaseRad);
		dp.addProperty("wavelength (nm)", adr.inputLambda.getWavelengthNm());
		dp.addProperty("FSR (nm)", adr.FSR_nm);
		dp.addResultProperty("round trip phase (rad)", adr.deltaPhiRad);
		dp.addResultProperty("Cwg1 phase (rad)", adr.Cwg1.S21.phase());
		dp.addResultProperty("Cwg2 phase (rad)", adr.Cwg2.S21.phase());
		dp.addResultProperty("Cwg1+Cwg2 phase (rad)", adr.Cwg1.S21.times(adr.Cwg2.S21).phase());
		dp.addResultProperty("|S11|^2 (dB)", MoreMath.Conversions.todB(adr.S11.absSquared()));
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(adr.S21.absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(adr.S31.absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(adr.S41.absSquared()));
		man.addDataPoint(dp);
	}

}
