package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestModeStripWgTE implements Experiment {

	StripWg stripWg ;
	ModeStripWgTE stripTE ;
	
	public TestModeStripWgTE(
			ModeStripWgTE stripTE
			){
		this.stripWg = stripTE.getStripWg() ;
		this.stripTE = stripTE ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", stripWg.getWidthNm());
		dp.addProperty("Height (nm)", stripWg.getHeightNm());
		dp.addResultProperty("DnSi", stripWg.getDnSi());
		dp.addResultProperty("DnSiO2", stripWg.getDnSiO2());
		dp.addProperty("Wavelength (nm)", stripWg.getWavelengthNm());
		dp.addResultProperty("Core index", stripWg.getCoreIndex());
		dp.addResultProperty("Cladding index", stripWg.getCladIndex());
		dp.addResultProperty("Neff (TE)", stripTE.getEffectiveIndex());
		dp.addResultProperty("Ng", stripTE.getGroupIndex());
		man.addDataPoint(dp);
	}
}
