package edu.columbia.sebastien.fat_trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.columbia.lrl.experiments.topology_radix.locality.AbstractTrafficMatrix;
import edu.columbia.lrl.experiments.topology_radix.locality.AdversarialTrafficMatrixForFatTree;
import edu.columbia.lrl.experiments.topology_radix.locality.UniformTrafficMatrix;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.IntegerArrayList;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.AbstractDeterministicGenerator;

public class SlimmedFatTreeGenerator extends AbstractDeterministicGenerator {

	final private int clients;
	final private int maxRadix;
	final private AbstractTrafficMatrix trafMat;
	final private boolean unfat;
	final private boolean mergeLast;
	
	// calculated values
	private int nbSwitches;
	private int nbLinks;
	private double averageDistance;
	private int levels;
	
	private int[] switchesPerLevel;
	
	public SlimmedFatTreeGenerator(int clients, 
								   int maxRadix, 
								   AbstractTrafficMatrix trafMat, 
								   boolean unfat, 
								   boolean mergeLast) {
		if (maxRadix < 4) throw new IllegalStateException("Radix must be >= 4");
		this.clients = clients;
		this.maxRadix = maxRadix;
		this.trafMat = trafMat;
		this.unfat = unfat;
		this.mergeLast = mergeLast;
		trafMat.init(clients, null);
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		vertical = false;
		nbLinks = 0;
		nbSwitches = 0;
		averageDistance = 0;
		agh.clear();
		int maxLevels = (int)Math.ceil(MoreMaths.logN(clients, maxRadix)) + 5;
		int[] clusterSizes = new int[maxLevels]; // change size in the future
		switchesPerLevel = new int[maxLevels];
		int[] switchesStartIndex = new int[maxLevels];
		int[][] repartitions = new int[maxLevels][];
		int[] clustering = new int[maxLevels];
		double[] uptrafTot = new double[maxLevels];
		clusterSizes[0] = 1;
		switchesPerLevel[0] = clients;
		clustering[0] = 1;
		uptrafTot[0] = trafMat.getTrafficFrom(0);
		int nbUpload = 0;
		double upTraf = 0;
		
		
		boolean cont = true;
		int level = 0;
		while (cont) {
			for (int clusterSize = maxRadix ; clusterSize > 0 ; clusterSize--) {
				repartitions[level] = getRepartion(switchesPerLevel[level], clusterSize);			
				int maxAggrAtThisLevel = MoreArrays.max(repartitions[level]);	
				int totalClusterSize = Math.min(clients, maxAggrAtThisLevel*clusterSizes[level]);
				upTraf = trafMat.getTraffic(0, totalClusterSize, totalClusterSize, clients - totalClusterSize)/(double)clustering[level];
				nbUpload = (int)Math.ceil(upTraf);
				if (nbUpload + clusterSize <= maxRadix) {
					clusterSizes[level+1] = totalClusterSize; 
					switchesPerLevel[level+1] = (int)Math.ceil((double)clients/(double)totalClusterSize);
					switchesStartIndex[level+1] = switchesStartIndex[level] + switchesPerLevel[level];
					clustering[level+1] = clustering[level]*nbUpload;
					uptrafTot[level+1] = clustering[level]*upTraf; 
					break;
				}
			}
			if (nbUpload == 0) cont = false;
			level++;
		}
		
		int[][] repartitionCopy = MoreArrays.arrayCopy(repartitions);
		
		double[] switchesWeight = new double[MoreArrays.sum(switchesPerLevel)];
		int[] clusters = new int[switchesWeight.length];
	//	int[] siblings = new int[switchesWeight.length];
		int[] uplinks = new int[switchesWeight.length];
	//	int[] downlinks = new int[switchesWeight.length];
		int[] switchMult = new int[switchesWeight.length];
		IntegerArrayList[] downLinks = IntegerArrayList.getInitialisedArray(switchesWeight.length);

		// for routing
		ArrayList<ArrayList<Integer>> routing = new ArrayList<>();
		for (int i = 0; i <= switchesStartIndex[level] ; i++) {
			ArrayList<Integer> list = new ArrayList<>();
			routing.add(list);
			if (i < switchesStartIndex[1]) {
				list.add(i);
			}
		}
	
		Arrays.fill(uplinks, 0, switchesPerLevel[0], 1);	
		Arrays.fill(clusters, 0, switchesPerLevel[0], 1);
		Arrays.fill(switchMult, 0, switchesPerLevel[0], 1);		
		for (int i = 0 ; i < level+1 ; i++) {
			if (unfat == false) {// draw
				placeOneColumn(i, switchesPerLevel[i], clients, agh);
			}
			if (i > 0) {	
				int repartionIndex = 0;
				int[] currentRep = repartitions[i-1];
				for (Integer nodeIndex : MoreArrays.range(switchesStartIndex[i-1], switchesStartIndex[i]-1)) {
					double ratio = (double)clusters[nodeIndex]/(double)clusterSizes[i-1];
					int indexOfNodeToConnect = switchesStartIndex[i] + repartionIndex;
					switchesWeight[indexOfNodeToConnect] += ratio;
					clusters[indexOfNodeToConnect] += clusters[nodeIndex];	
					int clustUpdated = 1;
			//		int ports = 1;
					if (i > 1) {
						clustUpdated = (int)Math.ceil(switchesWeight[nodeIndex]*uptrafTot[i-2]);
						uplinks[nodeIndex] = clustUpdated;
						downLinks[indexOfNodeToConnect].add(clustUpdated);
					//	downlinks[indexOfNodeToConnect] += clustUpdated;
					//	ports = uplinks[nodeIndex] + downlinks[nodeIndex];
						
						switchMult[nodeIndex] = findSwitchMult(uplinks[nodeIndex], downLinks[nodeIndex], mergeLast);
					/*	if (mergeLast) {
							boolean possible = true;
							int merge = 1;
							int worstCaseDown = MoreMaths.ceilDiv(downlinks[nodeIndex],maxDownLinks[nodeIndex]);
							int worstCaseUp = MoreMaths.ceilDiv(uplinks[nodeIndex], maxDownLinks[nodeIndex]);
							while(possible) {
								if ((worstCaseDown + worstCaseUp)*merge <= maxRadix) {
									switchMult[nodeIndex] = MoreMaths.ceilDiv(maxDownLinks[nodeIndex],merge);
									merge++;
								} else {
									possible = false;
								}
							}
						} else {
							switchMult[nodeIndex] = maxDownLinks[nodeIndex];
						}*/

						
						
					//	switchMult[nodeIndex] = MoreMaths.ceilDiv(ports, maxRadix);
					}
					if (unfat == false) {// draw
						routing.get(indexOfNodeToConnect).addAll(routing.get(nodeIndex));
						LinkContainer lc = agh.newLink(nodeIndex, indexOfNodeToConnect);
						lc.attribute("dests").setValue(routing.get(nodeIndex));
						if (clustUpdated > 1) {
							lc.attribute("label").setValue("x"+clustUpdated);
						}	
						if (switchMult[nodeIndex] > 1) {
							agh.getNodeContainer(nodeIndex).attribute("label").setValue("x"+switchMult[nodeIndex]);
						}						
					}	
					nbLinks += clustUpdated;
					currentRep[repartionIndex]--;
					if (currentRep[repartionIndex] == 0) { // check if should not be < 0
						repartionIndex++;
					}
				}
			}			
		}
		int lastNodeIndex = switchesStartIndex[level];		
		switchMult[lastNodeIndex] = findSwitchMult(uplinks[lastNodeIndex], downLinks[lastNodeIndex], mergeLast);
		if (unfat == false && switchMult[lastNodeIndex] > 1) {
			agh.getNodeContainer(lastNodeIndex).attribute("label").setValue("x"+switchMult[lastNodeIndex]);
			
		}
		
		this.levels = level;
		
		if (unfat) {
			List<NodeContainer> previousList = null;
			for (int i = 0 ; i < level+1 ; i++) {
				List<NodeContainer> newList;
				if (i < level) {
					newList = placeOneColumn(i, MoreArrays.sum(switchMult, switchesStartIndex[i], switchesStartIndex[i+1] -  switchesStartIndex[i]), clients, agh);
				} else {
					newList = placeOneColumn(i, switchMult[switchesStartIndex[i]], clients, agh);				
				}
				
				if (i > 0) {
					int cursor1 = previousList.get(0).getIndex();
					int cursor2 = newList.get(0).getIndex();
					int leftRealIndex = switchesStartIndex[i-1];
					int rightRealIndex = switchesStartIndex[i];
					for (int j = 0 ; j < repartitionCopy[i-1].length ; j++) {
						int add = connectNodes(agh, cursor1, cursor2, leftRealIndex, rightRealIndex, repartitionCopy[i-1][j], uplinks, switchMult);
						cursor1 += add;
						cursor2 += switchMult[switchesStartIndex[i] + j];
						leftRealIndex += repartitionCopy[i-1][j];
						rightRealIndex++;
					}
				}
				previousList = newList;
			}
		}
		
		this.switchesPerLevel = new int[level];
		
		// counting more precisely
		for (int i = 1 ; i < level ; i++) {
			this.switchesPerLevel[i-1] = MoreArrays.sum(switchMult, switchesStartIndex[i], switchesStartIndex[i+1] - switchesStartIndex[i]);
		}
		this.switchesPerLevel[level-1] = switchMult[switchesStartIndex[level]];
		
		System.out.println("Number of switches :" + (MoreArrays.sum(switchMult) - clients));
		System.out.println("Number of link :" + nbLinks);		
		
		nbSwitches = MoreArrays.sum(switchMult) - clients;
		
		double totDist = 0;
	//	for (int i = 0 ; i < clients ; i++) {
			Path[] p = BFS.getShortestPathsUndirectedFrom(agh, 0);
			for (int j = 1 ; j < clients ; j++) {
				if (p[j] != null) {
					totDist += p[j].getNumberOfHops();
				}
			}
	//	}
		averageDistance = totDist/(double) (clients-1);

		
		Collection<NodeContainer> nodes = agh.getNodeContainers();
		for (NodeContainer n : nodes) {
			int degree = n.getConnectedLinks().size();
			if (degree > maxRadix) throw new IllegalStateException();
		}
	}
	
