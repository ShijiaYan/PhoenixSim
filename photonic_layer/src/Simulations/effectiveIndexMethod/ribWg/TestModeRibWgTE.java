package Simulations.effectiveIndexMethod.ribWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.RibWg.ModeRibWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestModeRibWgTE implements Experiment {

	RibWg ribWg ;
	ModeRibWgTE ribTE ;
	
	public TestModeRibWgTE(
			ModeRibWgTE ribTE 
			){
		this.ribTE = ribTE ;
		ribWg = ribTE.getRibWg() ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", ribWg.getWidthNm());
		dp.addProperty("Height (nm)", ribWg.getHeightNm());
		dp.addProperty("Slab height (nm)", ribWg.getSlabHeightNm());
		dp.addProperty("Si Index", ribWg.getCoreIndex());
		dp.addProperty("SiO2 Index", ribWg.getCladIndex());
		dp.addProperty("M number", ribTE.getMnumber());
		dp.addProperty("N number", ribTE.getNnumber());
		dp.addProperty("Wavelength (nm)", ribWg.getWavelengthNm());
		dp.addResultProperty("Neff (TE)", ribTE.getEffectiveIndex());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.effectiveIndexMethod.ribWg.TestModeRibWgTE" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}
}
