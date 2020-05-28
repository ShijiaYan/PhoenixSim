package edu.columbia.sebastien.fat_trees;

import java.util.List;
import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator.WebTopologyGeneratorStub;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.AbstractDeterministicGenerator;
import edu.columbia.lrl.experiments.topology_radix.locality.UniformTrafficMatrix;

public class OversubscribedFattreeGenerator extends AbstractDeterministicGenerator {
	
	private int radix;
	private int levels;
	private double[] overSubs;
	private TomkosChapterFatTreeExp fattreecalc;
	
	public OversubscribedFattreeGenerator(int radix, int levels, double[] overSubs) {
		this.radix = radix;
		this.levels = levels;
		this.overSubs = new double[overSubs.length+1];
		fattreecalc = new TomkosChapterFatTreeExp(radix, levels, overSubs);

		System.arraycopy(overSubs, 0, this.overSubs, 1, overSubs.length);
		this.overSubs[0] = 1;
	}
	

	@Override
	public void generate(AbstractGraphHandler agh) {
		vertical = false;
		int endpoints = fattreecalc.getEndpoints();
		int[] offsets = new int[levels+1];
		List<NodeContainer> newList = placeOneColumn(0, endpoints, endpoints, agh);
		offsets[0] = newList.size();

		for (int i = 0 ; i < levels ; i++) {
			newList = placeOneColumn(i+1, (int)Math.round(endpoints/fattreecalc.getEPPerRouteratLevel(i)), endpoints, agh);
			offsets[i+1] = offsets[i] + newList.size();
		}
		
		int clusterL1 = (int)fattreecalc.getEPPerRouteratLevel(0);
		for (int i = 0 ; i < endpoints ; i++) {
			agh.newLink(i, offsets[0] + i/clusterL1);
		}
		
		for (int i = 0 ; i < levels-1 ; i++) {
			int nb = offsets[i+1] - offsets[i]; // nb routers at level
			int nb2 = offsets[i+2] - offsets[i+1]; // nb routers at level up
			for (int k = 0 ; k < nb ; k++) {
				int up = (int)Math.round(radix*(1/(overSubs[i+1]+1)));
				for (int u = 0 ; u < up ; u++) {
					int idInGroup;
					if (i == levels - 2) {
						idInGroup = (k*up + u) % nb2;
					} else {
						int down = (int) (radix*overSubs[i+2]/(overSubs[i+2]+1));
						idInGroup = (k/down) *up + u;
					}
					
					agh.newLink(offsets[i]+k, offsets[i+1] + idInGroup);
				}
				
			}
		}
		for (NodeContainer nc: agh.getNodeContainers()) {
			nc.attribute("node_see_id").setValue("false");
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "OversubscribedFattree";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		return fattreecalc.getEndpoints();
	}
	
	public static class OversubscribedFattreeGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateFatTreeAlt(AbstractGraphHandler agh, 
								@ParameterDef(name = "Switch radix") int radix,
								@ParameterDef(name = "Number of up facing ports (coma separated)") String over) {
			
			int[] ups = TypeParser.parseIntArray(over);
			
			double[] overDouble = new double[ups.length];
			
			for (int i = 0 ; i < ups.length; i++) {
				overDouble[i] = (radix - ups[i])/(double) ups[i];
			}

			OversubscribedFattreeGenerator gen = new OversubscribedFattreeGenerator(radix, ups.length+1, overDouble);
			gen.generate(agh);
			
			
			

			return null;
		}		
		
		@MethodDef
		public String generateFatTree(AbstractGraphHandler agh, 
								@ParameterDef(name = "Switch radix") int radix,
								@ParameterDef(name = "Levels")  int levels,
								@ParameterDef(name = "over factors") String over) {

			OversubscribedFattreeGenerator gen = new OversubscribedFattreeGenerator(radix, levels, TypeParser.parseDouble(over));
			gen.generate(agh);
			
	/*		if (pcol) {
				double[][] dist = new double[clients][clients];
				for (int i = 0 ; i < clients ; i++) {
					Path[] p = BFS.getShortestPathsUndirectedFrom(agh, i);
					for (int j = 0 ; j < clients ; j++) {
						if (j != i && p[j] != null) {
							dist[i][j] = p[j].getNumberOfHops();
						}
					}
				}
				dist = Matrix.normalize(dist);
				PcolorGUI gui = new PcolorGUI(dist);
				gui.showInFrame();				
			}
			

*/
			return null;
		}
	}	
	

}