	private int findSwitchMult(int uplinks, IntegerArrayList downLinks, boolean mergeLast) {
		if (mergeLast) {
			int minSwitches;
			if (downLinks.size() <= 1) {
				minSwitches = 1;
			} else {
				minSwitches = downLinks.getDecreasingValues()[1];
				
				for (int testSwi = minSwitches ; testSwi > 0 ; testSwi--) {
					int ports = MoreMaths.ceilDiv(uplinks, testSwi);
					for (Integer downL : downLinks) {
						ports += MoreMaths.ceilDiv(downL, testSwi);
					}
					if (ports <= maxRadix) {
						minSwitches = testSwi;
					} else {
						break;
					}
				}			
			}		
			int ports = downLinks.sum() + uplinks;			
			return Math.max(MoreMaths.ceilDiv(ports, maxRadix), minSwitches);
		} else {
			if (downLinks.size() == 0) return 1;
			return downLinks.max();
		}
		
	}
	
	private int connectNodes(AbstractGraphHandler agh, 
			int cursor1, 
			int cursor2, 
			int leftRealIndex, 
			int rightRealIndex, 
			int repartition, 
			int[] uplinks,
			int[] switchMult) {
			
		// right now, only the number of nodes on the left is given as parameter. This is not enough. Also the clustering among these
		//nodes (the repartition) should be given, together with how many uplinks for each cluster (repartition group). This
		//should replace the "i" loop
		int iteRight = 0;
		int leftClust = 0;
		for (int i = 0 ; i < repartition ; i++) {
			int iteLeft = 0;
			for (int j = 0 ; j < uplinks[i+leftRealIndex] ; j++) {
				LinkContainer lc = agh.getLinkContainer(cursor1+leftClust+iteLeft, cursor2+iteRight);
				if (lc == null) {
					lc = agh.newLink(cursor1+leftClust+iteLeft, cursor2+iteRight);
					lc.attribute("mult").setValue(1);
				} else {
					int multlink = lc.attribute("mult").intValue();
					lc.attribute("mult").setValue(multlink+1);
					lc.attribute("label").setValue(multlink+1);
				}
				iteRight++;
				if (iteRight > switchMult[rightRealIndex] - 1) {
					iteRight = 0;
				}
				iteLeft++;
				if (iteLeft > switchMult[leftRealIndex + i] - 1) {
					iteLeft = 0;
				}
			}
			leftClust += switchMult[leftRealIndex + i];
		}
		return leftClust;
	}

