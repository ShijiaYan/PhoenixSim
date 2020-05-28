package Simulations.math;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.Fourier.FFT;

// testing the MoreMath class

public class TestFFT {

	public static void main(String[] args) {
		int N = (int) Math.pow(2, 16) ;
		FFT fourierTransform = new FFT(N) ;
		double ts = 10e-9 ;
		double fs = 1/ts ;
		int ns = N ;
		double[] yReal = new double[ns] ;
		double[] yImag = new double[ns] ;
		double[] x = new double[ns] ;
		double[] freq = new double[ns] ;
		for(int i=0; i<ns; i++){
			x[i] = i*ts ;
			freq[i] = i*fs ;
			yReal[i] = getFunction(i*ts) ;
			yImag[i] = 0 ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(MoreMath.linspace(1, N, 1.0), yReal);
		fig.RenderPlot();
		fig.run(true);

		fourierTransform.fft(yReal, yImag);

		MatlabChart fig1 = new MatlabChart() ;
		double[] amp = new double[yReal.length] ;
		for(int i=0; i<yReal.length; i++){
			amp[i] = new Complex(yReal[i], yImag[i]).absSquared() ;
		}
		fig1.plot(MoreMath.linspace(1, N, 1.0), amp);
		fig1.RenderPlot();
		fig1.run(true);


		// calculating inverse Fourier transform by the method of swapping real and imaginary parts
		fourierTransform.fft(yImag, yReal);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(x, MoreMath.Arrays.times(yReal, (double)1/ns), "g");
		fig2.plot(x, MoreMath.Arrays.times(yImag, (double)1/ns), "k");
		fig2.RenderPlot();
		fig2.run(true);

	}


	private static double getFunction(double t){
		double tau = 1e-6 ;
		double y = Math.exp(-(t-3*tau)/tau)*MoreMath.Functions.step(3*tau, t) ;
		return y ;
	}

}
