package Simulations.thermalHeater.lumpedModel;

import PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel.LowPassFilter;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class LowPassFilterSim implements Experiment {

	double t_usec ;
	LowPassFilter lowpass ;
	
	public LowPassFilterSim(
			LowPassFilter lowpass,
			@ParamName(name="Time (usec)") double t_usec
			) {
		this.lowpass = lowpass ;
		this.t_usec = t_usec ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("time (usec)", t_usec);
		dp.addProperties(lowpass.getAllParameters());
		dp.addResultProperty("Impulse Response", lowpass.getImpulseResponse(t_usec));
		man.addDataPoint(dp);
	}

}
