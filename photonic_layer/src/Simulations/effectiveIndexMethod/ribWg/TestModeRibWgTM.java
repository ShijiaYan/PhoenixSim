package Simulations.effectiveIndexMethod.ribWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.RibWg.ModeRibWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestModeRibWgTM implements Experiment {

	RibWg ribWg ;
	ModeRibWgTM ribTM ;
	
	public TestModeRibWgTM(
			ModeRibWgTM ribTM 
			){
		this.ribTM = ribTM ;
		ribWg = ribTM.getRibWg() ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", ribWg.getWidthNm());
		dp.addProperty("Height (nm)", ribWg.getHeightNm());
		dp.addProperty("M number", ribTM.getMnumber());
		dp.addProperty("N number", ribTM.getNnumber());
		dp.addProperty("Wavelength (nm)", ribWg.getWavelengthNm());
		dp.addResultProperty("Neff (TM)", ribTM.getEffectiveIndex());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.effectiveIndexMethod.ribWg.TestModeRibWgTM" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}
}
