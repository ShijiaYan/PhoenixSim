package PhotonicElements.Heater;

import ch.epfl.general_libraries.clazzes.ParamName;

public class SimpleHeater {

	double phaseShiftRadian ;
	double phaseShiftDegree ;
	double changeOfLossdBperCm ;
	
	public SimpleHeater(
			@ParamName(name="Heater Phase Shift (degree)") double phaseShiftDegree
			){
		this.phaseShiftDegree = phaseShiftDegree ;
		this.phaseShiftRadian = phaseShiftDegree * Math.PI/180 ;
		this.changeOfLossdBperCm = 0 ;
	}
	
	
	public double getPhaseShiftRadian(){
		return phaseShiftRadian ;
	}
	
	public double getPhaseShiftDegree(){
		return phaseShiftDegree ;
	}
	
	public double getChangeOfLossdBperCm(){
		return changeOfLossdBperCm ;
	}
	
}
