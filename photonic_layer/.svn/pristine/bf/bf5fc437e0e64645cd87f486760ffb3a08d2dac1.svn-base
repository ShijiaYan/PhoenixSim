package People.Qixiang.Tests;

import People.Qixiang.Benes.MZI2x2.Switch2x2MZI_qixiang;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestSwitch2x2 implements Experiment {
	
	Switch2x2MZI_qixiang mziSwitch ;

	public TestSwitch2x2(
			@ParamName(name="MZI 2x2 Switch") Switch2x2MZI_qixiang mziSwitch
			){
		this.mziSwitch = mziSwitch ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("input cross coupling", Math.pow(mziSwitch.getInputCoupler().getKappa(), 2) );
		dp.addProperty("output cross coupling", Math.pow(mziSwitch.getOutputCoupler().getKappa(), 2) );
		dp.addProperty("Dalpha (1/cm)", mziSwitch.getPlasmaEffect().getDalphaPerCm());
		dp.addResultProperty("|S11|^2 (dB) - Thru", MoreMath.Conversions.todB(mziSwitch.getS21().absSquared()) );
		dp.addResultProperty("|S21|^2 (dB) - Cross", MoreMath.Conversions.todB(mziSwitch.getS31().absSquared()) );
		dp.addResultProperty("|S12|^2 (dB) - Thru", MoreMath.Conversions.todB(mziSwitch.getS24().absSquared()) );
		dp.addResultProperty("|S22|^2 (dB) - Cross", MoreMath.Conversions.todB(mziSwitch.getS34().absSquared()) );
		man.addDataPoint(dp);
	}

}
