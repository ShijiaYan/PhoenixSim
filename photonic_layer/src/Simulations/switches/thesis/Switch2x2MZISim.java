package Simulations.switches.thesis;

import PhotonicElements.Switches.Switch2x2.Switch2x2MZIDistributedCoupler;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class Switch2x2MZISim implements Experiment {

	Switch2x2MZIDistributedCoupler sw ;

	public Switch2x2MZISim(
			Switch2x2MZIDistributedCoupler sw
			) {
		this.sw = sw ;
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.switches.thesis.Switch2x2MZISim" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", sw.inputLambda.getWavelengthNm());
		dp.addProperty("alpha (1/cm)", sw.plasmaEffect.getDalphaPerCm());
		dp.addProperty("length of arm (um)", sw.armLengthMicron);
		dp.addProperty("coupler length (um)", sw.couplerLengthMicron);
		dp.addProperty("coupler gap (nm)", sw.gapNm);
		dp.addProperty("State", sw.getStateOfSwitch());
		dp.addResultProperty("coupler thru", sw.DC1.S21.absSquared());
		dp.addResultProperty("coupler cross", sw.DC1.S31.absSquared());
		dp.addResultProperty("Thru (dB)", MoreMath.Conversions.todB(sw.S21.absSquared()));
		dp.addResultProperty("Cross (dB)", MoreMath.Conversions.todB(sw.S31.absSquared()));

		man.addDataPoint(dp);
	}

}
