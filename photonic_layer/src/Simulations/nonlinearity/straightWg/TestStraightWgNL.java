package Simulations.nonlinearity.straightWg;

import PhotonicElements.Nonlinearity.StraightWaveguide.StraightWgNL;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestStraightWgNL implements Experiment {

	StraightWgNL wgNL ;
	
	public TestStraightWgNL(
			StraightWgNL wgNL
			) {
		this.wgNL = wgNL ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Input Power (mW)", wgNL.getPinMw());
		dp.addResultProperty("Input Power (mW)", wgNL.getPinMw());
		dp.addProperty("Wg length (micron)", wgNL.getWgLengthMicron());
		dp.addResultProperty("Output Power (mW)", wgNL.getPinMw()*wgNL.getS21().absSquared());
		dp.addResultProperty("Excess Alpha (1/cm)", wgNL.getDalphaNL()/100);
		dp.addResultProperty("Phase Shift (degree)", wgNL.getExcessPhaseNL() * 180/Math.PI);
		man.addDataPoint(dp);
	}


}
