package edu.columbia.lrl.experiments.topology_radix.locality;


import java.util.Map;

import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;

public class LocalCenteredTrafficVectorGenerator extends
		AbstractNormalisedTrafficVectorGenerator {
	
	private boolean centered;
	private double halo;
	
	private double relativeLoad;
	
	public LocalCenteredTrafficVectorGenerator(double halo, boolean centered) {
		if (halo > 1) throw new IllegalStateException("Halo cannot be greater than 1");
		this.centered = centered;
		this.halo = halo;
	}
	
	@Override
	public Map<String, String> getTrafficVectorGeneratorsParameters() {
		return SimpleMap.getMap("Halo", halo+"", "Centered halo", centered+"");
	}

	@Override
	public double[] getVector(int index, int length) {
		double[] dd = new double[length];
		int reach;
		if (centered) {
			reach = (int)(halo *0.5 * (double)length);
		} else {
			reach = (int)(halo * (double)length);
		}
		
		for (int i = 0 ; i < reach ; i++) {
			dd[i] = 1;
			if (centered)
				dd[length - 1 - i] = 1;
		}
		dd[0] = 0;
		// TODO Auto-generated method stub
		return MoreArrays.normalize(dd, relativeLoad);
	}

	@Override
	public void setMaxLoad(double relativeLoad) {
		this.relativeLoad = relativeLoad;
	}

}
