package edu.columbia.sebastien;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ExascaleCostExperiment implements Experiment {
	
	private double budget;
	private  double priceFlop;
	private  double priceGbs;
	private  double maxVerb;
	private  AbstractEfficiencyModel effMod;
	
	public ExascaleCostExperiment(
			@ParamName(name="Total budget in M$") double budget,
			@ParamName(name="Price per GFlop ($)") double priceFlop,
	//		@ParamName(name="Price per Gb/s ($)") double priceGbs,
			@ParamName(name="Max verbosity (byte/flop)") double maxVerb,
			@ParamName(name="Efficiency model") AbstractEfficiencyModel effMod
			) {
		this.budget = budget*1e6;
		this.priceFlop = priceFlop/1e9d;
	//	this.priceGbs = priceGbs;
		this.priceGbs = -1;
		this.maxVerb = maxVerb;
		this.effMod = effMod;
		
	}
	
	public static abstract class AbstractEfficiencyModel {
		
		public abstract double getEfficiency(double maxVerb);
		public abstract Map<String, String> getAllParameters();
	}
	
	public static class ArcTanEfficiencyModel extends AbstractEfficiencyModel {
		
		private double maxVerbForHalfMax;
		private double max;
		
		public ArcTanEfficiencyModel(double maxVerbForHalfMax, double max) {
			this.maxVerbForHalfMax = maxVerbForHalfMax;
			this.max = max;
		}

		@Override
		public double getEfficiency(double maxVerb) {
			double ratio = maxVerb/maxVerbForHalfMax;
			double atan = Math.atan(ratio);
			double resp = atan/(Math.PI/2);
			resp = resp*max;
			return resp;
		}

		@Override
		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap("Eff mod type", "arctan", "max", max+"", "maxVerbForHalfMax", maxVerbForHalfMax+"");
		}
	}
	
	public static class LinearEfficiencyModel extends AbstractEfficiencyModel {
		
		private double maxVerbForMax;
		private double max;
		
		public LinearEfficiencyModel(double maxVerbForMax, double max) {
			this.maxVerbForMax = maxVerbForMax;
			this.max = max;
		}

		@Override
		public double getEfficiency(double maxVerb) {
			if (maxVerb > maxVerbForMax) return max;
			if (maxVerb < 0) throw new WrongExperimentException("Max verb cannot be negative");
			return (maxVerb/maxVerbForMax)*max;
		}

		@Override
		public Map<String, String> getAllParameters() {
			return SimpleMap.getMap("Eff mod type", "linear", "max", max+"", "maxVerbForMax", maxVerbForMax+"");
		}
	}	
	
	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		if (priceGbs == -1) {
			solveForPriceGbs(man);
		}
	}

	private void solveForPriceGbs(AbstractResultsManager man) {
		double efficiency = effMod.getEfficiency(maxVerb);
		
		double flopEffective = 1e18d/efficiency;
		
		DataPoint dp = new DataPoint();
		
		dp.addResultProperty("Efficiency", efficiency);
		
		dp.addProperty("Total budget (M$)", budget);
		dp.addProperty("Price per GFlop/s ($)", priceFlop);
		dp.addProperty("Max verbpsity supported by interconnect (byte/Flop)", maxVerb);
		dp.addProperties(effMod.getAllParameters());
		
		dp.addResultProperty("Flop effective (EF)", flopEffective/1e18d);
		
		double costFlops = flopEffective * priceFlop;
		
		if (costFlops <= budget) {
		
			dp.addResultProperty("cost of flops", costFlops);
			
			double costNetwork = budget - costFlops;
			
			dp.addResultProperty("cost of network", costNetwork);
			dp.addResultProperty("network cost ratio", costNetwork/budget);
			
			double bandwidthEffective = flopEffective*maxVerb;
			
			dp.addResultProperty("bandwidth effective Eb/s", bandwidthEffective*8/1e18d);
			
			double resultingPricePerGb = costNetwork/(bandwidthEffective*8/1e9d);
			
			dp.addResultProperty("Price per Gb/s", resultingPricePerGb);
			
			double numerator = budget*efficiency - 1e18d*priceFlop;
			double denom = 1e18d*maxVerb;
			
			double priceGbs = numerator/denom;
			
			man.addDataPoint(dp);
		}
		
		
	}

}
