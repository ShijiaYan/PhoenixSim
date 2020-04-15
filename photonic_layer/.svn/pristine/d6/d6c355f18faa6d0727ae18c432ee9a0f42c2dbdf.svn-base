package Simulations.timeDomain.circuits;

import PhotonicElements.TimeDomain.ODEsolve.ODESolver;
import PhotonicElements.TimeDomain.ODEsolve.ODEs.SimpleCircuits.RCcircuit;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestRCcircuit implements Experiment {

	double time_sec ;
	RCcircuit rc ;
	
	public TestRCcircuit(
			@ParamName(name="RC circuit") RCcircuit rc,
			@ParamName(name="Time (sec)") double time_sec
			){
		this.rc = rc ;
		this.time_sec = time_sec ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		rc.setInitialConditions(0, new double[] {0});
		ODESolver.rungeKutta4(rc, time_sec, time_sec/1e3) ;
		dp.addProperty("C (micro F)", rc.C_muF);
		for(int i=0; i<rc.getX().length; i++){
			DataPoint derived = dp.getDerivedDataPoint() ;
			derived.addProperty("array index", i);
			derived.addProperty("Time (sec)", rc.getX()[i]);
			derived.addResultProperty("Vc (volt)", rc.getY()[i][0]);
			man.addDataPoint(derived);
		}
		
		man.addDataPoint(dp);
	}

}
