package Simulations.thermalHeater.lumpedModel;

import PhotonicElements.Heater.Model.ImpulseResponseModel.LumpedModel.HeaterLumpedModel;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class HeaterLumpedModelSim implements Experiment {

	double t_usec ;
	HeaterLumpedModel heater ;
	
	public HeaterLumpedModelSim(
			HeaterLumpedModel heater,
			@ParamName(name="Time (usec)") double t_usec
			) {
		this.heater = heater ;
		this.t_usec = t_usec ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("time (usec)", t_usec);
		dp.addProperties(heater.getAllParameters());
		dp.addResultProperty("Impulse Response", heater.getImpulseResponse(t_usec));
		dp.addResultProperty("Impulse Response Closed-From" , heater.getImpulseResponseClosedForm(t_usec));
		dp.addResultProperty("Impulse Response derivative", heater.getImpulseResponseDerivative(t_usec));
		dp.addResultProperty("Impulse Response derivative closed-form" , heater.getImpulseResponseDerivativeClosedForm(t_usec));
		man.addDataPoint(dp);
	}

}
