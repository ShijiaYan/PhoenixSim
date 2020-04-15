package edu.columbia.lrl.experiments.topology_radix.iscmaths;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class IscTopologyAdversarialExperiment  implements Experiment {

	private int S;
	private int R;
	private int n;
	private int C;
	private int N;
	
	public IscTopologyAdversarialExperiment(int N, long C) {
		this.N = N;	
		this.C = (int)C;
		this.n = 1;		
	}
	
	public IscTopologyAdversarialExperiment(long S, long C) {
		this.N = (int)(S*C);	
		this.S = (int)S;
		this.C = (int)C;
		this.n = 1;	
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		runFindAdv(man);
	}	
	
	public void runFindAdv(AbstractResultsManager man) {
		S = MoreMaths.ceilDiv(N, C);
		N = S*C;
		int bestR = 0;
		double bestCap = Double.POSITIVE_INFINITY;
		double bestTraf = Double.POSITIVE_INFINITY;
		int bestD = 0;
		for (R = 2 ; R < 200 ; R++) {
		// finding D
			int s = S-1;
			int D = 1;
			double temp;
			while ((temp = R*Math.pow(R-1, D-1)) < s) {
				D++;
				s -= temp;
			}
			double capacity = n*S*R;
			double traf = (double)N*(double)D;
			
			if (traf <= capacity) {
				if (capacity < bestCap) {
					bestCap = capacity;
					bestTraf = traf;
					bestR = R;
					bestD = D;
				}
			}
		}
		int totalRadix;
		
		if (C + (n*bestR) < 48) {
			double f = (C + (n*bestR))/8d;
			totalRadix = (int)Math.ceil(f)*8;
		} else {
			double f = (C + (n*bestR))/16d;
			totalRadix = (int)Math.ceil(f)*16;			
		}
		
		
		DataPoint dp = new DataPoint();
		
		dp.addProperty("z", "adv");
		dp.addProperty("S", S);
		dp.addProperty("Ropt", bestR);
		dp.addProperty("n_opt", n);
		dp.addProperty("C", C);
		dp.addProperty("N", N);
		dp.addResultProperty("r", C + (n*bestR));
		dp.addResultProperty("r_round", totalRadix);		
		dp.addResultProperty("Dopt", bestD);
		dp.addResultProperty("traffic", bestTraf);
		dp.addResultProperty("capacity", bestCap);
		dp.addResultProperty("difference", bestCap - bestTraf);
		Execution ex = new Execution();
		ex.addDataPoint(dp);
		
		man.addExecution(ex);		
	}	
		
}
