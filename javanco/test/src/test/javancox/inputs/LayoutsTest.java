package test.javancox.inputs;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;



import ch.epfl.javancox.inputs.compounds.FromGeneratorGraphExperimentInput;
import ch.epfl.javancox.inputs.topology.AbstractRandomTopologyGenerator;
import ch.epfl.javancox.inputs.topology.ReyniErdosGenerator;
import ch.epfl.javancox.inputs.topology.ScaleFreeNetworkGenerator;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import ch.epfl.javancox.topology_analysis.CombinedTopologyAnalyser;
import ch.epfl.javancox.topology_analysis.TopologyAnalysis;
import ch.epfl.javancox.topology_analysis.network_metrics.GiantComponentSizeComputer;
import ch.epfl.javancox.topology_analysis.node_metrics.DegreeComputer;
import ch.epfl.javancox.topology_analysis.node_metrics.NodeBetweennessComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.AlgebraicalDistanceComputer;


public class LayoutsTest {
	
	public static void main(String[] args) throws Exception {
		LayoutsTest test = new LayoutsTest();
		
		test.testTreeLayouts();
	}
	
	@Test
	public void testTreeLayouts() throws Exception {
		
	 //	NativeDB oldDb = new NativeDB();
		SmartDataPointCollector sebDb = new SmartDataPointCollector();
	//	OldDB oldDb = new OldDB(); 
		
		int[] nbNodesT = new int[]{2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		int[] seedT = new int[]{1};
		for (int nbNodes : nbNodesT) {
			for (int seed : seedT) {
				
				List<AbstractRandomTopologyGenerator> generators = new ArrayList<AbstractRandomTopologyGenerator>();
			
				generators.add(new ReyniErdosGenerator(0.12f, nbNodes));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 1));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 2));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 0.75f));
				
				
				CombinedTopologyAnalyser lyser = new CombinedTopologyAnalyser(
					new DegreeComputer(),
					new NodeBetweennessComputer(false),
					new GiantComponentSizeComputer(),
					new AlgebraicalDistanceComputer()
				);
				
				for (AbstractRandomTopologyGenerator gen : generators) {
					
					gen.setSeed(seed);
					
				
					TopologyAnalysis analysis = new TopologyAnalysis(
						new FromGeneratorGraphExperimentInput(gen),
						lyser
					);
					
					analysis.run(sebDb, null);

				}
			}
		}
		
		DefaultResultDisplayingGUI.displayDefault(sebDb, "Graph Analysis - SEB");		

	}
	
	
}
