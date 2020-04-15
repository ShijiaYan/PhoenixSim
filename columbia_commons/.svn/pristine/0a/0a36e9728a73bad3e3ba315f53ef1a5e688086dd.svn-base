package edu.columbia.lrl.LWSim.traffic.deadlines;

import java.util.Map;
import java.util.Random;

import ch.epfl.general_libraries.clazzes.ParamName;

public class GaussianTTLGenerator extends AbstractTTLGenerator {

	private double sigma;
	
	public GaussianTTLGenerator(
			@ParamName(name = "mean") double mean, 
			@ParamName(name = "sigma") double sigma, 
			@ParamName(name = "min") int min) {
		super();
		this.mean = mean;
		this.sigma = sigma;
		this.min = min;
	}

	@Override
	public int nextTTL() {
		int tmp = (int)(mean + sigma * new Random().nextGaussian());
		if (tmp < min)
			return min;
		else 
			return tmp;
	}
	
	@Override
	public AbstractTTLGenerator getCopy() {
		return new GaussianTTLGenerator(this.mean, this.sigma, this.min);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("mean", mean+"");
		m.put("sigma", sigma+"");
		return m;
	}

	
	

}
