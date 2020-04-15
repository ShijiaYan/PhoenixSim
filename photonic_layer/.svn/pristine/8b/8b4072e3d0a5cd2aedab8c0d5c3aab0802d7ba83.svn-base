package Simulations.pnJunction;

import PhotonicElements.PNJunction.PINdiode;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestPINdiode implements Experiment {

	PINdiode PIN ;
	
	public TestPINdiode(
			@ParamName(name="PIN diode") PINdiode PIN
			){
		this.PIN = PIN ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Voltage (V)", PIN.getVoltage_volt());
		dp.addProperty("Length (micron)", PIN.getLength_um());
		dp.addResultProperty("Current (mA)", PIN.getCurrent_mA());
		dp.addResultProperty("DN (1/cm^3)", PIN.getDN());
		dp.addResultProperty("Dalpha (1/cm)", PIN.getDalphaSi());
		dp.addResultProperty("DnSi", PIN.getDnSi());
		dp.addResultProperty("DnSi*L", PIN.getDnSi()*PIN.getLength_um());
		dp.addResultProperty("Dalpha*L", PIN.getDalphaSi()*PIN.getLength_um());
		man.addDataPoint(dp);
	}

}
