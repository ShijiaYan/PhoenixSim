package Simulations.effectiveIndexMethod.stripWg;

import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestStripWg implements Experiment {

	StripWg stripWg ;
	double xValNm, yValNm ;
	
	public TestStripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="width (nm)") double widthNm,
			@ParamName(name="height (nm)") double heightNm,
			@ParamName(name="X position (nm)") double xValNm,
			@ParamName(name="Y position (nm)") double yValNm
			){
		this.xValNm = xValNm ;
		this.yValNm = yValNm ;
		stripWg = new StripWg(inputLambda, widthNm, heightNm) ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("X position (nm)", xValNm);
		dp.addProperty("Y position (nm)", yValNm);
		dp.addProperty("Width (nm)", stripWg.getWidthNm());
		dp.addResultProperty("index profile", stripWg.getIndexProfile(xValNm, yValNm));
		dp.addProperty("Wavelength (nm)", stripWg.getWavelengthNm());
		man.addDataPoint(dp);
	}

}
