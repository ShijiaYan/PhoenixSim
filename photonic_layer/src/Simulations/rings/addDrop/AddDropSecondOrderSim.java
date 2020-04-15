package Simulations.rings.addDrop;

import PhotonicElements.RingStructures.AddDrop.Graph.AddDropSecondOrder;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class AddDropSecondOrderSim implements Experiment {

	AddDropSecondOrder adr ;
	
	public AddDropSecondOrderSim(
			AddDropSecondOrder adr
			) {
		this.adr = adr ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("phase (rad)", adr.phi_rad);
		dp.addResultProperty("Drop (dB)", MoreMath.Conversions.todB(adr.getS31().absSquared()));
		man.addDataPoint(dp);
	}

}
