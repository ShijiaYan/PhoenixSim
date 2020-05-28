package PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.OptimumCurve;

import org.apache.commons.math3.special.Gamma;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.IntegralFunction;

public class HyperGeometric2F1 {

	double a, b, c ;

	public HyperGeometric2F1(double a, double b, double c) {
		this.a = a ;
		this.b = b ;
		this.c = c ;
	}

	private double getCoeff(){
		double coeff = Gamma.gamma(c)/(Gamma.gamma(b) * Gamma.gamma(c-b)) ;
		return coeff ;
	}

	private double getIntegral(double x){
		IntegralFunction func = t -> Math.pow(t, b-1)*Math.pow(1-t, c-b-1)*Math.pow(1-t*x, -a);
		AdaptiveIntegral integral = new AdaptiveIntegral(func, 0, 1) ;
		return integral.getIntegral() ;
	}

	public double getValue(double x){
		return getCoeff()*getIntegral(x) ;
	}

	// for test
	public static void main(String[] args){
		HyperGeometric2F1 hg = new HyperGeometric2F1(0.5, (3*2.93-1)/(2*2.93), 1.5) ;
		double[] x = MoreMath.linspace(-1, 10, 100) ;
		double[] y = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			y[i] = x[i] * hg.getValue(-x[i]*x[i]) ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.RenderPlot();
		fig.run(true);
	}

}
