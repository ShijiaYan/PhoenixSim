package Simulations.nonlinearity.curvedWg;

import PhotonicElements.Nonlinearity.CurvedWaveguide.CurvedWgKerr;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCwgKerrNL implements Experiment {

	CurvedWgKerr wgKerr ;
	
	public TestCwgKerrNL(
			CurvedWgKerr wgKerr
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
