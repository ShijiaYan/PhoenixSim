package ch.epfl.javancox.experiments.builder;

import java.io.File;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

public class SubExperimentCockpitExample {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		
		try {
			String pre = null;
			String log4jFile = null;	
			String defaultFile = null;
			if (args.length > 0) {
				if (args[0].equals("-help") || args[0].equals("help") || args[0].equals("-h") || args[0].equals("usage")) {
					printUsage();
				}
				for (int i = 0 ; i < args.length ; i++) {
					if (args[i].equals("-p")) {
						pre = args[i+1];
					}
					if (args[i].equals("-l")) {
						log4jFile = args[i+1];
					}
					if (args[i].equals("-default")) {
						defaultFile = args[i+1];
					}
				}
			}
			if (log4jFile != null) {
				Logger.initLogger(new File(log4jFile));
			}
			ExperimentConfigurationCockpit co;
			if (pre != null) {
				co = new ExperimentConfigurationCockpit(ExampleSubExperiment.class, pre.split(";"));
			} else {
				co = new ExperimentConfigurationCockpit(ExampleSubExperiment.class);
			}
			if (defaultFile != null) {
				co.show(defaultFile);
			} else {
				co.show();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage : [-p <prefixes, ;-separated>][-l <log4j config file>]");
		System.exit(0);
	}

	
	public static class Example implements ExampleSubExperiment {
		
		private int i;
		
		public Example(int i) {
			this.i = i;
		}

		@Override
		public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
				throws WrongExperimentException {
			System.out.println(i);
			
		}
		
	}

}

interface ExampleSubExperiment extends Experiment {}

