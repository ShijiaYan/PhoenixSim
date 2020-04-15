package Simulations.inputSources;

import PhotonicElements.InputSources.LaserEfficiency.AbstractLaserEfficiency;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestLaserEfficiency implements Experiment {

	AbstractLaserEfficiency laserEff ;
	double currentmA ;
	double temperatureC ;
	double opticalPowermW ;
	
	public TestLaserEfficiency(
			@ParamName(name="Optical Power (mW)") double opticalPowermW,
			@ParamName(name="Temperature (C)") double temperatureC,
			@ParamName(name="Efficiency Model") AbstractLaserEfficiency laserEff
			) {
		this.opticalPowermW = opticalPowermW ;
		this.temperatureC = temperatureC ;
		this.laserEff = laserEff ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		currentmA = laserEff.getCurrentmAfromOpticalPowermW(opticalPowermW, temperatureC) ;
		dp.addProperty("Current (mA)", currentmA);
		dp.addProperty("Temperature (C)", temperatureC);
		dp.addResultProperty("WPE", laserEff.getEfficiencyFromCurrent(currentmA, temperatureC));
		dp.addResultProperty("Optical Power (mW)", laserEff.getOpticalPowermWfromCurrent(currentmA, temperatureC));
		dp.addResultProperty("Optical Power (dBm)", 10*Math.log10(laserEff.getOpticalPowermWfromCurrent(currentmA, temperatureC)));
		man.addDataPoint(dp);
	}

}
