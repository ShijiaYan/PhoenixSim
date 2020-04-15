package PhotonicElements.TimeDomain.CircuitSolver.Sources.VoltageSource;

import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Nodes.Node;
import ch.epfl.general_libraries.clazzes.ParamName;

public class VoltagePulsed extends AbstractVoltageSource {

	Node node_plus, node_minus ;
	double voltage_V, startTime_sec, endTime_sec ;
	
	public VoltagePulsed(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Voltage Amplitude (V)") double voltage_V,
			@ParamName(name="Start Time (sec)", default_="0") double startTime_sec,
			@ParamName(name="End Time (sec)", default_="1") double endTime_sec
			){
		this.objectName = objectName ;
		this.voltage_V = voltage_V ;
		this.startTime_sec = startTime_sec ;
		this.endTime_sec = endTime_sec ;
		node_plus = new Node("node_plus") ;
		node_minus = new Node("node_minus") ;
		
		saveProperties() ;
	}
	
	public void connectNodes(Node[] nodes){
		this.node_plus = nodes[0] ;
		this.node_minus = nodes[1] ;
	}
	
	@Override
	protected void saveProperties() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public double getVoltage(double time_sec) {
		double v ;
		if(time_sec < startTime_sec || time_sec > endTime_sec){
			v = 0 ;
		}
		else{
			v = voltage_V ;
		}
		return v ;
	}

	@Override
	public Node[] getAllNodes() {
		return null;
	}



	
	
	

}
