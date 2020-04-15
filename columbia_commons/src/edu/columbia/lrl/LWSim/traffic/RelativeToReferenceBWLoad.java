package edu.columbia.lrl.LWSim.traffic;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

public class RelativeToReferenceBWLoad extends AbstractLoadScheme {
	
//	private Rate nominalRate;
	private double normalisedLoad;

	public RelativeToReferenceBWLoad(
		//	@ParamName(name="Reference per client link rate", defaultClass_=Rate.GigabitPerS.class) Rate nominalRate, 
			@ParamName(name="Normalised load", default_="0.1:0.1:1") double normalisedLoad) {
		
	//	this.nominalRate = nominalRate;
		this.normalisedLoad = normalisedLoad;
		
	}
	
	private double getNormalisedLoad() {
		return normalisedLoad;
	}	
		
	public Rate getLoadPerClient(LWSIMExperiment lwSimExp) {
		return lwSimExp.getReferenceBandwidth().multiply(normalisedLoad);
	}
	
/*	public Rate getNominalLinkRate() {
		return nominalRate;
	}*/
	
	public Map<? extends String, ? extends String> getAllParameters(LWSIMExperiment lwSimExp) {
		return SimpleMap.getMap("Normalised load", "" + getNormalisedLoad(),
				                "Raw load per client in Gb/s", "" + getLoadPerClient(lwSimExp).getInGbitSeconds());
		
	}	
}
