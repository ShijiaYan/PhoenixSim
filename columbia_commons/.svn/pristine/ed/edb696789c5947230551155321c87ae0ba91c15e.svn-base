package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class RelativeToTotalInjectionBWLoad extends AbstractLoadScheme {
	
	private double load;
	private boolean addMoreInfo = false;
	
	public RelativeToTotalInjectionBWLoad(double load) {
		this.load = load;
	}
	
	public RelativeToTotalInjectionBWLoad(double load, 
			@ParamName(name="Add more info?", default_="false") boolean addMoreInfo) {
		this(load);
		this.addMoreInfo = addMoreInfo;
	}

	@Override
	public Rate getLoadPerClient(LWSIMExperiment lwSimExp) {
		return lwSimExp.getTotalInjectionBandwidth().multiply(load/lwSimExp.getNumberOfClients());
	}

	@Override
	public Map<? extends String, ? extends String> getAllParameters(LWSIMExperiment lwSimExp) {
		if (addMoreInfo) {
		return SimpleMap.getMap(
				"Relative load", load+"",
				"Raw load per client in Gb/s", "" + getLoadPerClient(lwSimExp).getInGbitSeconds(),
				"Total load in Gb/s", "" + lwSimExp.getTotalInjectionBandwidth().multiply(load).getInGbitSeconds());
		} else {
			return SimpleMap.getMap("Relative load", load+"");
		}
	}

}
