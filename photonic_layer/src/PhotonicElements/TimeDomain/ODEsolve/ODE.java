package PhotonicElements.TimeDomain.ODEsolve;

public class ODE {

	//This is used to allocate memory to the
	//x[] and y[][] arrays
	public static int MAX_STEPS = 999;
//	public static int MAX_STEPS = 999999;
	//numEqns = number of 1st order ODEs to be solved
	//numFreeVariables = number of free variables at domain boundaries
	//x[] = array of independent variables
	//y[][] = array of dependent variables
	private int numEqns, numFreeVariables;
	private double x[];
	private double y[][];
	
	public ODE(
			int numEqns, 
			int numFreeVariables
			){
		this.numEqns = numEqns;
		this.numFreeVariables = numFreeVariables;
		x = new double[MAX_STEPS];
		y = new double[MAX_STEPS][numEqns];
	}
	
	//these methods return the value of some of the fields public
	protected int getNumEqns( )
	{
	return numEqns;
	}
	
	public int getNumFreeVariables()
	{
	return numFreeVariables;
	}
	
	public double[] getX()
	{
	return x;
	}
	
	public double[][] getY()
	{
	return y;
	}
	
	public double[] getYVariable(int equation){
		double[] yvar = new double[MAX_STEPS] ;
		for(int i=0; i<yvar.length; i++){
			yvar[i] = getOneY(i, equation) ;
		}
		return yvar ;
	}
	
	public double getOneX(int step)
	{
	return x[step];
	}
	
	public double getOneY(int step, int equation)
	{
	return y[step][equation];
	}
	
	//This method lets you change one of the dependent or independent variables public
	public void setOneX(int step, double value)
	{
	x[step] = value;
	}
	
	public void setOneY(int step, int equation, double value)
	{
	y[step][equation] = value;
	}
	
	//These methods are implemented as stubs.
	//Subclasses of ODE will override them
	public void getFunction(double x, double dy[], double ytmp[]) {} 
	public void getError(double E[], double endY[]) {} 
	public void setInitialConditions(double x0, double Y[]) {} 

	public void setInitialConditions(double x0) {} 	// initial conditions are self defined within the ODE

}


