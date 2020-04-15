package NASA.Tests;

import NASA.Link.Transmitter;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.RadiusDependentBendLossModel;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestTransmitterBudget implements Experiment {

	double transBudgetdB ;
	
	public TestTransmitterBudget(
			@ParamName(name="Transmitter power budget (dB)") double transBudgetdB
			){
		this.transBudgetdB = transBudgetdB ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Transmitter Budget (dB)", transBudgetdB);
		double m = 1 ;
		Wavelength inputLambda = new Wavelength(1550) ;
		WgProperties wgProp = new WgProperties(10, 5, 0.8, new RadiusDependentBendLossModel()) ;
		Transmitter trans = new Transmitter(inputLambda, m, wgProp) ;
		while(transBudgetdB-trans.getTotalPenalty() > 0){
			m++ ;
			trans = new Transmitter(inputLambda, m, wgProp) ;
		}
		if(m==1){ m = 0 ;} 
		double totAggregation = 100 * m ;
		dp.addResultProperty("Max Aggregation (Tb/s)", totAggregation/1000);
		man.addDataPoint(dp);
	}

}
