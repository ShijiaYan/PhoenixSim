package edu.columbia.lrl.experiments.routing_in_switches;

import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;
import edu.columbia.lrl.switch_arch.BenesSwitchGenerator;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.general_libraries.path.ShortestPathRelativePathSet;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class SwitchRoutingEnum implements Experiment {
	
	private AbstractSwitchArchitectureGenerator gen;

	public SwitchRoutingEnum(AbstractSwitchArchitectureGenerator gen) {
		this.gen = gen;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		AbstractGraphHandler agh = gen.generate();
		
		double[][] d = agh.getEditedLayer().getDirectedIncidenceMatrixDouble();
		
		ShortestPathRelativePathSet pset = new ShortestPathRelativePathSet(d, 1d);
		
		for (Integer i : gen.getInputNodesIndexes()) {
			pset.enumerateAndStoreFrom(i);
		}
		
		DataPoint dp = new DataPoint();
		
		dp.addProperties(gen.getAllParameters());
		

		
		Execution e = new Execution();		
		
		for (Integer in : gen.getInputNodesIndexes()) {
			for (Integer out : gen.getOutputNodesIndexes()) {
				DataPoint dps = dp.getDerivedDataPoint();
				dps.addProperty("src-dest", in + "-" + out);
				dps.addResultProperty("paths", pset.getPaths(in, out).size());
				e.addDataPoint(dps);
			}
		}
		
		DataPoint glo = dp.getDerivedDataPoint();

		glo.addResultProperty("Number of paths", pset.size());
		
		e.addDataPoint(glo);
		
		man.addExecution(e);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SmartDataPointCollector db = new SmartDataPointCollector();
		new SwitchRoutingEnum(new BenesSwitchGenerator(32)).run(db, null);
		
		DefaultResultDisplayingGUI.displayDefault(db);

	}

}
