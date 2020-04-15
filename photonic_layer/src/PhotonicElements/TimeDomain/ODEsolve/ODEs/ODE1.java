package PhotonicElements.TimeDomain.ODEsolve.ODEs;

import PhotonicElements.TimeDomain.ODEsolve.ODE;

public class ODE1 extends ODE {
	
	/**
	 * This one solves for dy/dx = f(x,y) = y
	 */
	
	public ODE1(int numEqns, int numFreeVariables) {
		super(numEqns, numFreeVariables);
	}

	@Override
	public void getFunction(double x, double dy[], double ytmp[]) {
		int n = dy.length ;
		for(int i=0; i<n; i++){
//			dy[i] = fi(x, ytemp[i]) ; --> this is basically calculating the k factors.
			dy[i] = ytmp[i] ;
		}
	}
	
	@Override
	public void getError(double E[], double endY[]) {
		
	}
	
	@Override
	public void setInitialConditions(double x0, double Y[]) {
		int n = getNumEqns() ;
		setOneX(0, x0);
		for(int i=0; i<n; i++){
			setOneY(0, i, Y[i]);
		}
	}
	
	// set the self initial conditions
	@Override
	public void setInitialConditions(double x0) {
		int n = getNumEqns() ;
		double[] Y = {0} ;
		setOneX(0, x0);
		for(int i=0; i<n; i++){
			setOneY(0, i, Y[i]);
		}
	}
	
	
}