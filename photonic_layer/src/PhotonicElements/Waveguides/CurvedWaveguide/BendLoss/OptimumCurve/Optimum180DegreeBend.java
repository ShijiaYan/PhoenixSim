package PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.OptimumCurve;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.DerivFunction;
import flanagan.integration.IntegralFunction;
import flanagan.integration.RungeKutta;
import flanagan.interpolation.LinearInterpolation;

public class Optimum180DegreeBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		double A = 1.12454/R ;
		double b = 2.9123 ;
		double p = (3*b-1)/(2*b) ;

		DerivFunction dFunction = new DerivFunction() {
			public double deriv(double x, double f) {
				return A*Math.pow(1+f*f, p);
			}
		};
		RungeKutta integration = new RungeKutta() ;
		integration.setInitialValueOfX(0);
		integration.setInitialValueOfY(0);

		integration.setStepSize(1e-5*R);

		double[] x = MoreMath.Arrays.concat(MoreMath.linspace(0, 0.98*R, 100), MoreMath.linspace(0.98*R, R, 300))  ;
		double[] f = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			integration.setFinalValueOfX(x[i]);
			f[i] = integration.fourthOrder(dFunction) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, f);
		fig.RenderPlot();
		fig.run(true);

		// now calculate y
		LinearInterpolation interpolation = new LinearInterpolation(x, f) ;
		IntegralFunction func = new IntegralFunction() {
			public double function(double x) {
				return interpolation.interpolate(x);
			}
		};

		double[] y = new double[x.length] ;

		for(int i=0; i<x.length ; i++){
			AdaptiveIntegral yIntegral = new AdaptiveIntegral(func, 0, x[i]) ;
			y[i] = yIntegral.getIntegral() ;
		}

		double[] xx = MoreMath.Arrays.concat(MoreMath.Arrays.times(x, -1), x) ;
		double[] yy = MoreMath.Arrays.concat(y, y) ;

		MatlabChart fig1= new MatlabChart() ;
		fig1.plot(xx, yy, "r");
		fig1.RenderPlot();
		fig1.run(true);

		// find radius of curvature
		double[] radius = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			radius[i] = 1/A * Math.pow(1+f[i]*f[i], 0.5/b) ;
		}

		MatlabChart fig2= new MatlabChart() ;
		fig2.plot(x, radius, "g");
		fig2.RenderPlot();
		fig2.run(true);


	}

}