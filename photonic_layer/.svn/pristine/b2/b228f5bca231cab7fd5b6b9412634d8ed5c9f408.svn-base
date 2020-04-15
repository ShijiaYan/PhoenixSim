package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerRibWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestDistributedCouplerRibWg implements Experiment {

	DistributedCouplerRibWg couplerRib ;
	
	public TestDistributedCouplerRibWg(
			DistributedCouplerRibWg couplerRib
			) {
		this.couplerRib = couplerRib ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", couplerRib.getWavelengthNm());
		dp.addProperty("Length (micron)", couplerRib.getLengthMicron());
		dp.addProperty("gap (nm)", couplerRib.getGapSizeNm());
		dp.addResultProperty("t^2", couplerRib.getS21().absSquared());
		dp.addResultProperty("kappa^2", couplerRib.getS31().absSquared());
		dp.addResultProperty("Neff (Even)", couplerRib.getEffectiveIndexEven());
		dp.addResultProperty("Neff (odd)", couplerRib.getEffectiveIndexOdd());
		man.addDataPoint(dp);
	}

}
