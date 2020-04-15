package edu.columbia.lrl.LWSim.traffic.sizes;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractPacketSizeGenerator implements ExperimentBlock {

	public abstract int getSize(PRNStream stream);
	public abstract int getAverageSize();
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("packet size", getAverageSize()+"", "size gen type", this.getClass().getSimpleName());
	}
 
}
