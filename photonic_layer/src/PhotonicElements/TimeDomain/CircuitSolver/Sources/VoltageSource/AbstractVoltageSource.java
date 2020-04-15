package PhotonicElements.TimeDomain.CircuitSolver.Sources.VoltageSource;

import PhotonicElements.TimeDomain.CircuitSolver.CircuitElements.AbstractCircuitElement;

public abstract class AbstractVoltageSource extends AbstractCircuitElement {
	
	public abstract double getVoltage(double time_sec) ;
	
}