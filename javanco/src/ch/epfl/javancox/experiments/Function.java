package ch.epfl.javancox.experiments;

import ch.epfl.general_libraries.clazzes.ParamName;
import java.util.HashMap;
public class Function {
	String input;
	double x;
	HashMap<String, String> output = new HashMap<String,String>();
	
public Function(@ParamName(name="kappa") double x)
	{
		
		this.x = x;
	}

	public String getLpi() 
	{
		this.input =Double.toString(Math.PI/2/x);	
		return input;
	}
	
	public String getx() 
	{
		return Double.toString(x);
	}
	/*
	public HashMap<String,String> getMap() 
	{
		output.put(getx(), getLpi());
		return output;
	}
	*/
}
