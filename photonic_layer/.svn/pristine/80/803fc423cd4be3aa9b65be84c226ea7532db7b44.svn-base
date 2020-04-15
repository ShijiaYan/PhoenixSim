package Simulations.waveguides.straightWg;

import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWg;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class StraightWgSim implements Experiment {

	StraightWg wg, wg1 ;

	public StraightWgSim(
			Wavelength inputLambda,
			WgProperties wgProp,
			double length1Micron,
			double length2Micron
			){
		this.wg = new StraightWg(inputLambda, wgProp, length1Micron, false, null, false, null) ;
		this.wg1 = new StraightWg(inputLambda, wgProp, length2Micron, false, null, false, null) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		wg.initializePorts();
		wg1.initializePorts();
		int steps = 100 ;
		Complex portIn = Complex.ONE ;
		for(int i=0; i<steps; i++){
			wg.connectPorts(portIn, wg1.port1);
			wg1.connectPorts(wg.port2, Complex.ZERO);
			portIn = Complex.ZERO ;
		}

		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", wg.getWavelength().getWavelengthNm());
		dp.addProperty("length 1", wg.getWgLengthMicron());
		dp.addProperty("length 2", wg1.getWgLengthMicron());
		dp.addResultProperty("power at wg1", MoreMath.Conversions.todB(wg.getPort2().absSquared()));
		dp.addResultProperty("power at wg2", MoreMath.Conversions.todB(wg1.getPort2().absSquared()));
		dp.addResultProperty("Neff", wg.getEffectiveIndex());
		man.addDataPoint(dp);

	}

}
