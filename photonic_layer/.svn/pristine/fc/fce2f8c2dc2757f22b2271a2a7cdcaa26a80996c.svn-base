package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestRingWgCoupler_Strip implements Experiment {

	RingWgCoupler coupler ;

	public TestRingWgCoupler_Strip(
			RingWgCoupler coupler
			) {
		this.coupler = coupler ;
	}

	public TestRingWgCoupler_Strip(
			int numIntervals,
			RingWgCoupler coupler
			) {
		this.coupler = coupler ;
		this.coupler.setNumIntervals(numIntervals);
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", coupler.getWavelengthNm());
		dp.addProperty("gap (nm)", coupler.getGapSizeNm());
		dp.addProperty("Radius (micron)", coupler.getRadiusMicron());
		dp.addProperty("number of intervals", coupler.getNumIntervals());
		dp.addResultProperty("t^2", coupler.getS21().absSquared());
		dp.addResultProperty("t", coupler.getS21().abs());
		dp.addResultProperty("kappa^2", coupler.getS31().absSquared());
		dp.addResultProperty("kappa", coupler.getS31().abs());
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.couplers.TestRingWgCoupler_Strip" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
