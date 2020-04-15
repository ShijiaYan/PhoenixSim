package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class AbsoluteTotalLoad extends AbstractLoadScheme {
	
	private Rate totalGenRate;
	
	public AbsoluteTotalLoad(
			@ParamName(name="Total rate") Rate genRate,
			@ParamName(name="Coefficient") double coeff) {
		this.totalGenRate = genRate.multiply(coeff);
	}
	
	public AbsoluteTotalLoad(
			@ParamName(name="Total rate") Rate genRate) {
		this.totalGenRate = genRate;
	}	
	
	@Override
	public Rate getLoadPerClient(LWSIMExperiment lwSimExp) {
		return totalGenRate.divide(lwSimExp.getNumberOfClients());
	}

	@Override
	public Map<? extends String, ? extends String> getAllParameters(LWSIMExperiment lwSimExp) {
		return SimpleMap.getMap("Raw load per client in Gb/s", "" + getLoadPerClient(lwSimExp).getInGbitSeconds(),
				"Total load in Gb/s", totalGenRate.getInGbitSeconds()+"");
	}

}
