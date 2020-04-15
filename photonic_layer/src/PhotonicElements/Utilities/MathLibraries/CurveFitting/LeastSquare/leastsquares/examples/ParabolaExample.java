package PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.examples;

import java.util.Arrays;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Fitter;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.Function;
import PhotonicElements.Utilities.MathLibraries.CurveFitting.LeastSquare.leastsquares.fitters.LinearFitter;

public class ParabolaExample {

    public static void main(String[] args){
        double[][] xs = {
                {0}, {1}, {2}, {3}, {4}, {5}
        };
        double[] zs = {1.0, 0.9, 1.0, 1.3, 1.8, 2.5};

        Function fun = new Function(){
            @Override
            public double evaluate(double[] values, double[] parameters) {
                double A = parameters[0];
                double B = parameters[1];
                double C = parameters[2];
                double x = values[0];
                return A*x*x + B*x + C;
            }
            @Override
            public int getNParameters() {
                return 3;
            }

            @Override
            public int getNInputs() {
                return 1;
            }
        };

        Fitter fit = new LinearFitter(fun);
//        Fitter fit = new MarquardtFitter(fun);
//        Fitter fit = new NonLinearSolver(fun);
        fit.setData(xs, zs);
        fit.setParameters(new double[]{0,0,0});

        fit.fitData();

        System.out.println(Arrays.toString(fit.getParameters()));
        MatlabChart fig = new MatlabChart() ;
        double[] x = new double[]{xs[0][0], xs[1][0], xs[2][0], xs[3][0], xs[4][0], xs[5][0]} ;
        fig.plot(x, zs);
        double xmin = MoreMath.Arrays.FindMinimum.getValue(x) ;
        double xmax = MoreMath.Arrays.FindMaximum.getValue(x) ;
        double[] xx = MoreMath.linspace(xmin, xmax, 1000) ;
        double[] y = {} ;
        for(int i=0; i<xx.length; i++){
        	y = MoreMath.Arrays.append(y, fun.evaluate(new double[]{xx[i]}, fit.getParameters())) ;
        }
        fig.plot(xx, y, "r");
        fig.RenderPlot();
        fig.run(true);
        MoreMath.Arrays.show(fit.getParameters());

    }

}
