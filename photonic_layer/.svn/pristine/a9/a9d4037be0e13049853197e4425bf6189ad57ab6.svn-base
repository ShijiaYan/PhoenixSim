package Simulations.couplers;

import PhotonicElements.DirectionalCoupler.DistributedCoupler.DistributedCouplerStripWg;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCoupledWg implements Experiment {

	DistributedCouplerStripWg DC ;
	Wavelength inputLambda ;
	
	public TestCoupledWg(
			@ParamName(name="Wavelength") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Length of Coupler (micron)") double lengthMicron,
			@ParamName(name="Gap size (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.DC = new DistributedCouplerStripWg(inputLambda, wgProp, lengthMicron, gapNm) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Gap size (nm)", DC.gapNm);
		dp.addProperty("wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("Length (micron)", DC.lengthMicron);
		dp.addResultProperty("Even index", DC.getEffectiveIndexEven());
		dp.addResultProperty("Odd index", DC.getEffectiveIndexOdd());
		dp.addResultProperty("(nEven-nOdd)/2", (DC.getEffectiveIndexEven()-DC.getEffectiveIndexOdd())/2);
		dp.addResultProperty("t^2", DC.S21.absSquared());
		dp.addResultProperty("k^2", DC.S31.absSquared());
//		dp.addResultProperty("Coeffs Even", DC.getCoeffsFromDataBase(5, true)[8]);
//		dp.addResultProperty("Coeffs Odd", DC.getCoeffsFromDataBase(5, false)[8]);
//		dp.addResultProperty("Coeffs Even", DC.getCoeffs(true)[8]);
//		dp.addResultProperty("Coeffs Odd", DC.getCoeffs(false)[8]);
		man.addDataPoint(dp);
	}
	
}
