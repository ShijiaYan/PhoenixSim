package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestModeStripWgTM implements Experiment {

	StripWg stripWg ;
	ModeStripWgTM stripTM ;
	
	public TestModeStripWgTM(
			ModeStripWgTM stripTM
			){
		this.stripWg = stripTM.getStripWg() ;
		this.stripTM = stripTM ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", stripWg.getWidthNm());
		dp.addProperty("Height (nm)", stripWg.getHeightNm());
		dp.addProperty("Wavelength (nm)", stripWg.getWavelengthNm());
		dp.addResultProperty("Core index", stripWg.getCoreIndex());
		dp.addResultProperty("Cladding index", stripWg.getCladIndex());
		dp.addResultProperty("Neff (TM)", stripTM.getEffectiveIndex());
		man.addDataPoint(dp);
	}
}
