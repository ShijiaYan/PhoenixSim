package PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Nodes;

import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.DataBase;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Node extends DataBase {

	/*
	 * Node contains the info of voltage of each node as a function of time
	 * Each circuit element takes in two or more nodes and calculates the voltage and currents based on the state of the nodes
	 */
	
	String nodeName ;
	double voltage_volt ;	
	
	public Node(
			@ParamName(name="Node Name") String nodeName
			){
		this.nodeName = nodeName ;
	}
	
	public void setVoltage(double voltage_volt){
		this.voltage_volt = voltage_volt ;
	}
	
	public double getVoltage(double time_sec){
		return voltage_volt ;
	}
	

}