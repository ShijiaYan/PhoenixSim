package ch.epfl.javanco.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.Test;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;


public class CliqueDetector {

	@Test
	public static void cliqueTest(String[] args) throws Exception {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.newLayer("physical");
		for (int i = 0 ; i < 6 ; i++) {
			agh.newNode();
		}
		agh.newLink(0,1);
		agh.newLink(0,2);
		agh.newLink(1,2);
		agh.newLink(2,3);
		agh.newLink(3,4);
		agh.newLink(2,4);
		agh.newLink(2,5);
		agh.newLink(3,5);
		agh.newLink(4,5);
		System.out.println(getClique(agh));
	}

	public static List<TreeSet<Integer>> getClique(AbstractGraphHandler agh) {
		return getClique(agh, "physical", false);
	}

	public static List<TreeSet<Integer>> getClique(AbstractGraphHandler agh, String layerName, boolean directed) {
		if (directed == true) {
			throw Javanco.DIRECTED_EXCEPTION;
		}
		LayerContainer layC = agh.getLayerContainer(layerName);
		Collection<LinkContainer> links = layC.getLinkContainers();
		ArrayList<TreeSet<Integer>> cli = new ArrayList<TreeSet<Integer>>(links.size());
		for (LinkContainer lc : links) {
			TreeSet<Integer> el = new TreeSet<Integer>();
			el.add(lc.getStartNodeIndex());
			el.add(lc.getEndNodeIndex());
			cli.add(el);
		}
		boolean[][] incimat = layC.getIncidenceMatrix();
		Vector<Collection<Integer>> reachableHighestNodes = new Vector<Collection<Integer>>(agh.getHighestNodeIndex()+1);
		reachableHighestNodes.setSize(agh.getHighestNodeIndex()+1);
		for (NodeContainer nc : agh.getNodeContainers()) {
			reachableHighestNodes.setElementAt(nc.getConnectedNodeIndexes().tailSet(nc.getIndex()+1), nc.getIndex());
		}
		return getCliqueInternal(cli, incimat, reachableHighestNodes);
	}

	private static List<TreeSet<Integer>> getCliqueInternal(List<TreeSet<Integer>> lastLevel,
			boolean[][] incidenceMatrix,
			Vector<Collection<Integer>> reachableHighestNodes) {
		if (lastLevel.size() > 0) {
			ArrayList<TreeSet<Integer>> nextLevelList = new ArrayList<TreeSet<Integer>>();
			for (TreeSet<Integer> set : lastLevel) {
				for (int i : reachableHighestNodes.get(set.last())) {
					boolean test = true;
					for (int a : set) {
						if (!incidenceMatrix[a][i]) {
							test = false;
							break;
						}
					}
					if (test) {
						TreeSet<Integer> augCli = new TreeSet<Integer>();
						augCli.addAll(set);
						augCli.add(i);
						nextLevelList.add(augCli);
					}
				}
			}
			lastLevel.addAll(getCliqueInternal(nextLevelList, incidenceMatrix, reachableHighestNodes));
			return lastLevel;
		} else {
			return emptyList;
		}

	}

	private static List<TreeSet<Integer>> emptyList = new ArrayList<TreeSet<Integer>>(0);
}
