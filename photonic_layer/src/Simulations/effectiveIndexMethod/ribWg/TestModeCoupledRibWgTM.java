package Simulations.effectiveIndexMethod.ribWg;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.CoupledRib.ModeCoupledRibWgIdenticalTM;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.RibWg.ModeRibWgTM;
import PhotonicElements.EffectiveIndexMethod.Structures.RibWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestModeCoupledRibWgTM implements Experiment {

	RibWg ribWg ;
	ModeCoupledRibWgIdenticalTM ribTM_Coupled ;
	ModeRibWgTM ribTM ;
	double gapNm ;
	
	public TestModeCoupledRibWgTM(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="width (nm)") double widthNm,
			@ParamName(name="height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm,
			@ParamName(name="gap size (nm)") double gapNm
			){
		this.gapNm = gapNm ;
		ribWg = new RibWg(inputLambda, widthNm, heightNm, heightSlabNm, 0, 0) ;
		ribTM_Coupled = new ModeCoupledRibWgIdenticalTM(ribWg, gapNm, 0, 0); // TE00 mode (fundamental mode)
		ribTM = new ModeRibWgTM(ribWg, 0, 0); // TE00 mode (fundamental mode)
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Width (nm)", ribWg.getWidthNm());
		dp.addProperty("Height (nm)", ribWg.getHeightNm());
		dp.addProperty("Gap size (nm)", gapNm);
		dp.addProperty("Wavelength (nm)", ribWg.getWavelengthNm());
		dp.addResultProperty("Core index", ribWg.getCoreIndex());
		dp.addResultProperty("Cladding index", ribWg.getCladIndex());
		dp.addResultProperty("n_eff (TE)", ribTM.getEffectiveIndex());
		dp.addResultProperty("n_eff Even (TE)", ribTM_Coupled.getNeffEven());
		dp.addResultProperty("n_eff Odd (TE)", ribTM_Coupled.getNeffOdd());
		man.addDataPoint(dp);
	}
}
