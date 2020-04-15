package edu.columbia.sebastien.data_center_consumption;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DCcomsumptionPart {
	
	private String name;
	private double initialPartUser;
	private double initialPartNorm;
	private double specificEffImp;
	private double specificGrowth;
	
	public DCcomsumptionPart(
			@ParamName(name="Consumption name") String name,
			@ParamName(name="Initial part") double initialPart,
			@ParamName(name="Additional energy efficiency improvement per year", default_="1") double eeImp,
			@ParamName(name="Yearly relative growth compared to baseline", default_="1") double intgrow) {
		
		this.name = name;
		this.initialPartUser = initialPart;
		this.specificEffImp = eeImp;
		this.specificGrowth = intgrow;
	}
	
	public void setInitialPart(double part) {
		initialPartNorm = part;
	}

	public double getUserInitialpart() {
		return initialPartUser;
	}

	public double getInitialpart() {
		return initialPartNorm;
	}

	public String getName() {
		return name;
	}

	public double getSpecEffGain() {
		return specificEffImp;
	}
	
	public double getSpecGrowth() {
		return specificGrowth;
	}

	public Map<String, String> getProperties() {
		return SimpleMap.getMap(name + "_additionalEff", specificEffImp+"", name + "_additionalGrow", specificGrowth+"");
	}
			

}
