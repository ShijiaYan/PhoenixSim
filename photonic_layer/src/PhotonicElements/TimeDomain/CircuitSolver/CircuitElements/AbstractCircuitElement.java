package PhotonicElements.TimeDomain.CircuitSolver.CircuitElements;

import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.Nodes.Node;

public abstract class AbstractCircuitElement extends DataBase {

	public String objectName ;
	
	public abstract void connectNodes(Node[] nodes) ; // this is for connecting the circuit element to the nodes of the circuit
	public abstract Node[] getAllNodes() ;
	protected abstract void saveProperties() ;
}
