package PhotonicElements.TimeDomain.ODEsolve.ODEs;

import PhotonicElements.TimeDomain.ODEsolve.ODE;

public class ODE3 extends ODE {

	/**
	 * This one solves for d2y/dx2 = -aplha^2*y --> dy/dx = z & dz/dx = -alpha^2*y
	 */
	
	double alpha = 2 *Math.PI ;
	
	public ODE3(int numEqns, int numFreeVariables) {
		super(numEqns, numFreeVariables);
	}

	@Override
	public void getFunction(double x, double dy[], double ytmp[]) {
		double y = ytmp[0] ;
		double z = ytmp[1] ;
		dy[0] = z ;
		dy[1] = -alpha*alpha*y ;
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
	
	
}