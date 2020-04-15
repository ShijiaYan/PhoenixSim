package ch.epfl.javancox.experiments;

import ch.epfl.javancox.experiments.Function;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class FunctionTest implements Experiment{
	public static void main(String[] args){
		String pacakgeString = "ch.epfl.javancox.experiments" ;
		String classString = "ch.epfl.javancox.experiments.FunctionTest" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}
	Function fc;
	public FunctionTest(Function fc) {this.fc=fc;}
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint();
		
		
		//go!
		dp.addProperty("kappa",fc.getx());
		dp.addResultProperty("Lpi = "+fc.getLpi(),fc.getLpi());
		man.addDataPoint(dp);
		
	}
	
}