package edu.columbia.sebastien.fat_trees;

import java.util.Arrays;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.utils.MoreArrays;

public class FatTreeOverproExperiment implements Experiment {
	
	private int switchRadix;
	private int levels;
	
	public FatTreeOverproExperiment(int switchRadix, int levels) {
		this.switchRadix = switchRadix;
		this.levels = levels;
		
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		int[] upFacingPorts = new int[levels];
		
		Arrays.fill(upFacingPorts, 1);
		
		
		
		while (MoreArrays.sum(upFacingPorts) < levels*switchRadix/2) {
			
			double[] overDouble = new double[upFacingPorts.length];
			for (int i = 0 ; i < upFacingPorts.length; i++) {
				overDouble[i] = (switchRadix - upFacingPorts[i])/(double) upFacingPorts[i];
			}
			
			TomkosChapterFatTreeExp subExp = new TomkosChapterFatTreeExp(switchRadix, levels+1, overDouble);
			
			subExp.run(man, dis);
			
			increment(upFacingPorts);
		}
		
	}

	private void increment(int[] upFacingPorts) {
		int level = 0;
		boolean undone = true;
		while (undone && level <= this.levels) {
			upFacingPorts[level]++;
			if (upFacingPorts[level] > switchRadix/2) {
				upFacingPorts[level] = 1;
				level++;
			} else {
				undone = false;
			}
		}
		
	}
	
	

}
