package Simulations.heater;

import PhotonicElements.Heater.Model.Photoconductance.HeaterPhotoConductance;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class HeaterPhotoConductanceModel implements Experiment {

	HeaterPhotoConductance heater ;
	
	public HeaterPhotoConductanceModel(
			HeaterPhotoConductance heater
			) {
		this.heater = heater ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Laser Power (mW)", heater.laser.Pin_mW);
		dp.addProperty("Laser wavelength (nm)", heater.laser.lambdaLaser_nm);
		dp.addProperty("Laser detuning (nm)", heater.laser.lambdaLaser_nm - heater.ring.resLambda_nm);
		dp.addProperty("Heater Voltage (V)", heater.V_volt);
		dp.addResultProperty("Heater Current (mA)", heater.getI_mA());
		dp.addResultProperty("Heater Current (mA)-Bistable1", heater.getI_mA_allValues()[0]);
		dp.addResultProperty("Heater Current (mA)-Bistable2", heater.getI_mA_allValues()[1]);
		dp.addResultProperty("Heater Current (mA)-Bistable3", heater.getI_mA_allValues()[2]);
		dp.addResultProperty("Delta I (mA)", heater.getI_mA()-heater.selfHeating.getCurrent_mA(heater.V_volt));
		dp.addResultProperty("Delta I (mA)-Bistable1", heater.getI_mA_allValues()[0]-heater.selfHeating.getCurrent_mA(heater.V_volt));
		dp.addResultProperty("Delta I (mA)-Bistable2", heater.getI_mA_allValues()[1]-heater.selfHeating.getCurrent_mA(heater.V_volt));
		dp.addResultProperty("Delta I (mA)-Bistable3", heater.getI_mA_allValues()[2]-heater.selfHeating.getCurrent_mA(heater.V_volt));
		dp.addResultProperty("Drop Transmission", heater.getDropTrans());
		dp.addResultProperty("Drop Transmission (dB)", MoreMath.Conversions.todB(heater.getDropTrans()));
		man.addDataPoint(dp);
	}

}
