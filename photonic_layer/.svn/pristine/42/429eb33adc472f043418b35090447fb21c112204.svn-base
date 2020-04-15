package People.Meisam.GUI.PhoenixSim.ModulesLibrary.SingleVariableCalculus.Tests;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.Functions.Function.ExplicitFunction;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.interpolation.CubicSpline;

public class Tests {
	
	public static void main(String[] args){
		
		ExplicitFunction func = new ExplicitFunction("sin(x)", "x") ;
		double[] x = MoreMath.linspace(-10, 10, 100) ;
		double[] y = func.getFuncAt(x) ;
		double[] y_integral = func.integrate(Math.PI/2, x) ;
		MatlabChart fig = new MatlabChart() ;
		
		CubicSpline cub = new CubicSpline(x, y) ;
		
		int M = x.length ;
		int N = 100*M ; // a very dense plot
		double x_min = MoreMath.Arrays.FindMinimum.getValue(x) ;
		double x_max = MoreMath.Arrays.FindMaximum.getValue(x) ;
		double[] x_dense = MoreMath.linspace(x_min, x_max, N) ;
		
		double[] zCub = new double[N] ;
		double[] zIntegral = new double[N] ;
		for(int i=0; i<x_dense.length; i++){
			zCub[i] = cub.interpolate(x_dense[i]) ;
			zIntegral[i] = new CubicSpline(x, y_integral).interpolate(x_dense[i]) ;
		}
		
		
		fig.plot(x, y, "-b");
		fig.plot(x_dense, zCub, ":r");
		fig.plot(x, y_integral);
		fig.plot(x_dense, zIntegral, "r");
		fig.RenderPlot();
		fig.run();


	}
	
	
	
	
	
	
	
	

}
