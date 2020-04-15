package Simulations.junctions.yJunction;

import PhotonicElements.Junctions.Yjunctions.Yjunction;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestYjunction implements Experiment {

	Yjunction Y ;
	
	public TestYjunction(
			@ParamName(name="Yjunction Model") Yjunction Y
			) {
		this.Y = Y ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Y junction loss (dB)", Y.junctionLossdB);
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(Y.getS21().absSquared()));
		man.addDataPoint(dp);
	}

}
