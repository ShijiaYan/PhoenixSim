package Simulations.gds;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class TestEquationParser {

	public static void main(String[] args){
//		String eq = "2*t + t^2 -2*pi" ;
//		Expression e = new ExpressionBuilder(eq)
//						.variable("t")
//						.build()
//						.setVariable("t", 0) ;
//		System.out.println(e.evaluate());
		
		String eq = "2e3/2 -2*pi" ;
		Expression e = new ExpressionBuilder(eq)
						.build() ;
		System.out.println(e.evaluate());
	}
	
}
