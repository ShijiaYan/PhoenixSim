package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCompactCoupler implements Experiment{

	CompactCoupler coupler ;
	
	public TestCompactCoupler(
				@ParamName(name="Choose coupler model") CompactCoupler coupler 
			){
		this.coupler = coupler ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("t coeff", coupler.getT());
		dp.addProperty("Coupler Loss (dB)", coupler.getLossdB());
		dp.addProperty("Kappa", coupler.getKappa());
		dp.addResultProperty("t coeff", coupler.getT());
		dp.addResultProperty("Kappa", coupler.getKappa());
		dp.addResultProperty("Coupler Loss", coupler.getLossdB());	
		man.addDataPoint(dp);
	}

}
