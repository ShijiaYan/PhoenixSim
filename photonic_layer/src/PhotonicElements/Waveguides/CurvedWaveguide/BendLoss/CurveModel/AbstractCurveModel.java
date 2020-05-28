package PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.CurveModel;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import java.util.HashMap;


import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import flanagan.integration.IntegralFunction;

public abstract class AbstractCurveModel {

	public abstract double getX(double t) ;
	public abstract double getXPrime(double t) ;
	public abstract double getXDoublePrime(double t) ;

	public abstract double getY(double t) ;
	public abstract double getYPrime(double t) ;
	public abstract double getYDoublePrime(double t) ;

	public abstract String getName() ;

	public abstract HashMap<String, String> getAllParameters() ;

	public double getDS(double t) {
		return Math.sqrt(getXPrime(t)*getXPrime(t)+getYPrime(t)*getYPrime(t));
	}

	public double getLength(double tStart, double tEnd) {
		IntegralFunction func = t -> getDS(t);
		AdaptiveIntegral integral = new AdaptiveIntegral(func, tStart, tEnd) ;
		return integral.getIntegral()*1e-4; // length in cm instead of micron
	}

	public double getRadiusOfCurvature(double t){
		double num = pow(getXPrime(t)*getXPrime(t)+getYPrime(t)*getYPrime(t), 1.5) ;
		double denom = abs(getXPrime(t)*getYDoublePrime(t)-getYPrime(t)*getXDoublePrime(t)) ;
		return num/denom;
	}

}
