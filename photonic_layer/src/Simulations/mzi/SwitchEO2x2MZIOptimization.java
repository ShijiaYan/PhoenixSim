package Simulations.mzi;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZI;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class SwitchEO2x2MZIOptimization implements Experiment {

	Switch2x2MZI sw ;

	public SwitchEO2x2MZIOptimization(
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="Length of Arm (um)") double lengthMicron,
			CompactCoupler DC1,
			CompactCoupler DC2,
			PlasmaDispersionModel plasmaEffect
			){
		this.sw = new Switch2x2MZI(inputLambda, wgProp, lengthMicron, DC1, DC2, plasmaEffect) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", sw.getWavelength().getWavelengthNm());
		dp.addProperty("length of arm (um)", sw.getUpperWg().getWgLengthMicron());
		dp.addProperty("Delta Alpha (1/cm)", sw.getPlasmaEffect().getDalphaPerCm());
		dp.addResultProperty("Thru Transmission (dB)", MoreMath.Conversions.todB(sw.S21.absSquared()));
		dp.addResultProperty("Cross Transmission (dB)", MoreMath.Conversions.todB(sw.S31.absSquared()));
		man.addDataPoint(dp);
	}

}
