package Simulations.nonlinearity.straightWg;

import PhotonicElements.Nonlinearity.StraightWaveguide.StraightWgKerr;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestWgKerrNL implements Experiment {

	StraightWgKerr wgKerr ;
	
	public TestWgKerrNL(
			StraightWgKerr wgKerr
			) {
		this.wgKerr = wgKerr ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Input Power (mW)", wgKerr.getPinMw());
		dp.addProperty("Wg length (micron)", wgKerr.getWgLengthMicron());
		dp.addResultProperty("Output Power (mW)", wgKerr.getPinMw()*wgKerr.getS21().absSquared());
		dp.addResultProperty("Excess Alpha Kerr (1/cm)", wgKerr.getDalphaKerr()/100);
		dp.addResultProperty("Phase Shift Kerr (degree)", wgKerr.getExcessPhaseKerr() * 180/Math.PI);
		dp.addResultProperty("DNeff", wgKerr.getDnEffKerr());
		man.addDataPoint(dp);
	}

}
