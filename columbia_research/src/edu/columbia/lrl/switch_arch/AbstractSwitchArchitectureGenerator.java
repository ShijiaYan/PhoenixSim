package edu.columbia.lrl.switch_arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.general_libraries.path.BFSEnumeratedPathSet;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.inputs.topology.AbstractDeterministicGenerator;

public abstract class AbstractSwitchArchitectureGenerator extends AbstractDeterministicGenerator {

	public static final String TYPE_1x2 = "1x2";
	public static final String TYPE_2x2 = "2x2";
	public static final String TYPE_2x1 = "2x1";

	public abstract List<Integer> getInputNodesIndexes();

	public abstract List<Integer> getOutputNodesIndexes();

	public abstract List<Integer> getSwitchingNodesIndexes();

	public abstract int getMaxNumberOfStages();

	public abstract boolean hasOwnRouting();

	public abstract int[][] getRouting();

	public abstract int getNumberOfLinksInvolved();

	/**
	 * This methods return the path(s) that connect one input and output. Paths
	 * are returned as list of link ids. These ids span from 0 to
	 * <code>getNumberOfLinksInvolved() - 1</code>
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	// public abstract int[][][][] getPossiblePaths();

	public AbstractGraphHandler generateInAgh(AbstractGraphHandler agh) {
		generate(agh);
		return agh;
	}

	public AbstractGraphHandler generate() {
		AbstractGraphHandler agh = initJavancoAndGetAGH();
		generate(agh);
		return agh;
	}

	public AbstractGraphHandler initJavancoAndGetAGH() {
		Javanco.initJavancoSafe();
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(true);
		agh.newLayer("physical");
		return agh;
	}

	private TreeSet<Integer> getOutputSwitchesIndexes(AbstractGraphHandler agh) {
		TreeSet<Integer> list = new TreeSet<>();
		for (Integer i : getOutputNodesIndexes()) {
			int alt = agh.getNodeContainer(i).getIncomingLinks().get(0).getOtherNodeIndex(i);
			list.add(alt);
		}
		return list;
	}

	public int[][][][] getPossiblePaths() {
		return getPossiblePathsStruct().paths;
	}
		
	public static class SwitchRoutingStruct {
		public int[][][][] paths;
		public int[][] connectivityMatrix;
		public int[][] linkToSwitches;
		
		public Path rebuildPath(int[] array) {
			if (array.length == 0) return new Path();
			Path p = new Path();
			p.add(linkToSwitches[array[0]][0]);
            for (int i : array) {
                p.add(linkToSwitches[i][1]);
            }
			return p;
		}
		
		public ArrayList<Path> rebuildPaths(int[] array, int[] permut) {
			ArrayList<Path> alp = new ArrayList<>();
			int index = 0;
			for (Integer dest : permut) {
				int[][] pa = paths[index][dest];
				if (array.length > index) {
					int[] path = pa[array[index]];
					alp.add(rebuildPath(path));
				}
				index++;
			}
			return alp;
		}
	}
		
	public SwitchRoutingStruct getPossiblePathsStruct() {
	//	if (agh == null) {
			AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
			agh.newLayer("physical");
			generate(agh);
	//	}
		int size = getSwitchingNodesIndexes().size();
		int[][] conMatrix = new int[size][size];
		int index = 0;
		int nbL = agh.getLinkContainers().size();
		int[][] linkLabelToSwitchCoord = new int[nbL][2];		
		for (LinkContainer lc : agh.getLinkContainers()) {
			int start = lc.getStartNodeIndex();
			int end = lc.getEndNodeIndex();
			if (start >= size || end >= size) continue;
			conMatrix[start][end] = index;
			conMatrix[end][start] = index;
			linkLabelToSwitchCoord[index] = new int[]{start, end};
			lc.attribute("label").setValue(index);
			index++;
		}
		int nbInput = getInputNodesIndexes().size();
		int nbOutput = getOutputNodesIndexes().size();
		
		TreeSet<Integer> output = getOutputSwitchesIndexes(agh);
		
		int[][][][] paths = new int[nbInput][nbOutput][][];
		double[][] dist = agh.getEditedLayer().getIncidenceMatrixDouble(true);
		BFSEnumeratedPathSet ps = new BFSEnumeratedPathSet(dist);
		ps.enumerateAndStoreAll();
		for (int i = 0 ; i < nbInput/2 ; i++) {
			int aa = 0;
			for (Integer j : output) {
				Set<Path> set = ps.getPaths(i, j);
				int[][] pathArray = new int[set.size()][];
				int cc = 0;
				for (Path p : set) {
					int[] segments = new int[p.getNumberOfHops()];
					int dd = 0;
					for (NodePair np : p.getPathSegments()) {
						segments[dd] = conMatrix[np.getStartNode()][np.getEndNode()];
	
						dd++;
					}
					pathArray[cc] = segments;
					cc++;
				}
				paths[i*2][aa*2] = pathArray;
				paths[i*2 +1][aa*2] = pathArray;
				paths[i*2][aa*2 +1] = pathArray;
				paths[i*2 +1][aa*2 +1] = pathArray;
				aa++;
			}
		}
		
		SwitchRoutingStruct struct = new SwitchRoutingStruct();
		struct.paths = paths;
		struct.connectivityMatrix = conMatrix;
		struct.linkToSwitches = linkLabelToSwitchCoord;
		return struct;
	}		

}
