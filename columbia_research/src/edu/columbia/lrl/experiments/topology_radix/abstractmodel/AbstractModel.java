package edu.columbia.lrl.experiments.topology_radix.abstractmodel;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.PairList;

public class AbstractModel {
	
	private double z;
	private int S;
	private int C;
	private int N;
	
	private int bestR;
	private int linkMult;
	
	private int nOrig;
	
	private int totalRadix;
	
	private int diameterOpt;
	
	private double trafOpt;
	private int capOpt;
	private double distOpt;
	
	PairList<Integer, Integer> distanceHistogramOpt;	
	
	public AbstractModel(int N, long C) {
		this.N = N;
		this.linkMult = 1;
		this.C = (int)C;
		this.z = 1;
	}	
	
	public AbstractModel(int N, long C, int n, double z) {
		this.N = N;
		this.linkMult = n;
		this.C = (int)C;
		this.z = z;
	}
	
	public AbstractModel(long S, long C) {
		this.N = (int)(S*C);	
		this.S = (int)S;
		this.C = (int)C;
		this.linkMult = 1;	
		this.z = 1;
	}	
	
	
	
	public boolean runFindC() {
		nOrig = N;
		S = MoreMaths.ceilDiv(N, C);
		N = S*C;
		bestR = 0;
		capOpt = Integer.MAX_VALUE;
		trafOpt = Double.POSITIVE_INFINITY;
		distOpt = 0;
		diameterOpt = 0;
		distanceHistogramOpt = new PairList<>();
		for (int R = 2 ; R < 1000 ; R++) {
			PairList<Integer, Integer> distanceHistogram = new PairList<>();
		// finding D
			int s = S-1;
			int D = 1;
			double accum = 0;
			double temp;
			while ((temp = R*Math.pow(R-1, D-1)) < s) {
				D++;
				s -= temp;
				accum += temp*(D-1);
				distanceHistogram.add(D-1, (int)temp);
			}
			accum += s*D;
			distanceHistogram.add(D, s);
			int capacity = linkMult*S*R;
			double traf = z*N*C*accum/(double)(N-1);
			
			if (traf <= capacity) {
				if (capacity < capOpt) {
					distanceHistogramOpt = distanceHistogram;
					capOpt = capacity;
					trafOpt = traf;
					distOpt = C*accum/(double)(N-1);
					bestR = R;
					diameterOpt = D;
				}
			}
		}
		if (bestR == 0) return false;
		
		if (C + linkMult*bestR < 48) {
			double f = (C + linkMult*bestR)/8d;
			totalRadix = (int)Math.ceil(f)*8;
		} else {
			double f = (C + linkMult*bestR)/16d;
			totalRadix = (int)Math.ceil(f)*16;			
		}
		return true;
	}
		
	public void store(AbstractResultsManager man) {
		
		DataPoint dp = new DataPoint();
		
		dp.addProperty("z", z);
		dp.addProperty("S", S);
		dp.addProperty("Ropt", bestR);
		dp.addProperty("n_opt", linkMult);
		dp.addProperty("C", C);
		dp.addProperty("N", N);
		dp.addProperty("Norig", nOrig);
		dp.addResultProperty("r", C + linkMult*bestR);
		dp.addResultProperty("r_round", totalRadix);		
		dp.addResultProperty("Dopt", diameterOpt);
		dp.addResultProperty("traffic", trafOpt);
		dp.addResultProperty("capacity", capOpt);
		dp.addResultProperty("rounded capacity", MoreMaths.ceilDiv(capOpt, 50000)*50000);
		dp.addResultProperty("Rounded links per node", (double)MoreMaths.ceilDiv(capOpt*2, N)/2);
		dp.addResultProperty("ave. dist", distOpt);
		dp.addResultProperty("difference", capOpt - trafOpt);
		Execution ex = new Execution();
		ex.addDataPoint(dp);
		
		man.addExecution(ex);		
	}
	
	public PairList<Integer, Integer> getDistanceHistogram() {
		return distanceHistogramOpt;
	}
	
	public PairList<Integer, Integer> getNodeDistanceHistogram() {
		PairList<Integer, Integer> hist = new PairList<>();
		for (Pair<Integer, Integer> p : getDistanceHistogram()) {
			hist.add(p.getFirst()+2, p.getSecond()*C);
		}
		hist.add(2, C-1);
		return hist;
	}

	public double getAverageDistance() {
		return distOpt;
	}

	public int getNumberOfSwitches() {
		return S;
	}

	public int getRadix() {
		return totalRadix;
	}

	public int getUnroundedRadix() {
		return C + linkMult*bestR;
	}

	public int getOptCapacity() {
		return capOpt;
	}
	
	public double getOptTraffic(){
		return trafOpt;
	}

	public int getRRLinksPerSwitch() {
		return linkMult*bestR;
	}
	

}
