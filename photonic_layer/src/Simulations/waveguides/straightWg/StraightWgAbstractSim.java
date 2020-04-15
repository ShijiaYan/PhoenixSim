package Simulations.waveguides.straightWg;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.StraightWaveguide.StraightWgAbstract;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class StraightWgAbstractSim implements Experiment {

	StraightWgAbstract wg1, wg2 ;

	public StraightWgAbstractSim(
			StraightWgAbstract wg1,
			StraightWgAbstract wg2
			) {
		this.wg1 = wg1 ;
		this.wg2 = wg2 ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		int steps = 10 ;
		Complex port1In = Complex.ONE, port2In = Complex.ZERO ;
		wg1.initializePorts();
		wg2.initializePorts();
		for(int i=0; i<steps; i++){
			wg1.connectPorts(port1In, wg2.port1);
			wg2.connectPorts(wg1.port2, port2In);
			port1In = port2In = Complex.ZERO ;
		}

		DataPoint dp = new DataPoint();
		dp.addProperty("loss of wg1", MoreMath.Conversions.todB(wg1.S21.absSquared())) ;
		dp.addProperty("loss of wg2", MoreMath.Conversions.todB(wg2.S21.absSquared())) ;
		dp.addResultProperty("loss of wg1+wg2", MoreMath.Conversions.todB(wg2.getPort2().absSquared())) ;
		man.addDataPoint(dp);
	}

}
