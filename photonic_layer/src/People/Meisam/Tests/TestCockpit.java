package People.Meisam.Tests;

import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestCockpit extends Application {

	public static void main(String[] args){
		// vm argument: javanco_home
//		String jargs = "-DJAVANCO_HOME=/Users/meisam/Desktop/Javanco_env/javanco" ;
//		ExperimentConfigurationCockpit.main(new String[]{"-p", "GDS"});
		ExperimentConfigurationCockpit co = new ExperimentConfigurationCockpit(new String[]{"-p", "GDS"}) ;
		co.show();
//		new ExperimentConfigurationCockpit().main(null); ;
//		co.main(new String[]{"-p", "GDS"});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
//		ExperimentConfigurationCockpit co = new ExperimentConfigurationCockpit() ;
//		co.main(new String[]{"-p", "GDS"});
	}


}
