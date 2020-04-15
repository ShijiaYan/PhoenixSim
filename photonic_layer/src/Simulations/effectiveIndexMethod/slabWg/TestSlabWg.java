package Simulations.effectiveIndexMethod.slabWg;

import PhotonicElements.EffectiveIndexMethod.Structures.SlabWg;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestSlabWg implements Experiment {

	SlabWg slab ;
	
	public TestSlabWg(
			@ParamName(name="Slab Waveguide") SlabWg slab
			){
		this.slab = slab ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", slab.getWavelengthNm());
		dp.addProperty("Width (nm)", slab.getWidthNm());
		dp.addResultProperty("V param", slab.getNormalizedFreq());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.effectiveIndexMethod.slabWg.TestSlabWg" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
