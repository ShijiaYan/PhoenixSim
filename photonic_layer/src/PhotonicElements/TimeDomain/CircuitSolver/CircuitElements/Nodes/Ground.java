package PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Nodes;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Ground extends Node {

	/*
	 * Node contains the info of voltage of each node as a function of time
	 * Each circuit element takes in two or more nodes and calculates the voltage and currents based on the state of the nodes
	 */
	
	
	public Ground(
			@ParamName(name="Node Name") String nodeName
			) {
		super(nodeName);
		// TODO Auto-generated constructor stub
	}

	double voltage_volt = 0 ;
	
	@Override
	public double getVoltage(double time_sec){
		return voltage_volt ;
	}


}
