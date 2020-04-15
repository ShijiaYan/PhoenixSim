package edu.columbia.lrl.LWSim.traffic.deadlines;

import java.util.Map;
import java.util.Random;

import ch.epfl.general_libraries.clazzes.ParamName;

public class ExponentialTTLGernerator extends AbstractTTLGenerator {
	


	public ExponentialTTLGernerator(@ParamName(name = "mean") double mean) {
		this.mean = mean;
	}

	@Override
	public int nextTTL() {
		double u = new Random().nextDouble();
		double x = Math.log(1-u)*(-mean);
		return (int)x;
	}

	@Override
	public void setMean(double mean) {
			this.mean = mean;
	}

	@Override
	public AbstractTTLGenerator getCopy() {
		return new ExponentialTTLGernerator(this.mean);
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("mean", mean +"");
		return m;
	}

}
