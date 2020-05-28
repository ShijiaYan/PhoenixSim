package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.Map;

public abstract class AbstractSwitchFabric extends AbstractPhysicalLayout {

	int switchRadix = -1;
	
	public void setSwitchRadix(int radix) {
		if (switchRadix > 0) {
			System.out.println("Switch radix replaced, was " + switchRadix + ", will be " + radix);
		}
		this.switchRadix = radix;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getSwitchParameters();
		m.put("Switch radix", switchRadix+"");
		return m;
	}

	public abstract Map<String, String> getSwitchParameters();
	public abstract int getNumberOfStages();
	public abstract int getNumberOfLinksInvolved();
	public abstract int getNumberOfSwitches();
	public abstract int[][][][] getPaths();
//	public abstract int[][][][] getSwitches();
//	public abstract int[][][][] getSwitchIDs();
//	public abstract int[][][][] getSwitchStates();

}
