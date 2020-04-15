package edu.columbia.sebastien.green_optical.EPS;

import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.Rounder;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Property;
import ch.epfl.general_libraries.utils.SimpleMap;

public class FittingExperiment implements Experiment {
	
	private SubModel mod;

	
	private double alpha;
	private double beta;
	private int maxpin;
	
	private int r;
	private double B;
	private double maxWatts = 132;
	
	public FittingExperiment(SubModel mod) {
		this.mod = mod;
	}
	
	public FittingExperiment(SubModel mod,  
			@ParamName(name="Maxpin", default_="1280") int maxPin, 
			@ParamName(name="Alpha (in pJ/bit/Gb/s)", default_="0.189") double alpha, 
			@ParamName(name="Beta (base, in pJ/bit)", default_="1.496") double beta, 
			@ParamName(name="Ignore (badnwidth)", default_="-1") double B, 
			@ParamName(name="Ignore (radix)", default_="-1") int r) {
		this.mod = mod;

		
		this.maxpin = maxPin;
		this.alpha = alpha;
		this.beta = beta;
		this.r = r;
		this.B = B;
	}	
	
	public static abstract class SubModel {
		public abstract double calculate(int r, double B);

		public abstract Map<String, String> getAllProperties();
	}
	
	public static class Quad1 extends SubModel {
		
		public double a;
		public double b;
		public double c;	
		
		public Quad1(@ParamName(name="Base in W", default_="50.68")  double a_, 
				@ParamName(name="Compl in W/Gb/s", default_="0.00815") double b_, 
				@ParamName(name="Compl in W/port^2", default_="0") double c_) {
			a = a_;
			b = b_;
			c = c_;		
		}

		@Override
		public double calculate(int r, double B) {
			return a + b*(double)r*B + c*(double)r*(double)r;
		}

		@Override
		public Map<String, String> getAllProperties() {
			SimpleMap<String, String> dp = new SimpleMap<String, String>();
			dp.put("a", a+"");
			dp.put("b", b+"");
			dp.put("c", c+"");
			dp.put("model", this.getClass().getSimpleName());
			return dp;
		}
		
	}
	
	public static class Quad2 extends SubModel {

		@Override
		public double calculate(int r, double B) {
			double a = -1;
			double b = -1;
			double c = -1;
			return a + b*(double)r*B/1000d + (c/1000d)*(double)r*(double)r*B;
		}

		@Override
		public Map<String, String> getAllProperties() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	double[] corePowers = {54.06,
	                       66.95,
	                       68.80,
	                       68.29,
	                       53.38,
	                       84.26,
	                       96.42};
	
	int[] radixes = {32,
			18,
			36,
			36,
			36,
			24,
			48};
	
	double[] rates = {40,
			56,
			56,
			100,
			40,
			100,
			100};


	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		calculate(B, B, r, 0, man, new DataPoint());
	}
	
	private double calculateSerDes(double NRRateInjBW, double rrRate, int concentration, int switchR, int i, DataPoint dp) {
		int pinPerPortNR = i;
		int pinperPortRR = (int)((maxpin/4) - (i*concentration))/switchR;
		if (pinperPortRR <= 1) {
			return -2;
		}
		if (pinPerPortNR < 0) {
			System.out.println("uiu");
		}
		
		double ratePerPinNR = NRRateInjBW/pinPerPortNR;
		double ratePerPinRR = rrRate/pinperPortRR;
		
		double serDesEffNR = beta + alpha*ratePerPinNR;
		double serDesEffRR = beta + alpha*ratePerPinRR;
		
		double serDesPowerNR = serDesEffNR * concentration * NRRateInjBW;
		double serDesPowerRR = serDesEffRR * switchR * rrRate;
		
		double total = serDesPowerNR + serDesPowerRR;
		if (dp != null) {
			dp.addResultProperty("bestI", i);	
			dp.addResultProperty("serDesPowerNR", serDesPowerNR);
			dp.addResultProperty("serDesPowerRR", serDesPowerRR);
			dp.addResultProperty("rate per pin NR", ratePerPinNR);
			dp.addResultProperty("pin used", concentration*pinPerPortNR + pinperPortRR*switchR);
			dp.addResultProperty("serDesEffNR", serDesEffNR);
			dp.addResultProperty("pinPerPortNR", pinPerPortNR);
			
		}
		return total;
	}

