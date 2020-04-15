package People.Kristoff.copy;

import ch.epfl.general_libraries.clazzes.ParamName;
public class Function {
	double input;
	double x;
public Function(
		
		
		@ParamName(name="x") double x
		
		
		){

	this.input = x*x;
	
	
		
		
	}
	public double getx2() {
		return this.input;
	}
	public double getx() { return this.x;}
}