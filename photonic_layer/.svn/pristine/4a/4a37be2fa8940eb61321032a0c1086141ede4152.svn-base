package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RaceTrackCoupler;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestRacetrackCoupler_Strip implements Experiment {

	RaceTrackCoupler coupler ;
	double z ;

	public TestRacetrackCoupler_Strip(
			RaceTrackCoupler coupler
			) {
		this.coupler = coupler ;
	}

	public TestRacetrackCoupler_Strip(
			int numIntervals,
			RaceTrackCoupler coupler
			) {
		this.coupler = coupler ;
		this.coupler.setNumIntervals(numIntervals);
	}
	
	public TestRacetrackCoupler_Strip(
			double z,
			RaceTrackCoupler coupler
			) {
		this.coupler = coupler ;
		this.z = z ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", coupler.getWavelengthNm());
		dp.addProperty("gap (nm)", coupler.getGapSizeNm());
		dp.addProperty("z", z);
		dp.addProperty("length (um)", coupler.getLengthMicron());
		dp.addProperty("Radius (micron)", coupler.getRadiusMicron());
		dp.addProperty("number of intervals", coupler.getNumIntervals());
		dp.addResultProperty("t^2", coupler.getS21().absSquared());
		dp.addResultProperty("t", coupler.getS21().abs());
		dp.addResultProperty("kappa^2", coupler.getS31().absSquared());
		dp.addResultProperty("kappa", coupler.getS31().abs());
		dp.addResultProperty("y (nm)", coupler.getCouplingGapNm(z));
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.couplers.TestRacetrackCoupler_Strip" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
