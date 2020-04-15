package edu.columbia.lrl.experiments.routing_in_switches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;
import edu.columbia.lrl.switch_arch.GeneralizedGenerator;

public class RoutingBis {

		
	private int[] permut;
	private AbstractSwitchArchitectureGenerator gen;


//	private boolean[] linkStates;

	private AbstractSwitchArchitectureGenerator.SwitchRoutingStruct struct;



	public RoutingBis(int[] permut, AbstractSwitchArchitectureGenerator gen) {
		if (gen.getInputNodesIndexes().size() != permut.length) {
			throw new IllegalStateException("Do not coincide");
		}
		this.gen = gen;
		this.permut = permut;

//		this.linkStates = new boolean[gen.getNumberOfLinksInvolved()];

		struct = gen.getPossiblePathsStruct();
	}
	
	public ArrayList<Path> route() {
		Stack<Integer> s = new Stack<Integer>();
		explore(0, s);
		return struct.rebuildPaths(MoreArrays.toIntArray(s), permut);
	}
	
	private long trials = 0;
	
	public boolean explore(int level, Stack<Integer> stack) {
		ArrayList<Integer> optionsForThis = options.get(level);
		for (int i = 0 ; i < optionsForThis.size() ; i++) {
			Decision d = new Decision(level, optionsForThis.get(i));
			stack.push(optionsForThis.get(i));
			trials++;
			//if (trials > 700) System.exit(0);
		//	if (trials % 100000 == 0)
				System.out.println(trials + "-" + stack);
			boolean contradiction = removeOptions(d, level);
			if (!contradiction) {
				if (level == permut.length -1) {
					return true;
				}
				if (!explore(level+1, stack)) { 
					addOptions(d, level);
					stack.pop();
				} else {
					return true;
				}
			} else {
				stack.pop();
			}
		}
		return false;
	}
	
	public static class Option implements Comparable<Option> {
		int srcId;
		int pathId;
		
		public Option(int srcId, int pathId) {
			this.srcId = srcId;
			this.pathId = pathId;
		}
		
		
		
		public String toString() {
			return "s" + srcId + ":" + pathId;
		}



		@Override
		public int compareTo(Option o) {
			int i = this.srcId - o.srcId;
			if (i != 0) return i;
			return this.pathId - o.pathId;
		}		
	}
	
	public class Decision extends Option {
		
		Decision(int srcId, int pathId) {
			super(srcId, pathId);
			influencesInTermsOrImpossibilities = new TreeSet<Option>();
			int[] path = struct.paths[srcId][permut[srcId]][pathId];
			for (int i = 0; i < path.length ; i++) {
				influencesInTermsOrImpossibilities.addAll(conflicts.get(path[i]));
			}
		}
		
		public String toString() {
			return "d_s" + srcId + ":" + pathId;
		}		
		
		TreeSet<Option> influencesInTermsOrImpossibilities;
	}
	
	Vector<ArrayList<Option>> conflicts;
	
	private void establishConflicts() {
		conflicts = new Vector<ArrayList<Option>>();
		conflicts.setSize(gen.getNumberOfLinksInvolved());
		for (int i = 0 ; i < gen.getNumberOfLinksInvolved() ; i++) {
			ArrayList<Option> pairList = new ArrayList<Option>();
			conflicts.setElementAt(pairList, i);
		}
		
		for (int i = 0 ; i < permut.length ; i++) {
			int[][] paths = struct.paths[i][permut[i]];
			for (int j = 0 ; j < paths.length ; j++) {
				Option pair = new Option(i, j);
				for (int k = 0 ; k < paths[j].length ; k++) {
					ArrayList<Option> pairList = conflicts.get(paths[j][k]);
					pairList.add(pair);
				}
			}
		}
	}
	
	ArrayList<ArrayList<Integer>> options;
	
	private void createOptions() {
		options = new ArrayList<ArrayList<Integer>>(permut.length);
		for (int i = 0 ; i < permut.length ; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			options.add(list);
			for (int j = 0 ; j < struct.paths[i][permut[i]].length; j++) {
				list.add(j);
			}
		}
	}
	
	private boolean removeOptions(Decision d, int level) {
		boolean contradiction = false;
		int[] optionSizes = new int[options.size()];
		for (int i = level+1 ; i < optionSizes.length ; i++) {
			optionSizes[i] = options.get(i).size();
			if (optionSizes[i] == 0) {
				//int iii = 0;
			}
		}
		for (Option pi : d.influencesInTermsOrImpossibilities) {
			int concerned = pi.srcId;
			if (concerned <= level) continue;
			ArrayList<Integer> al = options.get(concerned);
			if (al.contains((Integer)(pi.pathId))) {
				optionSizes[concerned]--;
				if (optionSizes[concerned] == 0) {
					contradiction = true;
					break;
				}
			}
		/*	if (al.size() == 1 && al.get(0) == pi.pathId) {
				contradiction = true;
				break;
			}*/
		}
		if (contradiction) return true;
		for (Iterator<Option> it = d.influencesInTermsOrImpossibilities.iterator() ; it.hasNext() ; ) {
	//	for (Option pi : d.influencesInTermsOrImpossibilities) {
			Option pi = it.next();
			ArrayList<Integer> f = options.get(pi.srcId);
			boolean success = f.remove((Integer)(pi.pathId));
			if (success == false) {
				it.remove();
			}
		/*	if (options.get(pi.srcId).size() == 0 && pi.srcId > level) {
				int gg = 0;
			}*/
		}
		return false;
	}
	
	private void addOptions(Decision d, int level) {
		for (Option pi : d.influencesInTermsOrImpossibilities) {
			options.get(pi.srcId).add(pi.pathId);
		}
	}
	
	public static void main(String[] args) {
		AbstractSwitchArchitectureGenerator gen =new GeneralizedGenerator(3, 0, 4);
		
		int[] vals = MoreArrays.range(0, gen.getInputNodesIndexes().size()-1);
		vals = PRNStream.getDefaultStream(2).shuffle(vals);
		
		System.out.println(Arrays.toString(vals));
		
		RoutingBis rb = new RoutingBis(vals, gen);
		
		rb.establishConflicts();
		rb.createOptions();
		ArrayList<Path> alp = rb.route();
		
		System.out.println(alp);
		
		AbstractGraphHandler agh = gen.generate();
		
		ColorMap cm = ColorMap.getDarkTonesDefaultMap();
		int index = 0;
		for (Path p : alp) {
			
			String c = ColorMap.toStringColor(cm.getColor(index));
			
			for (LinkContainer l : agh.getLinkContainers(p)) {
				if (l.attribute("link_color", false) != null) {
					for (NodeContainer nc : agh.getNodeContainers()) {
						nc.attribute("node_color").setValue("255, 0 ,0");
					}
				}
				l.attribute("link_color").setValue(c);
			}
			index++;
		}
		
		JavancoGUI.displayGraph(agh);		
	}
	
}
