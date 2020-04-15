package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledStrip.ModeCoupledStripWgTE;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestModeCoupledStripWgTE implements Experiment {

	StripWg stripWg ;
	ModeCoupledStripWgTE stripTE_Coupled ;
	ModeStripWgTE stripTE ;
	double gapNm ;
	
	public TestModeCoupledStripWgTE(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="width (nm)") double widthNm,
			@ParamName(name="height (nm)") double heightNm,
			@ParamName(name="gap size (nm)") double gapNm
			){
		this.gapNm = gapNm ;
		stripWg = new StripWg(inputLambda, widthNm, heightNm) ;
		stripTE_Coupled = new ModeCoupledStripWgTE(stripWg, gapNm, 0, 0); // TE00 mode (fundamental mode)
		stripTE = new ModeStripWgTE(stripWg, 0, 0); // TE00 mode (fundamental mode)
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", stripWg.getWidthNm());
		dp.addProperty("Height (nm)", stripWg.getHeightNm());
		dp.addProperty("Gap size (nm)", gapNm);
		dp.addProperty("Wavelength (nm)", stripWg.getWavelengthNm());
		dp.addResultProperty("Core index", stripWg.getCoreIndex());
		dp.addResultProperty("Cladding index", stripWg.getCladIndex());
		dp.addResultProperty("n_eff (TE)", stripTE.getEffectiveIndex());
		dp.addResultProperty("n_eff Even (TE)", stripTE_Coupled.getNeffEven());
		dp.addResultProperty("n_eff Odd (TE)", stripTE_Coupled.getNeffOdd());
		man.addDataPoint(dp);
	}
}
