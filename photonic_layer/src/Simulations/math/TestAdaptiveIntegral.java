package Simulations.math;

import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import flanagan.integration.IntegralFunction;

public class TestAdaptiveIntegral {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double x0 = 0, x1 = Math.PI/2 ;
		IntegralFunction func = x -> {
            double y = Math.cos(x) ;
            return y;
        };
		AdaptiveIntegral adint = new AdaptiveIntegral(func, x0, x1) ;
		System.out.println(adint.getIntegral());
	}

}
