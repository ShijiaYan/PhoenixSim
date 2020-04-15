package edu.columbia.sebastien.link_util.models.linkconsumptionmodel;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.sebastien.link_util.models.AbstractSiPLinkConsumptionModel;

public class SimpleLinkConsumptionModel extends AbstractSiPLinkConsumptionModel {
	
	private double basePassiveConsumption;
	private double consumptionPerLanePassive;
	private double consumptionPerLaneActive;
	
	public SimpleLinkConsumptionModel(@ParamName(name="Base passive consumption (in mW)", default_="1")  double basePassiveConsumption, 
									  @ParamName(name="Per lane passive consumption (in mW) (e.g. receivers)", default_="4.825") double consumptionPerLanePassive,
									  @ParamName(name="Per lane active consumption (in mW) (e.g. modulators)", default_="1.35") double consumptionPerLaneActive) {
		this.basePassiveConsumption = basePassiveConsumption;
		this.consumptionPerLanePassive = consumptionPerLanePassive;
		this.consumptionPerLaneActive = consumptionPerLaneActive;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("base consumption", basePassiveConsumption + "", 
								"perLanePassiveConsumption", consumptionPerLanePassive + "", 
								"perLaneActiveConsumption", consumptionPerLaneActive+"");
	}

	@Override
	public double getJoules(double totalTime, int lanes) {
		//                  s 10^-9                w 10^-3    = J 10^-12 = pJ
		double picoJoules = totalTime *           basePassiveConsumption;
		picoJoules       += totalTime *          (consumptionPerLanePassive * lanes);
	//	picoJoules       += transmissionTimeNS * (consumptionPerLaneActive * lanes);
		return picoJoules * 1e-12;
	}

}