	private int[] getRepartion(int clients, int clustering) {
		int bins = MoreMaths.ceilDiv(clients, clustering);
		int[] rep = new int[bins];
		for (int i = 0 ; i < clients ; i++) {
			rep[i % bins]++;
		}
		return rep;
	}	

	@Override
	public String getName() {
		return "SlimmedFatTree";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		Map<String, String> m = trafMat.getAllParameters();
		m.put("Radix", maxRadix+"");
		m.put("Clients", clients+"");
		return m;
	}

	@Override
	public int getNumberOfNodes() {
		return 0;
	}
	

	public static class SlimmedTreeGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateFatTree(AbstractGraphHandler agh, 
								@ParameterDef(name = "Number of clients") int clients, 
								@ParameterDef(name = "Switch radix") int radix,
								@ParameterDef(name = "Load") float load,
								@ParameterDef(name = "Unfat?") boolean unfat,
								@ParameterDef(name = "Try to merge?") boolean merge,
								@ParameterDef(name="Show pcolor?") boolean pcol) {

		//	SlimmedFatTreeGenerator gen = new SlimmedFatTreeGenerator(clients, radix, new UniformTrafficMatrix(load), unfat, merge);
			SlimmedFatTreeGenerator gen = new SlimmedFatTreeGenerator(clients, radix, new AdversarialTrafficMatrixForFatTree(load), unfat, merge);			
			gen.generate(agh);
			
			if (pcol) {
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
			


			return null;
		}
	}
	
