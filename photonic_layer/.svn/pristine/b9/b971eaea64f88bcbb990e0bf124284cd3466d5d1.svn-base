package Simulations.rings.backscattering;

import PhotonicElements.RingStructures.BackScattering.AddDropBackScatteringAsymmetricAbstract;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class AddDropBackScatteringAsymmetricSim implements Experiment {

	AddDropBackScatteringAsymmetricAbstract adr ;

	public AddDropBackScatteringAsymmetricSim(
			AddDropBackScatteringAsymmetricAbstract adr
			) {
		this.adr = adr ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("LR1 strength", Math.pow(adr.LR1.refAmp, 2));
		dp.addProperty("LR1 phase (rad)", adr.LR1.refPhaseRad);
		dp.addProperty("LR1 phase (degree)", adr.LR1.S11.phaseDegree());
		dp.addProperty("LR2 strength", Math.pow(adr.LR2.refAmp, 2));
		dp.addProperty("LR2 phase (rad)", adr.LR2.refPhaseRad);
		dp.addProperty("LR2 phase (degree)", adr.LR2.S11.phaseDegree());
		dp.addProperty("wavelength (nm)", adr.inputLambda.getWavelengthNm());
		dp.addProperty("FSR (nm)", adr.FSR_nm);
		dp.addResultProperty("round trip phase (rad)", adr.deltaPhiRad);
		dp.addResultProperty("Cwg1 phase (rad)", adr.Cwg1.S21.phase());
		dp.addResultProperty("Cwg2 phase (rad)", adr.Cwg2.S21.phase());
		dp.addResultProperty("Cwg1+Cwg2 phase (rad)", adr.Cwg1.S21.times(adr.Cwg2.S21).phase());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(adr.S21.absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(adr.S31.absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(adr.S41.absSquared()));
		dp.addResultProperty("|S21|^2", (adr.S21.absSquared()));
		dp.addResultProperty("|S31|^2", (adr.S31.absSquared()));
		dp.addResultProperty("|S41|^2 ", (adr.S41.absSquared()));
		man.addDataPoint(dp);
	}

}
