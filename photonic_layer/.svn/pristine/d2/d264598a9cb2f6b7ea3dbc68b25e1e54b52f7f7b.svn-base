package Simulations.nonlinearity.straightWg;

import PhotonicElements.Nonlinearity.StraightWaveguide.StraightWgThermal;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestWgThermalNL implements Experiment {

	StraightWgThermal wgThermal ;
	
	public TestWgThermalNL(
			StraightWgThermal wgThermal
			) {
		this.wgThermal = wgThermal ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Input Power (mW)", wgThermal.getPinMw());
		dp.addResultProperty("Input Power (mW)", wgThermal.getPinMw());
		dp.addResultProperty("Excess Alpha Thermal (1/cm)", wgThermal.getDalphaThermal()/100);
		dp.addResultProperty("Phase Shift Thermal (degree)", wgThermal.getExcessPhaseThermal() * 180/Math.PI);
		dp.addResultProperty("Delta T (K)", wgThermal.getDeltaT());
		dp.addResultProperty("DNeff", wgThermal.getDnEffThermal());
		man.addDataPoint(dp);
	}

}
