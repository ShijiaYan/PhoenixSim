package test.javancox.inputs;

import java.util.Arrays;

import org.junit.Test;

import ch.epfl.javancox.inputs.compounds.AbstractGraphExperimentInput;
import ch.epfl.javancox.inputs.compounds.FromGeneratorGraphExperimentInput;
import ch.epfl.javancox.inputs.topology.ReyniErdosGenerator;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import ch.epfl.javancox.topology_analysis.AbstractTopologyAnalyser;
import ch.epfl.javancox.topology_analysis.CombinedTopologyAnalyser;
import ch.epfl.javancox.topology_analysis.TopologyAnalysis;
import ch.epfl.javancox.topology_analysis.network_metrics.ComponentNumberComputer;
import ch.epfl.javancox.topology_analysis.network_metrics.GiantComponentSizeComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.PathDistanceComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.TopologicalDistanceComputer;


public class TestTopologyAnalysis {
	
	public static void main(String[] args) throws Exception {

		TestTopologyAnalysis test = new TestTopologyAnalysis();
		
		test.testTopologyAnalysis();
	}
	
	@Test
	public void testTopologyAnalysis() throws Exception {



		SmartDataPointCollector db = new SmartDataPointCollector();
		int[] nodes = new int[]{15,20,};
		int[] seeds = new int[]{1,2};
		float[] linkProbability = new float[]{0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 0.9f, 1.1f, 1.3f, 1.5f, 1.7f, 1.9f, 2.1f, 2.3f, 2.5f, 2.75f, 3.0f, 3.25f, 3.5f, 3.75f, 4f};
		
		//Class[] classes = new Class[]{ReyniErdosGenerator.class/*CirclesPlanarGenerator.class, DivisionPlanarGenerator.class*/};
		AbstractTopologyAnalyser[] metrics = new AbstractTopologyAnalyser[]{
			new ComponentNumberComputer(),
			new GiantComponentSizeComputer.RelativeGiantComponentSizeComputer(),
			new TopologicalDistanceComputer(false), new PathDistanceComputer(false)
			//new AlgebraicalDistanceComputer(),
			//new GeodesicalDistanceComputer(), new NodeBetweennessComputer()
		};
		
		
		
		CombinedTopologyAnalyser comb = new CombinedTopologyAnalyser(Arrays.asList(metrics));

		for (int seed : seeds) {
			for (float linkProb : linkProbability) {
				for (int node : nodes) {		
					AbstractGraphExperimentInput creator = new FromGeneratorGraphExperimentInput(
						new ReyniErdosGenerator(node, linkProb*2f, seed)
					);					
					TopologyAnalysis topoAna = new TopologyAnalysis(creator, comb);

					topoAna.run(db, null);
				}
			}
		}	
		DefaultResultDisplayingGUI.displayDefault(db,"Topology test");
	}
}
