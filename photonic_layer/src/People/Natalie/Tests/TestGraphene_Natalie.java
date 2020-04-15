package People.Natalie.Tests;

import People.Natalie.Graphene_Natalie;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestGraphene_Natalie implements Experiment{
	public static void main(String[] args){
		String pacakgeString = "People.Natalie.Tests" ;
		String classString = "People.Natalie.Tests.TestGraphene_Natalie" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}
	
	Graphene_Natalie gr;
	
	public TestGraphene_Natalie(Graphene_Natalie gr){
		this.gr = gr;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint();
		
		//plotting Fermi energy level
		dp.addProperty("Voltage (V)", gr.getVin());
		dp.addResultProperty("Voltage", gr.getVin());
		dp.addResultProperty("Fermi level of the graphene: ", gr.getFermiLevel());
		
		//plotting conductivity of graphene
		
		//plotting sqrt(epsilonReal)
		man.addDataPoint(dp);
	}

}
