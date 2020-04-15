package Simulations.timeDomain.ode;

import PhotonicElements.TimeDomain.ODEsolve.ODE;
import PhotonicElements.TimeDomain.ODEsolve.ODESolver;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;


public class TestODE implements Experiment {
	
	double x_initial, x_end, x, y ;
	double[] X, Y ;
	
	public TestODE(
			@ParamName(name="Choose ODE") ODE ode,
			@ParamName(name="Set initial point") double x_initial,
			@ParamName(name="Set evaluation point") double x_eval
			){
		ode.setInitialConditions(x_initial, new double[] {1});
		ODESolver.rungeKutta4(ode, x_eval, (x_eval - x_initial)/1e3) ;
		X = ode.getX() ;
		Y = ode.getYVariable(0) ;
		x = x_eval ;
		y = Y[Y.length-1] ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("x", x);
		dp.addResultProperty("y", y);
		man.addDataPoint(dp);
	}



}
