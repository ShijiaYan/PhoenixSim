package PhotonicElements.BERT;

import org.apache.commons.math3.special.Erf;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Fitter;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Function;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.fitters.MarquardtFitter;

public class GaussianNoise {

	public double getQberFromBER(double ber){
		double qBER = Math.sqrt(2) * Erf.erfcInv(2*ber) ;
		return qBER ;
	}

	public double getBERFromQber(double qBER){
		double ber = 0.5 * Erf.erfc(qBER/Math.sqrt(2)) ;
		return ber ;
	}
	
	public double[] getPrecFittingCoeffs(double[] prec_dBm, double[] qMeasured){
		/**
		 * Prec_dBm = A * qBER + B
		 */
		// step1: finding the number of data points
		int numParams = 2 ;
		int M = qMeasured.length ;
		double[][] qValues = new double[M][1] ;
		for(int i=0; i<M; i++){
			qValues[i][0] = qMeasured[i] ;
		}
		// step2: setting up fitting polynomial of degree M by least square fitting
		Function Prec = new Function(){
			@Override
			public double evaluate(double[] values, double[] parameters) {
				double x = values[0]; // only one variable
				double y = 0 ;
				for(int i=0; i<numParams; i++){
					y += parameters[i]*Math.pow(x, i) ;
				}
				return y;
			}

			@Override
			public int getNParameters() {
				return numParams;
			}

			@Override
			public int getNInputs() {
				return 1;
			}
		} ;
		Fitter fit = new MarquardtFitter(Prec) ;
		fit.setData(qValues, prec_dBm);
		fit.setParameters(MoreMath.Arrays.setValue(0, numParams));
		fit.fitData();
		return fit.getParameters() ;
	}
	
	public double[] getQberFittingCoeffs(double[] prec_dBm, double[] qMeasured){
		/**
		 * qBER = A * Prec_dBm + B
		 */
		// step1: finding the number of data points
		int numParams = 2 ;
		int M = prec_dBm.length ;
		double[][] pRec_values = new double[M][1] ;
		for(int i=0; i<M; i++){
			pRec_values[i][0] = prec_dBm[i] ;
		}
		// step2: setting up fitting polynomial of degree M by least square fitting
		Function Prec = new Function(){
			@Override
			public double evaluate(double[] values, double[] parameters) {
				double x = values[0]; // only one variable
				double y = 0 ;
				for(int i=0; i<numParams; i++){
					y += parameters[i]*Math.pow(x, i) ;
				}
				return y;
			}

			@Override
			public int getNParameters() {
				return numParams;
			}

			@Override
			public int getNInputs() {
				return 1;
			}
		} ;
		Fitter fit = new MarquardtFitter(Prec) ;
		fit.setData(pRec_values, prec_dBm);
		fit.setParameters(MoreMath.Arrays.setValue(0, numParams));
		fit.fitData();
		return fit.getParameters() ;
	}

}
