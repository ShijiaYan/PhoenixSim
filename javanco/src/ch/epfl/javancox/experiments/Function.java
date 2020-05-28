package ch.epfl.javancox.experiments;

import ch.epfl.general_libraries.clazzes.ParamName;
public class Function {
	String input;
	double x;

	
public Function(@ParamName(name="kappa") double x)
	{
		
		this.x = x;
	}

	public String getLpi() 
	{
		this.input =Double.toString(Math.PI/2/x);	
		return input;
	}
	
	public String getX()
	{
		return Double.toString(x);
	}
}
