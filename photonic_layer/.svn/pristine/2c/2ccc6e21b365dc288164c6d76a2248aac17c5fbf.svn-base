package Simulations.rings.addDrop;

import PhotonicElements.RingStructures.AddDrop.AddDropFirstOrderFast;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class AddDropFirstOrderSim implements Experiment {

	AddDropFirstOrderFast adr ;
	
	public AddDropFirstOrderSim(
			AddDropFirstOrderFast adr
			) {
		this.adr = adr ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", adr.inputLambda.getWavelengthNm());
		dp.addProperty("Heater phase (rad)", adr.H.getPhaseShiftRadian());
		dp.addProperty("Heater phase (degree)", adr.H.getPhaseShiftDegree());
		dp.addResultProperty("Thru (dB)", MoreMath.Conversions.todB(adr.S21.absSquared()));
		dp.addResultProperty("Drop (dB)", MoreMath.Conversions.todB(adr.S41.absSquared()));
		man.addDataPoint(dp);
	}

}
