package test.javancox.inputs;

import org.junit.Test;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javancox.inputs.compounds.FromGeneratorGraphExperimentInput;
import ch.epfl.javancox.inputs.topology.GridRandomGenerator;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import ch.epfl.javancox.topology_analysis.TopologyAnalysis;
import ch.epfl.javancox.topology_analysis.node_metrics.DegreeComputer;




public class TestGridRandomGenerator {
	
	public static void main(String[] args) throws Exception {
		TestGridRandomGenerator test = new TestGridRandomGenerator();
		
		test.testGridLayout();
	}
	
	public static final boolean display = true;	
	
	@Test
	public void testGridLayout() throws Exception {

		SmartDataPointCollector sebDb = new SmartDataPointCollector();

		int[] nodes = GridRandomGenerator.getIntArray(20,21,2);
		int[] seeds =  GridRandomGenerator.getIntArray(1,5);
		float[] degs = GridRandomGenerator.getFloatArray(1.6f, 5f, 0.2f);
		float[] fracs = GridRandomGenerator.getFloatArray(0.0f, 0.4f, 0.2f);

		DegreeComputer comp = new DegreeComputer();




		for (int seed : seeds) {

			for (int node : nodes) {
				for (float f : degs) {
					for (float frac : fracs) {
						GridRandomGenerator gen = new GridRandomGenerator(node, f, frac, PRNStream.getDefaultStream(seed));

						TopologyAnalysis analysis = new TopologyAnalysis(
								new FromGeneratorGraphExperimentInput(gen),
								comp
						);

						//		analysis.getAgh().saveGraphImage("test"+id++, 600, 600);

						analysis.run(sebDb, null);

					}
				}
			}
		}

		if (display)
		DefaultResultDisplayingGUI.displayDefault(sebDb);

	}


		
	
	
}
