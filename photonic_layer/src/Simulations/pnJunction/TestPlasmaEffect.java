package Simulations.pnJunction;

import PhotonicElements.PNJunction.PlasmaDispersionModel;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestPlasmaEffect implements Experiment {

	PlasmaDispersionModel plasma ;
	
	public TestPlasmaEffect(
			@ParamName(name="Plasma Dispersion Model") PlasmaDispersionModel plasma
			) {
		this.plasma = plasma ;
	}
	
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("DN", plasma.getDN());
		dp.addProperty("Dalpha (1/cm)", plasma.getDalphaPerCm());
		dp.addProperty("DnSi", plasma.getDnSi());
		dp.addResultProperty("DN", plasma.getDN());
		dp.addResultProperty("Dalpha (1/cm)", plasma.getDalphaPerCm());
		dp.addResultProperty("DnSi", plasma.getDnSi());
		man.addDataPoint(dp);
	}

}