	public static class SlimmedTreeExperiment implements Experiment {
		
		SlimmedFatTreeGenerator gen;
		
		static int i = 0;
		
		public SlimmedTreeExperiment(int nbClients, int radix, AbstractTrafficMatrix trafMat) {
			gen = new SlimmedFatTreeGenerator(nbClients, radix, trafMat, false, true);
		}

		@Override
		public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
			Execution e = new Execution();
			
			DataPoint dp = new DataPoint();
			dp.addProperty("radix", gen.maxRadix);
			dp.addProperty("clients", gen.clients);
			dp.addProperties(gen.trafMat.getAllParameters());
			

			
			AbstractGraphHandler agh = gen.generate();
			
			int aboveTorPorts = 0;
			
			for (int i = 0 ; i < gen.switchesPerLevel.length ; i++) {
				DataPoint dp1 = dp.getDerivedDataPoint();
				dp1.addProperty("level id", i);
				dp1.addResultProperty("Switches per level", gen.switchesPerLevel[i]);
				man.addDataPoint(dp1);
				if (gen.switchesPerLevel[i] == 0) break;	
				if (i > 0) aboveTorPorts +=gen.switchesPerLevel[i]*gen.maxRadix;
			}
			
			agh.saveGraphImage("slim" + String.format("%03d", i), 2064, 1564);
			
			dp.addResultProperty("nb switches", gen.nbSwitches);
			dp.addResultProperty("nb links", gen.nbLinks);
			dp.addResultProperty("nb links internal", (gen.nbLinks - gen.clients)*2);	
			dp.addResultProperty("above tor ports", aboveTorPorts);
			dp.addResultProperty("average dist", gen.averageDistance);
			dp.addResultProperty("levels", gen.levels);
			
			e.addDataPoint(dp);
			
			man.addExecution(e);
			
			i++;
			
		}
	}

}
