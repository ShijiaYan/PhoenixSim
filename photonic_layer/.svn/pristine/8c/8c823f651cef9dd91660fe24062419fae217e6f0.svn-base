package Simulations.dummy;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestCarrierInjection implements Experiment {

	double current ;
	double R ;
	double Vbi ;
	double n ;
	double Vthermal ;
	double Is ;
	
	public TestCarrierInjection(
			@ParamName(name="R", default_="0.25") double R ,
			@ParamName(name="Vbi", default_="0.7") double Vbi ,
			@ParamName(name="n", default_="0.62") double n ,
			@ParamName(name="Vthermal", default_="0.026") double Vthermal ,
			@ParamName(name="Is", default_="9e-5") double Is,
			@ParamName(name="I")double current
			) {
		this.current = current ;
		this.R = R ;
		this.Vbi = Vbi ;
		this.n = n ;
		this.Vthermal = Vthermal ;
		this.Is = Is ;
		}
	
	private double getVoltageFromCurrent(double ImA){
//		double R = 0.25 ; // in Kilo Ohms
//		double Vbi = 0.7 ; // built-in diode voltage
//		double n = 0.62 ; // non-ideality factor of the diode
//		double Vthermal = 0.026 ; // Thermal voltage = KT/q
//		double Is = 9e-5 ; // 90 nano amperes = 9e-5 mA
		
		double V = Vbi + R * ImA + 1/n * Vthermal * Math.log(ImA/Is + 1) ;
		if (Double.isNaN(V)) {
			throw new WrongExperimentException();
		}
		return V ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("R", R);
		dp.addProperty("Voltage (V)", getVoltageFromCurrent(current));
		dp.addResultProperty("current (mA)", current);
		man.addDataPoint(dp);
		
	}
	
	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.dummy.TestCarrierInjection" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
