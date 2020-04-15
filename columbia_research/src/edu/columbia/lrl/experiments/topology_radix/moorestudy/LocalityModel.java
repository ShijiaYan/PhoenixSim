package edu.columbia.lrl.experiments.topology_radix.moorestudy;

import java.util.Arrays;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;

public class LocalityModel {
	
	private double param;
	
	public LocalityModel(double param) {
		this.param = param;
	}
	
	public double[] calculate(boolean withoutLocal, int bins) {
		if (withoutLocal) {
			double[] temp = calculate(bins-1);
			double[] ret = new double[bins];
			System.arraycopy(temp, 0, ret, 1, bins-1);
			return ret;
		} else {
			return calculate(bins);
		}
	}
	
	public double[] calculate(int bins) {
		double[] ret = new double[bins];
		if (param == 0) {
			Arrays.fill(ret, 1d/bins);
		} else if (param <= -2) {
			ret[0] = 1;
		} else if (param >= 2) {
			ret[bins-1] = 1;
		} else if (param < -1) {
			return veryLocal(ret);
		} else if (param < 0) {
			return local(ret);
		} else if (param <= 1) {
			return distant(ret);
		} else {
			return veryDistant(ret);
		}
		return ret;
	}

	private double[] veryDistant(double[] ret) {
		double p = 2 - param;
//		double nbOfFilledBin = (ret.length) * p;
//		double slope = nbOfFilledBin - 0.5;
		double nbOfFilledBin = Math.max(1+p,(ret.length-1) * p);
		double slope = nbOfFilledBin;		
		for (int i = 0 ; i < Math.ceil(nbOfFilledBin) ; i++) {
			ret[ret.length-i-1] = Math.max(0,1 - (double)i/slope);
		}
		ret = MoreArrays.normalize(ret);
		return ret;
	}
	
	private double[] veryLocal(double[] ret) {
		double p = 1 - (-param - 1);
		double nbOfFilledBin = Math.max(1+p,(ret.length-1) * p);
		double slope = nbOfFilledBin;
		for (int i = 0 ; i < Math.ceil(nbOfFilledBin) ; i++) {
			ret[i] = Math.max(0,1 - (double)i/slope);
		}
		ret = MoreArrays.normalize(ret);
		return ret;
	}	

	private double[] distant(double[] ret) {
		double slope = (double)(ret.length-1) / (param);
		for (int i = 0 ; i < ret.length ; i++) {
			ret[ret.length-i-1] = Math.max(0,1 - (double)i/slope);
		}
		ret = MoreArrays.normalize(ret);
		return ret;
	}

	private double[] local(double[] ret) {
		double slope = (double)(ret.length-1) / (-param);
		for (int i = 0 ; i < ret.length ; i++) {
			ret[i] = Math.max(0,1 - (double)i/slope);
		}
		ret = MoreArrays.normalize(ret);
		return ret;
	}


	
	/* Other version
	private double[] veryLocal(double[] ret) {
		double p = 1 - (-param - 1);
		double nbOfFilledBin = Math.max(2,(ret.length) * p);
		double slope = nbOfFilledBin - 0.5;
		for (int i = 0 ; i < Math.max(2,Math.round(nbOfFilledBin)) ; i++) {
			ret[i] = Math.max(0,1 - (double)i/slope);
		}
		ret = MoreArrays.normalize(ret);
		return ret;
	}	*/	
	
	public static  class TestExperiment implements Experiment {
		
		LocalityModel m;
		int size;
		
		public TestExperiment(LocalityModel m, int size) {
			this.m = m;
			this.size = size;
		}

		@Override
		public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
				throws WrongExperimentException {
			Execution e = new Execution();
			
			DataPoint p = new DataPoint();
			p.addProperty("locality param", m.param);
			p.addProperty("size", size);
			e.addArrayResult("val", m.calculate(size), p);
			
			man.addExecution(e);
			
		}
		
	}

	public double getLocality() {
		return param;
	}

}
