package Simulations.waveguides.curvedWg;

import static java.lang.Math.* ;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.IntegralFunction;

public class CurvedWgLoss {

	static double A = 5 ; // in micron
	static double B = 3 ; // in micron

	public static void main(String[] args){
		double[] tt = MoreMath.linspace(0, 2*PI, 1000) ;
		double[] RR = new double[1000] ;
 		for(int i=0; i<1000; i++){
			RR[i] = getLossDB(0, tt[i]) ;
		}
 		MatlabChart fig = new MatlabChart() ;
 		fig.plot(tt, RR);
 		fig.RenderPlot();
 		fig.run(true);
	}


	private static double getAlpha(double R){
		double a = 2096.3 ;
		double b = 2.9123 ;
		double c = 0 ;
		double alpha = a/pow(R, b) + c ;
		return alpha ;
	}

	@SuppressWarnings("unused")
	private static double getR(double t){
		double x = A*cos(t)  ;
		double xPrime = -A * sin(t) ;
		double xDoublePrime = -A * cos(t) ;
		double y = B*sin(t) ;
		double yPrime = B*cos(t) ;
		double yDoublePrime = -B*sin(t) ;
		double num = pow(xPrime*xPrime+yPrime*yPrime, 1.5) ;
		double denom = abs(xPrime*yDoublePrime-yPrime*xDoublePrime) ;
		return (num/denom) ;
	}

	@SuppressWarnings("unused")
	private static double getDs(double t){
		double x = A*cos(t)  ;
		double xPrime = -A * sin(t) ;
		double y = B*sin(t) ;
		double yPrime = B*cos(t) ;
		double ds = sqrt(xPrime*xPrime+yPrime*yPrime) ;
		return ds ;
	}

	private static double getLossDB(double tStart, double tEnd){
		IntegralFunction func = new IntegralFunction() {
			public double function(double t) {
				return getAlpha(getR(t))*getDs(t)*1e-4;
			}
		};

		AdaptiveIntegral integral = new AdaptiveIntegral(func, tStart, tEnd) ;
		return integral.getIntegral() ;
	}

	@SuppressWarnings("unused")
	private static double getLossDBperCm(double tStart, double tEnd){
		return (getLossDB(tStart, tEnd)/getLength(tStart, tEnd)) ;
	}

	private static double getLength(double tStart, double tEnd){
		IntegralFunction func = new IntegralFunction() {
			public double function(double t) {
				return getDs(t)*1e-4;
			}
		};
		AdaptiveIntegral integral = new AdaptiveIntegral(func, tStart, tEnd) ;
		return integral.getIntegral() ;
	}

}
