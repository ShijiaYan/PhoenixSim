package Simulations.fourier;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.Functions.InverseFourier.InverseFourierFunction;
import PhotonicElements.Utilities.Functions.InverseFourier.InverseFourierTransform;
import PhotonicElements.Utilities.MathLibraries.Complex;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestInverseFourierTransform extends Application {

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		InverseFourierFunction func = new InverseFourierFunction() {
			double f0 = 30e3 ;
			double nu = 1.3 ;
			private double getK(double f){
				return Math.sqrt(f/f0) ;
			}
			
			private Complex getComplexK(double f){
				return new Complex(getK(f), getK(f)) ;
			}
			private Complex getComplexFunc(double f){
				Complex num = Complex.ONE.minus(getComplexK(f).times(-2*(nu-1)).exp()).times(getComplexK(f).times(-1).exp()) ;
				Complex denum = Complex.ONE.minus(getComplexK(f).times(-2* nu).exp()) ;
				if(f == 0){
					return new Complex((nu-1)/nu, 0) ;
				}
				else{
					return num.divides(denum) ;
				}
				
			}
			@Override
			public double realPart(double f) {
				return getComplexFunc(f).re() ;
			}

			@Override
			public double imagPart(double f) {
				return getComplexFunc(f).im()  ;
			}
		};

		InverseFourierTransform invFT = new InverseFourierTransform(func) ;
//		invFT.setTimeResAndFreqRes(1e-7, 1e4);
//		invFT.setTimeDurationAndNumSamples(200e-6, 1000);
		invFT.setFreqBandwidthAndSamples(2e8, 1000);

		System.out.println(invFT.M + "  ,   " + invFT.N);
		System.out.println(invFT.df + "  ,   " + invFT.fs);
		System.out.println(invFT.ts + "  ,   " + invFT.N*invFT.ts);

		invFT.performIFT();

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(invFT.freq, invFT.ftAmp);
		fig1.RenderPlot();
//		fig1.exportToMatlab();
		fig1.run(true);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(invFT.time, invFT.funcReal, "r");
//		fig2.plot(invFT.time, invFT.funcImag, "k");
		fig2.RenderPlot();
//		fig2.exportToMatlab();
		fig2.run(true);
	}

}
