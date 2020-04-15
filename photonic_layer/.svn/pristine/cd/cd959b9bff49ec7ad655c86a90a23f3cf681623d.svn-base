package Simulations.nonlinearity.rings;

import PhotonicElements.Nonlinearity.RingStructures.RingWgNL;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestRingWgNL implements Experiment {

	RingWgNL ringNL ;
	
	
	public TestRingWgNL(
			RingWgNL ringNL
			){
		this.ringNL = ringNL ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", ringNL.getWavelengthNm());
		dp.addProperty("input Power (mW)", ringNL.getPin_mW());
		dp.addProperty("Convergence Steps", ringNL.steps);
		dp.addResultProperty("Pin NCwg (mW)", ringNL.getPin_mW_NCwg());
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(ringNL.getS21().absSquared()));
		man.addDataPoint(dp); 
	}

}
