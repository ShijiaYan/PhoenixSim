package PhotonicElements.TimeDomain.ODEsolve.ODEs.LinearODE;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FirstOrderODE {

	/*
	 * This solves the equation: a(x)*y'+b(x) = f(x), y(x0) = y0
	 */
	
	String ax, bx, fx ;
	double x0, y0 ;
	
	// creating the differential equation
	public FirstOrderODE(
			@ParamName(name="a(x) coefficient") String ax,
			@ParamName(name="b(x) coefficient") String bx,
			@ParamName(name="f(x) coefficient") String fx
			){
		this.ax = ax ;
		this.bx = bx ;
		this.fx = fx ;
	}
	
	// setting up the initial conditions
	public void setInitialConditions(double x0, double y0){
		this.x0 = x0 ;
		this.y0 = y0 ;
	}
	
}
