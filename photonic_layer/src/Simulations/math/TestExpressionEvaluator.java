package Simulations.math;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

public class TestExpressionEvaluator {

//	public static void main(String[] args){
//		String expression = "2+3^(-1/2)" ;
//		double result = new DoubleEvaluator().evaluate(expression) ;
//		System.out.println(result);
////		System.out.println(MoreMath.evaluate(expression));
//	}

	public static void main(String[] args){
		String expression = "sin(x)*cos(y)" ;
		StaticVariableSet<Double> variables = new StaticVariableSet<>() ;
		variables.set("x", Math.PI/2);
		variables.set("y", 0d);
		double result = new DoubleEvaluator().evaluate(expression, variables) ;
		System.out.println(result);
	}

}
