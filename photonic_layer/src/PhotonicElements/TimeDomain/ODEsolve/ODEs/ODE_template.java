package PhotonicElements.TimeDomain.ODEsolve.ODEs;

import PhotonicElements.TimeDomain.ODEsolve.ODE;

public class ODE_template extends ODE {
	
	/**
	 * This one solves for dy1/dx = f1(x,y1,y2,...,yn), dy2/dx = f2(x,y1,y2,...,yn), ... , dyn/dx = fn(x,y1,y2,...,yn) 
	 * dy[i] = fi(x, ytemp[i]) ; --> this is basically calculating the k factors.
	 * ytmp = {y1, y2, y3, ...} --> array of all the functions: y1(x), y2(x), ..., yn(x)
	 */
	
	public ODE_template(int numEqns, int numFreeVariables) {
		super(numEqns, numFreeVariables);
	}

	@Override
	public void getFunction(double x, double dy[], double ytmp[]) {
		int n = dy.length ;
		for(int i=0; i<n; i++){
		//	dy[i] = fi(x, ytemp) ; --> this is basically calculating the k factors.
		}
	}
	
	@Override
	public void getError(double E[], double endY[]) {
		// no need to put something here
	}
	
	@Override
	public void setInitialConditions(double x0, double Y[]) {
		// here to set the initial conditions
		int n = getNumEqns() ;
		setOneX(0, x0);
		for(int i=0; i<n; i++){
			setOneY(0, i, Y[i]);
		}
	}
	
}
