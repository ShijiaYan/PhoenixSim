package Simulations.math;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.RealRootFinder;
import flanagan.roots.RealRootFunction;

public class TestRealRootFinder {

	public static void main(String[] args){
		RealRootFunction func = new RealRootFunction(){
			@Override
			public double function(double x) {
				return Math.sin(Math.sqrt(x));
			}
		} ;

		double xStart = -6*Math.PI ;
		double xEnd = 25*Math.PI ;

		double[] x = MoreMath.linspace(xStart, xEnd, 1000) ;
		double[] y = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			y[i] = func.function(x[i]) ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.RenderPlot();
		fig.run(true);

		RealRootFinder rootFinder = new RealRootFinder(func, xStart, xEnd) ;
		rootFinder.findAllRoots();
		rootFinder.showAllRoots();



	}









}
