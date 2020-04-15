package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class AbsolutePerClientLoad extends AbstractLoadScheme {
	
	private Rate genRate;
	private double load = 1;

	public AbsolutePerClientLoad(
			@ParamName(name="Per client rate") Rate genRate,
			@ParamName(name="Coefficient") double load) {
		this.genRate = genRate;
		this.load = load;
	}
	
	public AbsolutePerClientLoad(
			@ParamName(name="Generated rate") Rate genRate) {
		this.genRate = genRate;
	}	


	@Override
	public Rate getLoadPerClient(LWSIMExperiment lwSimExp) {
		return genRate.multiply(load);
	}

	
	public Map<? extends String, ? extends String> getAllParameters(LWSIMExperiment lwSimExp) {
		return SimpleMap.getMap("Raw load per client in Gb/s", "" + getLoadPerClient(lwSimExp).getInGbitSeconds());
	}	

}
