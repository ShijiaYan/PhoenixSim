package PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.examples;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Fitter;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Function;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.fitters.NonLinearSolver;

public class HeaterDCTest {

	public static void main(String[] args){

		double[] current_mA = {0, 3.117729, 6.234038, 9.313787, 12.3856, 15.42257, 18.41245, 21.34163, 24.19397, 26.99265, 29.71356, 32.35067, 34.90186, 37.35186, 39.71971, 41.99975, 44.17936, 46.26705, 48.25028, 50.153, 51.96568, 53.68989, 55.32937, 56.87549, 58.35173, 59.75289} ;
		double[] voltage_V = {0, 0.07996588, 0.1600363, 0.2395641, 0.3195978, 0.3997143, 0.4798623, 0.559867, 0.639495, 0.7196569, 0.7997466, 0.8798388, 0.9599171, 1.039656, 1.119706, 1.199914, 1.279935, 1.360039, 1.439679, 1.519732, 1.59981, 1.679864, 1.759964, 1.839556, 1.919616, 1.999734} ;
		double[][] V_volt = new double[voltage_V.length][1] ;
		for(int i=0; i<voltage_V.length; i++){
			V_volt[i][0] = voltage_V[i] ;
		}

		Function I = new Function(){
			@Override
			public double evaluate(double[] values, double[] parameters) {
				double Kv = parameters[0] ; // 1/V^2 units
				double Rlinear = parameters[1] ; // kOhm units
				double voltage = values[0] ; // only one variable
				double current = voltage/Rlinear *2/(1+Math.sqrt(1+Kv*voltage*voltage)) ; // mA units
				return current ;
			}
			@Override
			public int getNParameters() {
				return 2;
			}
			@Override
			public int getNInputs() {
				return 1;
			}
		} ;

		Fitter fit = new NonLinearSolver(I) ;
//		Fitter fit = new MarquardtFitter(I) ;
		fit.setData(V_volt, current_mA);
		fit.setParameters(new double[]{1, 0.1});
		fit.fitData();
		MatlabChart fig = new MatlabChart() ;
		fig.plot(current_mA, voltage_V, "b");
		double Kv = fit.getParameters()[0] ;
		System.out.println(Kv);
		double Rlinear = fit.getParameters()[1] ;
		System.out.println(Rlinear);
		double[] V_values = MoreMath.linspace(MoreMath.Arrays.FindMinimum.getValue(voltage_V), MoreMath.Arrays.FindMaximum.getValue(voltage_V), 1000) ;
		double[] I_values = new double[V_values.length] ;
		for(int i=0; i<V_values.length; i++){
			I_values[i] = V_values[i]/Rlinear * 2/(1+Math.sqrt(1+Kv*V_values[i]*V_values[i])) ;
		}
		fig.plot(I_values, V_values, "r");
		fig.RenderPlot();
		fig.xlabel("Current (mA)");
		fig.ylabel("Voltage (V)");
//		fig.setXAxis_to_Log();
		fig.run(true);



	}


}
