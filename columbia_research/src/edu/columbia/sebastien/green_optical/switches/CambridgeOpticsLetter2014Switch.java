package edu.columbia.sebastien.green_optical.switches;

import java.util.Map;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.green_optical.AbstractOpticalSwitchModel;

public class CambridgeOpticsLetter2014Switch extends AbstractOpticalSwitchModel {
	
	private boolean benes;
	
	public CambridgeOpticsLetter2014Switch(boolean benes) {
		this.benes = benes;
	}
	
	private int getStages(int radix) {
		if (benes) {
			return (int)(2*Math.ceil(MoreMaths.log2(radix)) + 1);
		} else {
			return (int)Math.ceil(MoreMaths.log2(radix));
		}
		
	}

	@Override
	public double getPowerPenalty(int radix, double rate, int nbWavelengths) {
		int stages = getStages(radix);
		if (stages == 1) {
			return 0.1;
		} else if (stages == 2) {
			return 0.2;
		} else if (stages == 3) {
			return 0.5;
		} else if (stages == 4 ){
			return 0.8;
		} else {
			return 1.4 + (stages - 5)*0.5;
		}			
	}

	@Override
	public double getSwitchConsumption(int radix) {
		return (double)getStages(radix) * (double)radix/2d * 8d * 20d;
	}

	@Override
	protected Map<String, String> getSwitchProperties() {
		return SimpleMap.getMap("use as benes", benes+"");
	}

}
