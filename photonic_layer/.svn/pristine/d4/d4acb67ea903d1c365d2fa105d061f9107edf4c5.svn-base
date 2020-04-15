package Simulations.effectiveIndexMethod.slabWg.index_fitting;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.IntegralFunction;
import javafx.application.Application;
import javafx.stage.Stage;

public class BxFunction extends Application {

	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		double[] x = MoreMath.linspace(0, 100, 1000) ;
		double[] B = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			double xVal = x[i] ;
			IntegralFunction func = new IntegralFunction() {
				@Override
				public double function(double theta) {
					double y = 2*xVal*Math.exp(-xVal*(1-Math.cos(theta)))*Math.cos(theta) ;
					return y;
				}
			};
			AdaptiveIntegral integral = new AdaptiveIntegral(func, 0, Math.PI/2) ;
			B[i] = integral.getIntegral() ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, B);
		fig.RenderPlot();
		fig.xlabel("x");
		fig.ylabel("B(x) Coefficient"); 
		fig.run(true);
		fig.exportToMatlab();
	}

}
