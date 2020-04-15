package PhotonicElements.Utilities.Functions.Function;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.integration.IntegralFunction;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ExplicitFunction {
	
	Expression func ;
	String funcExpression ;
	String var ;
	
	public ExplicitFunction(){
		this.funcExpression = "" ;
		this.var = "" ;
	}
	
	public ExplicitFunction(
			String funcExpression,
			String var
			){
		this.funcExpression = funcExpression ;
		this.var = var ;
		build() ;
	}
	
	
	// setting parameters and building
	public void setVariableName(String var){
		this.var = var ;
	}
	
	public void setFuncExpression(String funcExpression){
		this.funcExpression = funcExpression ;
	}
	
	public void build(){
		func = new ExpressionBuilder(funcExpression).variable(var).build() ;
	}
	
	// getters for variable 
	public String getVariableName(){
		return var ;
	}
	
	public String getExpression(){
		return funcExpression ;
	}
	
	public Expression getFunction(){
		return func ;
	}
	
	// evaluating function
	public double getFuncAt(double x){
		return func.setVariable(var, x).evaluate() ;
	}
	
	public double[] getFuncAt(double[] x){
		int M = x.length ;
		double[] y = new double[M] ;
		for(int i=0; i<M; i++){
			y[i] = getFuncAt(x[i]) ;
		}
		return y ;
	}
	
	public void plotFunction(double[] x, boolean systemExit){
		int M = x.length ;
		int N = 100*M ; // a very dense plot
		double x_min = MoreMath.Arrays.FindMinimum.getValue(x) ;
		double x_max = MoreMath.Arrays.FindMaximum.getValue(x) ;
		double[] x_dense = MoreMath.linspace(x_min, x_max, N) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x_dense, getFuncAt(x_dense));
		fig.RenderPlot();
		fig.run(systemExit);
	}
	
	public void plotFunction(double x_min, double x_max, boolean systemExit){
		int N = 1000 ; // a very dense plot
		double[] x_dense = MoreMath.linspace(x_min, x_max, N) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x_dense, getFuncAt(x_dense));
		fig.RenderPlot();
		fig.run(systemExit);
	}
	
	public void plotFunction(double x_min, double x_max, int numPoints, boolean systemExit){
		int N = numPoints ; // a very dense plot
		double[] x_dense = MoreMath.linspace(x_min, x_max, N) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x_dense, getFuncAt(x_dense));
		fig.RenderPlot();
		fig.run(systemExit);
	}
	
	public double integrate(double x_from, double x_to){
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double t) {
				return getFuncAt(t);
			}
		} ;
		AdaptiveIntegral integral = new AdaptiveIntegral(func, x_from, x_to) ;
		return integral.getIntegral() ;
	}
	
	public double[] integrate(double x_from, double[] x_to){
		int M = x_to.length ;
		double[] result = new double[M] ;
		for(int i=0; i<M; i++){
			result[i] = integrate(x_from, x_to[i]) ;
		}
		return result ;
	}
	

}
