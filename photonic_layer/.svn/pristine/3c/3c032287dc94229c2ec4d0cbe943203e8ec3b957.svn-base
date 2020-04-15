package NASA.Tests;

import NASA.ErrorCoding.AbstractCoding;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestCodingGain implements Experiment {

	double logBER ;
	AbstractCoding coding ;
	
	public TestCodingGain(
			@ParamName(name="Choose Coding") AbstractCoding coding,
			@ParamName(name="LogBER") double logBER
			) {
		this.coding = coding ;
		this.logBER = logBER ; 
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		double BER = Math.pow(10, logBER) ;
		dp.addProperty("OSNR uncoded (dB)", coding.getUncodedOSNR(BER));
		dp.addProperty("OSNR coded (dB)", coding.getCodedOSNR(BER));
		dp.addResultProperty("logBER", logBER);
		dp.addProperty("logBER", logBER);
		dp.addResultProperty("OSNR uncoded (dB)", coding.getUncodedOSNR(BER));
		dp.addResultProperty("OSNR coded (dB)", coding.getCodedOSNR(BER));
		dp.addProperty("NCG", coding.getCodingGain(BER));
		dp.addResultProperty("NCG", coding.getCodingGain(BER));
		dp.addProperty("Coding Name", coding.getCodingName());
		man.addDataPoint(dp);
	}

}