	public double[] calculate(double NRRateInjBW, double rrRate, int concentration, int switchR, AbstractResultsManager man,
			DataPoint dpOrig) {
		DataPoint dp = dpOrig.getDerivedDataPoint();
		dp.addProperties(mod.getAllProperties());

		double gloEff = -1;
		boolean feasible = true;
		int radix = concentration + switchR;
		
		double averagePortRateInGbs = (NRRateInjBW*(double)concentration + rrRate*(double)switchR)/(double)radix;
		
		double pinQuadsPerPortNR = 0;
		double pinQuadsPerPortRR = 0;
		
		if (maxpin > 0) {
			dp.addProperty("r", radix);
			dp.addProperty("alpha", alpha);
			dp.addProperty("beta", beta);
			dp.addProperty("maxPin", maxpin);
			dp.addProperty("max watts", maxWatts);
			dp.addProperty("Average B", averagePortRateInGbs);
			

			

			double serdesPower;
			if (NRRateInjBW == rrRate) {
				double pinPerPort = (maxpin/4)/radix;
				pinQuadsPerPortNR = pinPerPort;
				pinQuadsPerPortRR = pinPerPort;
				dp.addResultProperty("Pin per port", pinPerPort);
				double ratePerPin = averagePortRateInGbs/pinPerPort;
				dp.addResultProperty("rate per pin (Gb/s)", ratePerPin);
				double serDesEff = beta + alpha*ratePerPin;
				dp.addResultProperty("Serdes efficiency (pJ/bit)", serDesEff);
				serdesPower = serDesEff*radix*averagePortRateInGbs;
			} else {
				double min = Double.MAX_VALUE;
				int bestI = -1;
				
				// finding best splitting of pins
				// start by allocate only one pin per NR link, then grow
				for (int i = 1 ; i < (maxpin/4) - switchR ; i++) {
					double total = calculateSerDes(NRRateInjBW, rrRate, concentration, switchR, i, null);
					if (total == -2) break;
					
					if (total < min) {
						min = total;
						bestI = i;
					}
				}				
				if (bestI == -1)  throw new WrongExperimentException();
				pinQuadsPerPortNR = bestI;
				pinQuadsPerPortRR = (maxpin/4) - bestI;
				serdesPower = calculateSerDes(NRRateInjBW, rrRate, concentration, switchR, bestI, dp);
			}
			dp.addResultProperty("Serdes power (mW)", serdesPower);
			double corePower = mod.calculate(radix, averagePortRateInGbs);
			dp.addResultProperty("Core power (W)", corePower);
			double totalPowre = corePower + serdesPower*0.001d;
			dp.addResultProperty("Total power (W)", totalPowre);
			double eff = 1000d * totalPowre / (NRRateInjBW*(double)concentration + rrRate*(double)switchR);
			dp.addResultProperty("Switch chip energy eff (pJ/bit)", eff);
			gloEff = 1000d * totalPowre / (0.7*averagePortRateInGbs*(double)radix);
			dp.addResultProperty("Global switch energy eff (pJ/bit)", gloEff);		
			dp.addResultProperty("Total bandwidth (Tb/s)", averagePortRateInGbs*radix/1000d);
			
			if (totalPowre < 132) {
				dp.addProperty("feasible", true);
				dp.addResultProperty("Portion of core", Rounder.round(corePower/totalPowre, 2));
			} else {
				dp.addProperty("feasible", false);
				feasible = false;
			}
			
		} else {
			
			double max = 0;
			double sum = 0;
			for (int i = 0 ; i < corePowers.length ; i++) {
				double perc = mod.calculate(radixes[i], rates[i]);
				perc = Math.abs(perc - corePowers[i])/corePowers[i];
				if (perc > max) {
					max = perc;	
				}
				perc *= perc;
				sum += perc;
			}
			
			dp.addResultProperty("max dev", max);
			dp.addResultProperty("sum dev", sum);
		}
		
		
		man.addDataPoint(dp);
		if (feasible) {
			return new double[]{gloEff, pinQuadsPerPortNR, pinQuadsPerPortRR};
		} else {
			throw new WrongExperimentException();
		}
	}

	public Map<String, String> getAllProperties() {
		
		Map<String, String> m = mod.getAllProperties();
		
		m.put("alpha", alpha+"");
		m.put("beta", beta+"");
		m.put("maxPin", maxpin+"");
		m.put("max watts", maxWatts+"");
		return m;
	}

}
