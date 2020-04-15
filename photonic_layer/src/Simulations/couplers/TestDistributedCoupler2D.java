package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCoupler2D;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestDistributedCoupler2D implements Experiment {

	DistributedCoupler2D coupler2D ;
	
	public TestDistributedCoupler2D(
			DistributedCoupler2D coupler2D
			) {
		this.coupler2D = coupler2D ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", coupler2D.getWavelengthNm());
		dp.addProperty("Length (micron)", coupler2D.getLengthMicron());
		dp.addProperty("gap (nm)", coupler2D.getGapSizeNm());
		dp.addResultProperty("t^2", coupler2D.getS21().absSquared());
		dp.addResultProperty("kappa^2", coupler2D.getS31().absSquared());
		dp.addResultProperty("Neff (Even)", coupler2D.getEffectiveIndexEven());
		dp.addResultProperty("Neff (odd)", coupler2D.getEffectiveIndexOdd());
		man.addDataPoint(dp);
	}

}
