package Simulations.math;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.IntegralFunction;

public class GaussianDistribution {

	public static void main(String[] args){
		double [] alpha = MoreMath.linspace(0, 10, 1000) ;
		IntegralFunction gaussian = new IntegralFunction(){
			@Override
			public double function(double x) {
				return 1/Math.sqrt(2*Math.PI) * Math.exp(-x*x/2);
			}
		} ;

		double[] prob = new double[alpha.length] ;
		for(int i=0; i<alpha.length; i++){
			AdaptiveIntegral integral = new AdaptiveIntegral(gaussian, -alpha[i]/2, alpha[i]/2) ;
			prob[i] = integral.getIntegral() ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(alpha, prob, 3f);
		fig.RenderPlot();
		fig.run(true);








	}

}
