package archives.common.physical_layer.loss_models;

import java.util.Map;

import archives.common.physical_layer.GlobalConstantSet;


import ch.epfl.general_libraries.traffic.Rate;

public abstract class AbstractExtinctionRatioModel {
	
	public abstract void getExtinctionRatioPowerPenalty(int sites_per_branch,GlobalConstantSet gloco, PscanResult result);
	public abstract double getRateRelatedExtinctionRatio(Rate rate);
	
	public abstract Map<String, String> getAllParameters();
}
