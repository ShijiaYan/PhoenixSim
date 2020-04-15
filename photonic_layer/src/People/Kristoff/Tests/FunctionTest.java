package People.Kristoff.Tests;

import People.Kristoff.Function;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class FunctionTest implements Experiment{
	public static void main(String[] args){
		String pacakgeString = "ch.epfl" ;
		String classString = "javancox.experiments.FunctionTest.Functiontest" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}
	Function fc;
	public FunctionTest(Function fc) {this.fc=fc;}
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		// TODO Auto-generated method stub
		DataPoint dp = new DataPoint();
		
		//go!
		dp.addProperty("x",fc.getx());
		dp.addResultProperty("x", fc.getx());
		dp.addResultProperty("x^2=",fc.getx2());
		
		man.addDataPoint(dp);
	}
	
}