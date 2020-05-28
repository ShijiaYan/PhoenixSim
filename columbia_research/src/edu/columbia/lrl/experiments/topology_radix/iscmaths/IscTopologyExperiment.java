package edu.columbia.lrl.experiments.topology_radix.iscmaths;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;

public class IscTopologyExperiment implements Experiment {
	
	private int S;
	private int R;
	private int n;
	private int C;
	private int N;
	private int type;
	private double z;
	private boolean excludeUnderDim;
	private int N_orig;
	private int totalRadix;
	
	public IscTopologyExperiment(long N, int totalRadix, int z) {
		this.N = (int)N;
		this.type = 3;
		this.totalRadix = totalRadix;
		this.z = z;
	}
	
	public IscTopologyExperiment(int S, int R, int n) {
		this.S =S;
		this.R =R;
		this.n = n;
		this.type = 1;
		this.C = 1;
		this.N = S;
	}
	
	public IscTopologyExperiment(int S, int R, int n, int C) {
		this.S =S;
		this.R =R;
		this.n = n;
		this.type = 1;
		this.C = C;
		this.N = S*C;
	}
	
	public IscTopologyExperiment(long N, int R, int n, int C) {
		this.S = MoreMaths.ceilDiv((int)N, C);
		this.R =R;
		this.n = n;
		this.type = 1;
		this.C = C;
		this.N = S*C;
		this.z = 1;
	}	
	
	public IscTopologyExperiment(long N, int R, int n, int C, double z, boolean excludeUnderDim) {
		this.S = MoreMaths.ceilDiv((int)N, C);
		this.R =R;
		this.n = n;
		this.type = 1;
		this.C = C;
		this.N = S*C;
		this.z = z;
		this.excludeUnderDim = excludeUnderDim;
		this.N_orig = (int)N;
	}		
	
	public IscTopologyExperiment(double z, int S, int C, int totalRadix) {	
		this.S = S;
		this.C = C;
		this.N = S*C;
		this.N_orig = N;
		this.n = 1;
		this.z = z;
		this.totalRadix = totalRadix;
		this.type = 3;
	}
	
	public IscTopologyExperiment(int S, int n) {
		this.S = S;
		this.n = n;
		this.type = 2;
	}
	
	public IscTopologyExperiment(int C, long N) {
		this.S = MoreMaths.ceilDiv((int)N, C);
		this.N = S*C;
		this.N_orig = (int)N;
		this.C = C;
		this.n = 1;
		this.z = 1;
		this.type = 2;
	}	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		if (type == 1)
			runSimple(man);
		if (type ==2)
			runFind1(man);
		if (type == 3) {
			if (C > 0) { // C fixed mode
				R = totalRadix - C;
				runSimple(man);
			} else {
				for (R = 2 ; R < totalRadix - 1 ; R++) {
					C = totalRadix - R;
					this.S = MoreMaths.ceilDiv(N, C);
					this.N = S*C;
					this.N_orig = N;
					this.n = 1;
					runSimple(man);
				}
			}
		}
	}
	
	public void runSimple(AbstractResultsManager man) {
		if (R <= 1) return;
		// finding D
		int s = S-1;
		int D = 1;
		double accum = 0;
		double temp;
		while ((temp = R*Math.pow(R-1, D-1)) < s) {
			D++;
			s -= temp;
			accum += temp*(D-1);
		}
		accum += s*D;
		
		double traffic = z*N*C*accum/(double)(N-1);
		
		int totalRadix;
	/*	if (C + (n*R) < 24) {
			totalRadix = 24;
		} else */
		if (C + n*R <= 32) {
			double f = (C + n*R)/8d;
			totalRadix = (int)Math.ceil(f)*8;
		} else {
			double f = (C + n*R)/16d;
			totalRadix = (int)Math.ceil(f)*16;			
		}
		
		double utilization = traffic/(double)(n*S*R);
		
		if (utilization < 1 || excludeUnderDim == false) {
		
			DataPoint dp = new DataPoint();
			
			dp.addProperty("S", S);
			dp.addProperty("R", R);
			dp.addProperty("n", n);
			dp.addProperty("C", C);
			dp.addProperty("N_", N);
			dp.addProperty("N orig", N_orig);
			dp.addProperty("z", z);
			dp.addResultProperty("D", D);
			dp.addResultProperty("x", s);
			dp.addResultProperty("delta", C*accum/(double)(N-1));
			dp.addResultProperty("traffic", traffic);
			dp.addResultProperty("capacity", n*S*R);
			dp.addResultProperty("links per end-point", (double)n*S*R/(double)N);
			dp.addResultProperty("difference", n*S*R - traffic);
			dp.addResultProperty("utilization", utilization);
			dp.addResultProperty("radix", C + n*R);
			dp.addResultProperty("r_round", totalRadix<=96 ? totalRadix+"" : "large");
			dp.addResultProperty("underdim", utilization > 1);
			
			Execution ex = new Execution();
			ex.addDataPoint(dp);
			
			man.addExecution(ex);
		}
	}
	
	public void runFind1(AbstractResultsManager man) {
		int bestR = 0;
		double bestCap = Double.POSITIVE_INFINITY;
		double bestTraf = Double.POSITIVE_INFINITY;
		double bestDist = Double.POSITIVE_INFINITY;
		int bestD = 0;
		for (R = 2 ; R < 150 ; R++) {
		// finding D
			int s = S-1;
			int D = 1;
			double accum = 0;
			double temp;
			while ((temp = R*Math.pow(R-1, D-1)) < s) {
				D++;
				s -= temp;
				accum += temp*(D-1);
			}
			accum += s*D;
			double capacity = n*S*R;
			double traf = z*N*C*accum/(double)(N-1);
			
			if (traf <= capacity) {
				if (capacity < bestCap) {
					bestCap = capacity;
					bestTraf = traf;
					bestR = R;
					bestD = D;
					bestDist = C*accum/(double)(N-1);
				}
			}
		}
		
		
		DataPoint dp = new DataPoint();
		
		dp.addProperty("S", S);
		dp.addProperty("Ropt", bestR);
		dp.addProperty("n_opt", n);
		dp.addProperty("C", C);
		dp.addProperty("N", N);		
		dp.addResultProperty("Dopt", bestD);
		dp.addResultProperty("traffic", bestTraf);
		dp.addResultProperty("capacity", bestCap);
		dp.addResultProperty("rounded capacity", MoreMaths.ceilDiv(bestCap, 50000)*50000);
		dp.addResultProperty("ave. dist", bestDist);
		dp.addResultProperty("difference", bestCap - bestTraf);
		Execution ex = new Execution();
		ex.addDataPoint(dp);
		
		man.addExecution(ex);		
	}
	
	

	

	
	
}
