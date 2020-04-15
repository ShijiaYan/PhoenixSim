package PhotonicElements.TimeDomain.ODEsolve.ODEs.SimpleCircuits;

import PhotonicElements.TimeDomain.CircuitSolver.Sources.VoltageSource.AbstractVoltageSource;
import PhotonicElements.TimeDomain.ODEsolve.ODE;
import ch.epfl.general_libraries.clazzes.ParamName;

public class RCcircuit extends ODE {

	public double R_kohm, C_muF ;
	AbstractVoltageSource Vs ;
	
	public RCcircuit(
			@ParamName(name="Voltage Source") AbstractVoltageSource Vs,
			@ParamName(name="R (kohm)") double R_kohm,
			@ParamName(name="C (micro Farad)") double C_muF
			) {
		super(1, 1);
		this.R_kohm = R_kohm ;
		this.C_muF = C_muF ;
		this.Vs = Vs ;
	}

	
	@Override
	public void getFunction(double t, double dy[], double ytmp[]) {
		double coeff = (R_kohm*1e3) * (C_muF*1e-6) ;
		dy[0] = Vs.getVoltage(t)/coeff - ytmp[0]/coeff ; 
	}
	
	@Override
	public void getError(double E[], double endY[]) {
		// no need to put something here
	}
	
	@Override
	public void setInitialConditions(double t0, double Y[]) {
		// here to set the initial conditions
		int n = getNumEqns() ;
		setOneX(0, t0);
		for(int i=0; i<n; i++){
			setOneY(0, i, Y[i]);
		}
	}
	
	
	
	
}