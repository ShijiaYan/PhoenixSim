package Simulations.nonlinearity.curvedWg;

import PhotonicElements.Nonlinearity.CurvedWaveguide.CurvedWgTPA;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCwgTPANL implements Experiment {

	CurvedWgTPA wgTPA ;
	
	public TestCwgTPANL(
			CurvedWgTPA wgTPA
			) {
		this.wgTPA = wgTPA ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Input Power (mW)", wgTPA.getPinMw());
		dp.addResultProperty("Input Power (mW)", wgTPA.getPinMw());
		dp.addResultProperty("Output Power (mW)", wgTPA.getPinMw()*wgTPA.getS21().absSquared());
		dp.addResultProperty("Excess Alpha TPA (1/cm)", wgTPA.getDalphaTPA()/100);
		dp.addResultProperty("Phase Shift TPA (degree)", wgTPA.getExcessPhaseTPA() * 180/Math.PI);
		dp.addResultProperty("DNeff", wgTPA.getDnEffTPA());
		dp.addProperty("Carrier life time (s)", wgTPA.gettau());
		man.addDataPoint(dp);
	}

}
