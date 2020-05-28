package edu.columbia.lrl.experiments.routing_in_switches;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.MultiDigitCounter;
import ch.epfl.general_libraries.utils.ObjectIndexedCounter;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import edu.columbia.lrl.switch_arch.AbstractSwitchArchitectureGenerator;
import edu.columbia.lrl.switch_arch.BenesSwitchGenerator;

public class RoutingAndSignalInSwitches {
	
	private int[] permut;
//	private AbstractSwitchArchitectureGenerator gen;
	
	
	private boolean[] linkStates;

	private AbstractSwitchArchitectureGenerator.SwitchRoutingStruct struct;
	
	private int trials;
	
	public RoutingAndSignalInSwitches(int[] permut, AbstractSwitchArchitectureGenerator gen) {
		if (gen.getInputNodesIndexes().size() != permut.length) {
			throw new IllegalStateException("Do not coincide");
		}
		//this.gen = gen;
		this.permut = permut;
		
		this.linkStates = new boolean[gen.getNumberOfLinksInvolved()];

		struct = gen.getPossiblePathsStruct();
		
	}
	
	public ArrayList<Path> route2() {
		
		int index = 0;
		int[] nbPossiblePaths = new int[permut.length];
		Arrays.fill(linkStates, true);
		ArrayList<Path> alp = new ArrayList<>();
		for (Integer dest : permut) {
			System.out.print("Route from " + index + " to "+ dest + " ");
			int[][] pa = struct.paths[index][dest];
			alp.add(rebuildPath(tryPaths(pa)));
			System.out.println();
			nbPossiblePaths[index] = pa.length;
			index++;
		}
		return alp;
	}
	public ArrayList<Path> route() {	
		
		int index = 0;
		int[] nbPossiblePaths = new int[permut.length];
		for (Integer dest : permut) {
			int[][] pa = struct.paths[index][dest];
			nbPossiblePaths[index] = pa.length;
			index++;
		}
		
		int[][] used = new int[permut.length][permut.length/2];
		Arrays.fill(linkStates, true);
		MultiDigitCounter mdc = new MultiDigitCounter(nbPossiblePaths);
		
		boolean ok = false;
		int[] okTab = null;
		trials = 0;
		int level = 0;
		while(true) {
			int[] state = mdc.getState();
			level = tryP(state, used, level);
			if (level < 0) {
				okTab = state;
				ok = true;
				break;
			}
			int ret = mdc.incrementWithReset(level);
			for (int i = ret ; i < level ; i++) {
				for (int j = 0 ; j < permut.length/2 ; j++) {
					linkStates[used[i][j]] = true;
				}
			}
			level = ret;
			
			trials++;
			if (trials % 10000000 == 0) {
				System.out.println(trials);
				System.out.println("Trying " + Arrays.toString(mdc.getState()));
			}
		}
		if (ok == false) {
			throw new IllegalStateException("Can't");
		}
		System.out.println(Arrays.toString(okTab));
		
		
		// construct paths
		ArrayList<Path> lp = new ArrayList<>(okTab.length);
		for (int i = 0 ; i < okTab.length ; i++) {
			lp.add(rebuildPath(struct.paths[i][permut[i]][okTab[i]]));
		}
		return lp;
	}
	
	public Path rebuildPath(int[] array) {
		if (array.length == 0) return new Path();
		Path p = new Path();
		p.add(struct.linkToSwitches[array[0]][0]);
        for (int i : array) {
            p.add(struct.linkToSwitches[i][1]);
        }
		return p;
	}
	
	public int tryP(int[] choices, int[][] used, int level) {

		for (int i = level ; i < choices.length ; i++) {
			int[][] possiblePaths = struct.paths[i][permut[i]];
			int[] choosenPath = possiblePaths[choices[i]];
            for (int value : choosenPath) {
                if (linkStates[value] == false) {
                    return i;
                }
            }
			for (int j = 0 ; j < choosenPath.length ; j++) {
				used[i][j] = choosenPath[j];
				linkStates[choosenPath[j]] = false;
			}
		}
		return -1;
	}
	
	private int[] tryPaths(int[][] paths) {
		for (int i = 0 ; i < paths.length ; i++) {
			boolean ok = true;
			for (Integer j : paths[i]) {
				if (linkStates[j] == false) {
					ok = false;
					break;
				}
			}
			if (ok) {
				System.out.print(" path ID" + i + " madeof ");
				for (Integer j : paths[i]) {
					System.out.print(j + " ");
					linkStates[j] = false;
				}
			//	break;
				return paths[i];
			}
		}
		System.out.println();
		return new int[0];
	}
	
	public static class RoutingAndSignalExperiment implements Experiment {
		
		private int logRad;
		private int seed;
		
		public RoutingAndSignalExperiment(int logRad, int seed) {
			this.logRad = logRad;
			this.seed = seed;
		}

		@Override
		public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
			AbstractSwitchArchitectureGenerator gen =new BenesSwitchGenerator((int)Math.pow(2, logRad));
			
			int[] vals = MoreArrays.range(0, gen.getInputNodesIndexes().size()-1);
			vals = PRNStream.getDefaultStream(seed).shuffle(vals);
			
			RoutingAndSignalInSwitches f = new RoutingAndSignalInSwitches(vals,gen);
			long l = System.nanoTime();
			ArrayList<Path> alp = f.route();
			long time = System.nanoTime() - l;
			
			DataPoint dp = new DataPoint();
			dp.addProperty("logRad", logRad);
			dp.addProperty("seed", seed);
			dp.addResultProperty("time in ms", time / 1000000);
			dp.addResultProperty("number of paths", alp.size());
			dp.addResultProperty("trials", f.trials);
			
			Execution e = new Execution();
			
			e.addDataPoint(dp);
			
			man.addExecution(e);
		}
		
	}
	
	public static void main(String[] args) {
		AbstractSwitchArchitectureGenerator gen =new BenesSwitchGenerator(16);
		
		int[] vals = MoreArrays.range(0, gen.getInputNodesIndexes().size()-1);
		vals = PRNStream.getDefaultStream(8).shuffle(vals);
		
		System.out.println(Arrays.toString(vals));
		
		RoutingAndSignalInSwitches f = new RoutingAndSignalInSwitches(vals,gen);
		ArrayList<Path> alp = f.route();
		
		f.analyzeConflicts();
		
		AbstractGraphHandler agh = gen.generate();
		
		Color coo = ColorMap.getRandomColor();
		int index = 0;
		for (Path p : alp) {
			
			String c = ColorMap.toStringColor(ColorMap.getDarkerTone(coo, alp.size()+2, index));
			
			for (LinkContainer l : agh.getLinkContainers(p)) {
				l.attribute("link_color").setValue(c);
			}
			index++;
		}
		
		JavancoGUI.displayGraph(agh);		
	}

	private void analyzeConflicts() {
		ObjectIndexedCounter<Integer> cc = new ObjectIndexedCounter<>();
		
		for (int i = 0 ; i < permut.length ; i++) {
			int[][] paths = struct.paths[i][permut[i]];
            for (int[] path : paths) {
                for (int k = 0; k < path.length; k++) {
                    cc.increment(path[k]);
                }
            }
		}
		
		System.out.println(cc);
	}



}
