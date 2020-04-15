package edu.columbia.lrl.experiments.topology_radix.routing_study.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.WorseCaseTrafficCalculator;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public abstract class JavancoBasedAxisStructure extends AbstractAxisStructure {

	protected int nbLinks;
	protected int[][][][] linksUsed;
	protected int[][] linksConnectedTo;
	protected int[][] reverse;
	
	public JavancoBasedAxisStructure(short size) {
		super(size);
	}

	protected static void interpret(AbstractGraphHandler agh, JavancoBasedAxisStructure struct) {
		List<LinkContainer> lcs = agh.getLinkContainers();
		struct.reverse = new int[lcs.size()][2];
		for (int i = 0 ; i < lcs.size() ; i++) {
			LinkContainer lc = lcs.get(i);
			struct.reverse[i] = lc.getExtremities();
		}
		Collection<NodeContainer> ncs = agh.getNodeContainers();
		struct.linksConnectedTo = new int[ncs.size()][];
		struct.linksUsed = new int[ncs.size()][ncs.size()][2][];
		struct.nbLinks = lcs.size();
		for (NodeContainer nc : ncs) {
			int nodeIndex = nc.getIndex();
			List<LinkContainer> connectedToNc = nc.getAllConnectedLinks();
			struct.linksConnectedTo[nodeIndex] = new int[connectedToNc.size()];
			for (int i = 0 ; i < connectedToNc.size() ; i++) {
				LinkContainer lc = connectedToNc.get(i);
				int in = lcs.indexOf(lc);
				struct.linksConnectedTo[nodeIndex][i] = in;
			}
		}
		
		WorseCaseTrafficCalculator calc = new WorseCaseTrafficCalculator(ncs.size());
		
		for (int i = 0 ; i < ncs.size() ; i++) {
			Path[] paths = BFS.getShortestPathsUndirectedFrom(agh, i);
			calc.addPaths(paths);
			for (int j = 0 ; j < paths.length ; j++) {
				if (j == i) continue;
				struct.linksUsed[i][j][0] = new int[paths[j].getNumberOfHops()];
				struct.linksUsed[i][j][1] = new int[paths[j].getNumberOfHops()];
				ArrayList<NodePair> npl = paths[j].getPathSegments();
				for (int k = 0 ; k < npl.size() ; k++) {
					NodePair np = npl.get(k);
					LinkContainer l = agh.getLinkContainer(np.getStartNode(), np.getEndNode(), false);
					struct.linksUsed[i][j][0][k] = lcs.indexOf(l);
					if (l.getStartNodeIndex() == np.getStartNode()) {
						struct.linksUsed[i][j][1][k] = 1;
					} else {
						struct.linksUsed[i][j][1][k] = -1;
					}
				}
			}
		}
		calc.getWorseTraffic();
	}
	
	@Override
	protected int[][] getIndexesOfLinksUsed(int from, int to) {
		try {
			return linksUsed[from][to];
		}
		catch (Exception e) {
			return new int[0][];
		}
	}

	@Override
	public int getNumberOfLinksInStructure() {
		return nbLinks;
	}

	@Override
	public int[] getIndexesOfLinksConnectedTo(int nodeIndex) {
		return linksConnectedTo[nodeIndex];
	}
	
	@Override
	public int[] getExtremitiesOfLink(int k) {
		try {
			return reverse[k];
		}
		catch (NullPointerException e) {
		}
		return null;
	}	

}
