package Simulations.thermalHeater;

import PhotonicElements.Heater.Model.Structure.SelfHeating;
import PhotonicElements.Heater.Model.VoltageFunc.AbstractVoltage;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class HeaterVoltageSim implements Experiment {

	AbstractVoltage voltage ;
	SelfHeating selfHeating = new SelfHeating(4.5e-3, 0.6, 161) ;
	double t_usec ;
	
	public HeaterVoltageSim(
			@ParamName(name="Time (usec)") double t_sec,
			@ParamName(name="Self Heating Model") SelfHeating selfHeating,
			AbstractVoltage voltage
			) {
		this.voltage = voltage ;
		this.t_usec = t_sec ;
		this.selfHeating = selfHeating ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperties(voltage.getAllParameters());
		dp.addProperties(selfHeating.getAllParameters());
		dp.addProperty("time (usec)", t_usec);
		dp.addResultProperty("voltage (V)", voltage.getVoltage(t_usec));
		dp.addProperty("alpha_H", selfHeating.getAlphaH());
		dp.addResultProperty("Delta T_H", selfHeating.getDeltaT(voltage.getVoltage(t_usec)));
		dp.addResultProperty("I (mA)", selfHeating.getCurrent_mA(voltage.getVoltage(t_usec)));
		man.addDataPoint(dp);
	}

}
