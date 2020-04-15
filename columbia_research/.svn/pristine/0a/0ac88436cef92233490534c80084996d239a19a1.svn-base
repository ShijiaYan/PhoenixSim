package edu.columbia.sebastien.link_util.models.linkpowerreqmodel;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.link_util.models.AbstractLinkPowerRequirementModel;

public class SimplePowerRequiredModel extends AbstractLinkPowerRequirementModel {
	
	private double powerPerLane;
	private double basePower;
	
	public SimplePowerRequiredModel(@ParamName(name="Base optical power for coupling, etc. in mW", default_="10")  double basePower, 
									@ParamName(name="Optical power per lane in mW", default_="5")  double powerPerLane) {
		this.powerPerLane = powerPerLane;
		this.basePower = basePower;
	}

	@Override
	public double getRequiredOpticalPowerMW(int lanes) {
		return basePower + lanes*powerPerLane;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Base req. opt power", basePower+"", "Opt power per lane", powerPerLane+"");
	}

}
