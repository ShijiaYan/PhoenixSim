package PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Resistor;

import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.AbstractCircuitElement;
import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Nodes.Node;
import ch.epfl.general_libraries.clazzes.ParamName;

public class IdealResistor extends AbstractCircuitElement {

	/*
	 * takes two nodes and calculates the voltage and current
	 */
	
	double R_kohm ;
	Node node_plus, node_minus ;
	
	public IdealResistor(
			@ParamName(name="Element Name") String objectName,
			@ParamName(name="Resistivity (kOhm)") double R_kohm,
			@ParamName(name="Node Plus (+)") Node node_plus,
			@ParamName(name="Node Minus (-)") Node node_minus
			){
		this.objectName = objectName ;
		this.R_kohm = R_kohm ;
		this.node_plus = node_plus ;
		this.node_minus = node_minus ;
		
		saveProperties() ;
	}
	
	@Override
	protected void saveProperties(){
		
	}
	
	// this is not necessary, but it's a good idea to have it.
	@Override
	public void connectNodes(Node[] nodes){
		this.node_plus = nodes[0] ;
		this.node_minus = nodes[1] ;
	}

	public double getVoltage_volt(double time_sec){
		double V_volt = node_plus.getVoltage(time_sec) - node_minus.getVoltage(time_sec) ;
		return V_volt ;
	}
	
	public double getCurrent_mA(double time_sec){
		double I_mA = getVoltage_volt(time_sec)/R_kohm ;
		return I_mA ;
	}
	
	public double getCurrent_node_plus_mA(double time_sec){
		return getCurrent_mA(time_sec) ;
	}
	
	public double getCurrent_node_minus_mA(double time_sec){
		return -1*getCurrent_mA(time_sec) ;
	}
	
	@Override
	public Node[] getAllNodes(){
		return new Node[] {node_plus, node_minus} ;
	}
	
}