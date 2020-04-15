package edu.columbia.lrl.LWSim.traffic.deadlines;

import java.util.Map;
import java.util.Random;

public class UniformTTLGenerator extends AbstractTTLGenerator {

	private int max;
	
	public UniformTTLGenerator(int min, int max) {
		super();
		this.min = min;
		this.max = max;
		this.mean = (min + max)/(double)2;
	}

	@Override
	public int nextTTL() {
		return (int) (min + (max - min) * new Random().nextDouble());
	}

	@Override
	public AbstractTTLGenerator getCopy() {
		return new UniformTTLGenerator(this.min, this.max);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = super.getAllParameters();
		m.put("mean", this.mean +"");
		return m;
	}
	
}
