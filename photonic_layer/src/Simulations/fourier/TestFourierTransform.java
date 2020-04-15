package Simulations.fourier;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.Functions.Fourier.FourierFunction;
import PhotonicElements.Utilities.Functions.Fourier.FourierTransform;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestFourierTransform extends Application {

	public static void main(String[] args){
		launch(args) ;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FourierFunction func = new FourierFunction() {
			public double realPart(double t) {
				if(t >= 1e-6 && t<=10e-6){
					return 1 ;}
				else{
					return 0 ;
				}
			}
			@Override
			public double imagPart(double t) {
				return 0;
			}
		};
		FourierTransform ft = new FourierTransform(func) ;
//		ft.setTimeResAndFreqRes(1e-8, 1e2);
//		ft.setTimeDurationAndNumSamples(10e-6, 1000);
//		ft.setFreqResAndBandwidth(1e8, 1e2);
		ft.setFreqBandwidthAndSamples(1e8, 20000);

		System.out.println(ft.M + "  ,   " + ft.N);
		System.out.println(ft.df + "  ,   " + ft.fs);
		System.out.println(ft.ts + "  ,   " + ft.N*ft.ts);
		ft.performFT();

		MatlabChart fig = new MatlabChart() ;
		fig.plot(ft.time, ft.funcReal, "b");
		fig.RenderPlot();
		fig.run(true);
//		fig.exportToMatlab();

		MatlabChart fig1 = new MatlabChart() ;
//		fig1.plot(ft.freq, ft.ftReal, "b");
//		fig1.plot(ft.freq, ft.ftImag, "r");
		fig1.plot(MoreMath.Arrays.times(ft.freq, 1), ft.ftAmp, "r");
		fig1.RenderPlot();
//		fig1.markerON();
//		fig1.exportToMatlab();
		fig1.run(true);



	}

}
