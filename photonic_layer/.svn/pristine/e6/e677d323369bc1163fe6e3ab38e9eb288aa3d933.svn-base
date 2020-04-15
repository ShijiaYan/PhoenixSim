package Simulations.nonlinearity.rings;

import PhotonicElements.Nonlinearity.RingStructures.AddDropFirstOrderNL;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestAddDropFirstOrderNL implements Experiment {

	AddDropFirstOrderNL ringNL ;

	public TestAddDropFirstOrderNL(
			AddDropFirstOrderNL ringNL
			){
		this.ringNL = ringNL ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", ringNL.getWavelengthNm());
		dp.addProperty("input Power (mW)", ringNL.pin_mW);
		dp.addProperty("Convergence Steps", ringNL.steps);
		dp.addResultProperty("Pin Cwg1 (mW)", ringNL.pin_Cwg1);
		dp.addResultProperty("Pin Cwg2 (mW)", ringNL.pin_Cwg2);
		dp.addResultProperty("|S41|^2 (dB)", 10*Math.log10(ringNL.getS41().absSquared()));
		dp.addResultProperty("|S21|^2 (dB)", 10*Math.log10(ringNL.getS21().absSquared()));
		dp.addResultProperty("drop Power (mW)", ringNL.getS41().absSquared() * ringNL.pin_mW);
		man.addDataPoint(dp); 
	}

}
