package Simulations.fourier;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.Functions.Convolution.Convolution;
import PhotonicElements.Utilities.Functions.Convolution.ConvolutionFunctionPair;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestConvolution extends Application {

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConvolutionFunctionPair funcs = new ConvolutionFunctionPair() {
			@Override
			public double func2(double t) {
				if(t<=0 || t>1){return 0;}
				else{return t ;}
			}

			@Override
			public double func1(double t) {
				if(t<=0 || t>2){return 0;}
				else{return t ;}
			}
		};

		Convolution conv = new Convolution(funcs, 5, 1e-2) ;
		conv.performConvolution();

		System.out.println(conv.M + "  ,   " + conv.N);
		System.out.println(conv.fs + "  ,   " + conv.df);
		System.out.println(conv.ts);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(conv.time, conv.func1, "b");
		fig1.RenderPlot();
		fig1.run(true);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(conv.time, conv.func2, "r");
		fig2.RenderPlot();
		fig2.run(true);

		MatlabChart fig3 = new MatlabChart() ;
		fig3.plot(conv.time, conv.convResult, "k");
		fig3.RenderPlot();
		fig3.markerON();
		fig3.run(true);
	}

}
