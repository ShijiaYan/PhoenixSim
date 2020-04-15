package Simulations.rings.bending_loss;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Fitter;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Function;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.fitters.MarquardtFitter;
import javafx.application.Application;
import javafx.stage.Stage;

public class PowerLawFitting extends Application {
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		double[] R_um = {6.002860769052747,	6.5009992536503995,	7.003309466443254,	7.499069820196837,	8.005345815768793,	8.48415300252806,	8.99400910128169,	9.507351691664226,	9.989682814673506} ;
//		double[] loss_dBperCm = {42.055058127422534,	20.991284047603628,	10.62034815844122,	5.265272936095723,	2.555683509725197,	1.224524100693259,	0.4991239012954523,	0.379962143477368,	0.2062214901823534} ;
		
		double[] R_um = {6.00744159,
				6.238099414,
				6.489487544,
				6.744350216,
				6.995801315,
				7.495307682,
				8.00518595,
				8.501298736,
				8.993972964,
				9.98966858
} ;
		double[] loss_dBperCm = {13.48816752,
				9.15418058,
				6.245565161,
				4.04970708,
				2.676338398,
				1.135902117,
				0.472642316,
				0.193337359,
				0.078558913,
				0.123045856
} ;

		
		double[][] R_values = new double[R_um.length][1] ;
		for(int i=0; i<R_um.length; i++){
			R_values[i][0] = R_um[i] ;
		}
		Function func = new Function(){
			@Override
			public double evaluate(double[] values, double[] parameters) {
				double R = values[0] ;
				double a = parameters[0] ;
				double b = parameters[1] ; 
				return (a*Math.pow(R, -b));
			}

			@Override
			public int getNParameters() {
				return  2;
			}

			@Override
			public int getNInputs() {
				return 1;
			}
		} ;
		
		Fitter fitter = new MarquardtFitter(func) ;
		fitter.setParameters(new double[]{1e8, 1});
		fitter.setData(R_values, loss_dBperCm);
		fitter.fitData();
		
		double[] params = fitter.getParameters(); 
		MoreMath.Arrays.show(params);
		
		MatlabChart fig = new MatlabChart() ;
//		fig.plot(R_um, loss_dBperCm, "b");
		double[] R_dense = MoreMath.linspace(5, 12, 1000) ;
		double[] loss_dense = new double[R_dense.length] ;
		for(int i=0; i<R_dense.length; i++){
			loss_dense[i] = func.evaluate(new double[]{R_dense[i]}, fitter.getParameters()) ;
		}
		fig.plot(R_dense, loss_dense, "r");
		fig.RenderPlot();
//		fig.markerON(0);
		fig.run(true);
		
		fig.exportToFile();
	}

}
