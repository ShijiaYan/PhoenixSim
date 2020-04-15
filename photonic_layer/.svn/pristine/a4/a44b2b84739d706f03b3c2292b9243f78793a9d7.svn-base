package Simulations.materials;

import PhotonicElements.Materials.LorentzModel.EpsilonLorentz;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestEpsilonLorentz implements Experiment {

	EpsilonLorentz eps ;
	
	public TestEpsilonLorentz(
			EpsilonLorentz eps
			) {
		this.eps = eps ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperties(eps.getAllParameters());
		dp.addResultProperty("Real(chi)", eps.getRealChi());
		dp.addResultProperty("Imag(chi)", eps.getImagChi());
		dp.addResultProperty("n(w)", eps.getRealIndex());
		dp.addResultProperty("k(w)", eps.getImagIndex());
		man.addDataPoint(dp);
	}

}
