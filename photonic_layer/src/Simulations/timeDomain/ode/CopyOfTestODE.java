package Simulations.timeDomain.ode;

import PhotonicElements.TimeDomain.ODEsolve.ODE;
import PhotonicElements.TimeDomain.ODEsolve.ODESolver;
import PhotonicElements.TimeDomain.ODEsolve.ODEs.ODE1;
import flanagan.plot.PlotGraph;

public class CopyOfTestODE {
	
	public static void main(String[] args){
		
		// step 1: choose the ODE that you want to solve	
		ODE ode = new ODE1(1,1) ;
		// step 2: set the initial conditions and range of the ODE
		ode.setInitialConditions(0, new double[] {5});
		// step 3: call the ODE solver and solve the ODE
		ODESolver.rungeKutta4(ode, 10, 10/1e3) ;
		// step 4: plot the solution
		double[] X = ode.getX() ;
		double[] Y = ode.getYVariable(0) ;
		PlotGraph plot = new PlotGraph(X, Y) ;
		plot.plot();
		
	}

}
