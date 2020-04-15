package PhotonicElements.TimeDomain.ODEsolve;

public class ODESolver {
	
	public static int rungeKutta4(ODE ode, double range, double dx) {
	//Define some convenience variables to make the code more readable
	int numEqns = ode.getNumEqns();
	double x[] = ode.getX();
	double y[][] = ode.getY();
	//Define some local variables and arrays int i, j, k;
	double scale[] = {1.0, .5, .5, 1.0};
	double dy[][] = new double[4][numEqns];
	double ytmp[] = new double[numEqns];
	//Integrate the ODE over the desired range
	//Stop if you are going to overflow the matrices
	int i = 1;
	while(x[i-1] < range && i < ODE.MAX_STEPS - 1)
	{
	//Increment independent variable. Make sure it
	//doesn't exceed the range.
	x[i] = x[i-1] + dx;
	if(x[i] > range)
	{
	x[i] = range;
	dx = x[i] - x[i-1];
	}
	//First Runge-Kutta step
	ode.getFunction(x[i-1], dy[0], y[i-1]);
	//Runge-Kutta steps 2-4
	for(int k = 1; k < 4; ++k)
	{
	for(int j = 0; j < numEqns; ++j)
	ytmp[j] = y[i-1][j] + scale[k]*dx*dy[k-1][j];
	ode.getFunction(x[i-1] + scale[k]*dx, dy[k], ytmp);
	}
	//Update the dependent variables
	for(int j = 0; j < numEqns; ++j)
	{
	y[i][j] = y[i-1][j] + dx*(dy[0][j] + 2.0*dy[1][j] + 2.0*dy[2][j] + dy[3][j])/6.0;
	}
	//Increment i
	++i;
	} //end of while loop
	//return the number of steps
	//computed
	return i;
	}


}