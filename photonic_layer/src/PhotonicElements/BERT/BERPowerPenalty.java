package PhotonicElements.BERT;

import org.apache.commons.math3.special.Erf;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Fitter;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Function;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.fitters.MarquardtFitter;
import flanagan.interpolation.LinearInterpolation;


public class BERPowerPenalty {


    double[] prec1_dBm = null , logBERCase1 = null ;
    double[] prec1Fitted_dBm = null , logBERCase1Fitted = null ;
    double[] prec2_dBm = null , logBERCase2 = null ;
    double[] prec2Fitted_dBm = null , logBERCase2Fitted = null ;

    LinearInterpolation interpolateCase1 = null, interpolateCase2 = null ;

    public BERPowerPenalty(){

    }

    public void setCase1DataSet(double[] prec1_dBm, double[] logBERCase1){
    	this.prec1_dBm = prec1_dBm ;
    	this.logBERCase1 = logBERCase1 ;
    }

    public void setCase2DataSet(double[] prec2_dBm, double[] logBERCase2){
    	this.prec2_dBm = prec2_dBm ;
    	this.logBERCase2 = logBERCase2 ;
    }

    public void calculate(){
    	double qBER_min = getQberFromBER(Math.pow(10, -1)) ;
    	double qBER_max = getQberFromBER(Math.pow(10, -13)) ;
    	double[] qBER_dense = MoreMath.linspace(qBER_min, qBER_max, 1000) ;

    	// step1: fit Gaussian BER to case 1
    	int M1 = prec1_dBm.length ;
    	double[] berMeasuredCase1 = new double[M1] ;
    	double[] qMeasuredCase1 = new double[M1] ;
    	for(int i=0; i<M1; i++){
    		berMeasuredCase1[i] = Math.pow(10, logBERCase1[i]) ;
    		qMeasuredCase1[i] = getQberFromBER(berMeasuredCase1[i]) ;
    	}
    	double[] fittingCoeffsCase1 = getPrecFittingCoeffs(prec1_dBm, qMeasuredCase1) ;
    	double[] prec1_dense_dBm = new double[qBER_dense.length] ;
    	double[] BER1_dense = new double[qBER_dense.length] ;
    	double[] logBER1_dense = new double[qBER_dense.length] ;
    	for(int i=0; i<qBER_dense.length; i++){
    		for(int j=0; j<fittingCoeffsCase1.length; j++){
    			prec1_dense_dBm[i] += fittingCoeffsCase1[j] * Math.pow(qBER_dense[i], j) ;
    		}
    		BER1_dense[i] = getBerFromQber(qBER_dense[i]) ;
    		logBER1_dense[i] = Math.log10(BER1_dense[i]) ;
    	}
    	prec1Fitted_dBm = prec1_dense_dBm ;
    	logBERCase1Fitted = logBER1_dense ;

    	// step2: create interpolator for fitted case 1
    	interpolateCase1 = new LinearInterpolation(logBER1_dense, prec1_dense_dBm) ;

    	// step3: fit Gaussian BER to case 2
    	int M2 = prec2_dBm.length ;
    	double[] berMeasuredCase2 = new double[M2] ;
    	double[] qMeasuredCase2 = new double[M2] ;
    	for(int i=0; i<M2; i++){
    		berMeasuredCase2[i] = Math.pow(10, logBERCase2[i]) ;
    		qMeasuredCase2[i] = getQberFromBER(berMeasuredCase2[i]) ;
    	}
    	double[] fittingCoeffsCase2 = getPrecFittingCoeffs(prec2_dBm, qMeasuredCase2) ;
    	double[] prec2_dense_dBm = new double[qBER_dense.length] ;
    	double[] BER2_dense = new double[qBER_dense.length] ;
    	double[] logBER2_dense = new double[qBER_dense.length] ;
    	for(int i=0; i<qBER_dense.length; i++){
    		for(int j=0; j<fittingCoeffsCase2.length; j++){
    			prec2_dense_dBm[i] += fittingCoeffsCase2[j] * Math.pow(qBER_dense[i], j) ;
    		}
    		BER2_dense[i] = getBerFromQber(qBER_dense[i]) ;
    		logBER2_dense[i] = Math.log10(BER2_dense[i]) ;
    	}
    	prec2Fitted_dBm = prec2_dense_dBm ;
    	logBERCase2Fitted = logBER2_dense ;

    	// step4: create interpolator for fitted case 2
    	interpolateCase2 = new LinearInterpolation(logBER2_dense, prec2_dense_dBm) ;
    }

    public double getPowerPenaltyAt(double logBER){
    	double prec_case1_dBm_interpolated = interpolateCase1.interpolate(logBER) ;
    	double prec_case2_dBm_interpolated = interpolateCase2.interpolate(logBER) ;
    	double pp_atLogBER = Math.abs(prec_case2_dBm_interpolated-prec_case1_dBm_interpolated) ;
    	return pp_atLogBER ;
    }

	public double getQberFromBER(double ber){
		double qBER = Math.sqrt(2) * Erf.erfcInv(2*ber) ;
		return qBER ;
	}

	public double getBerFromQber(double qBER){
		double ber = 0.5 * Erf.erfc(qBER/Math.sqrt(2)) ;
		return ber ;
	}

	public double[] getPrecFittingCoeffs(double[] prec_dBm, double[] qMeasured){
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


}
