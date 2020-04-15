package People.Qixiang.RingSwitch.Test;

import People.Qixiang.RingSwitch.Switch2x2Ring;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestRingSwitch2x2 implements Experiment {

	Switch2x2Ring sw ;
	
	public TestRingSwitch2x2(
			@ParamName(name="Switch") Switch2x2Ring sw
			){
		this.sw = sw ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", sw.getWavelength().getWavelengthNm());
		dp.addProperty("radius (um)", sw.getRadiusMicron());
		dp.addProperty("input kappa", sw.getInputCoupling().getKappa());
		dp.addProperty("output Kappa", sw.getOutputCoupling().getKappa());
		dp.addProperty("Delta Alpha (1/cm)", sw.getPlasmaEffect().getDalphaPerCm());
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(sw.getS21().absSquared()));
		dp.addResultProperty("|S31|^2 (dB)", MoreMath.Conversions.todB(sw.getS31().absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(sw.getS41().absSquared()));
		man.addDataPoint(dp);
	}

	
	
	
	
	
	
}
