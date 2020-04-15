package Simulations.math;

import org.apache.commons.math3.special.Erf;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;

public class TestErfc {
	public static void main(String[] args){
		double[] qBER = MoreMath.linspace(0, 10, 1000) ;
		double[] BER = new double[qBER.length] ;
		for(int i=0; i<qBER.length; i++){
			BER[i] = Math.log10(getBerFromQber(qBER[i])) ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(qBER, BER);
		fig.RenderPlot();
		fig.run(true);
//		System.out.println(getBerFromQber(8));
	}

	public static double getBerFromQber(double qBER){
		double ber = 0.5 * Erf.erfc(qBER/Math.sqrt(2)) ;
//		double ber = Erf.erfc(qBER/Math.sqrt(2))/2 ;
		return ber ;
	}

}
