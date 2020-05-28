package Simulations.timeDomain.ode;

import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.plot.PlotGraph;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootDerivFunction;
import flanagan.roots.RealRootFunction;

public class TestDummy {

	public static void main(String[] args) {
		
		RealRootFunction func = x -> {
            double y = x*x + Math.sin(x*x) - 1/x -5 ;
            return y ;
        };
	
		RealRootDerivFunction dfunc = x -> {
            double y = x*x+Math.sin(x*x) - 1/x - 5 ;
            double yPrime = 2*x + 2*x*Math.cos(x*x) + 1/(x*x) ;
            return new double[] {y, yPrime} ;
        };
		
		RealRoot rootFinder = new RealRoot() ;
		double root = rootFinder.bisectNewtonRaphson(dfunc, 0.1, 3) ;
		System.out.println(root) ;
		
		double[] x = MoreMath.linspace(0.1, 4, 1000) ;
		double[] y = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			y[i] = func.function(x[i]) ;
		}
		
		PlotGraph pt = new PlotGraph(x, y) ;
		pt.plot();
		
	}
	
	
}
