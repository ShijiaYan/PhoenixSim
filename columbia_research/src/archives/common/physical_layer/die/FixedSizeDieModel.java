package archives.common.physical_layer.die;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class FixedSizeDieModel extends AbstractDieModel {
	
	private double sizeInCm;

	
	public FixedSizeDieModel(@ParamName(name="Die size in cm", default_="1") double sizeInCm) {
		this.sizeInCm = sizeInCm;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Die size in cm", sizeInCm+"",
								"Die model", "Fixed Die");
	}
	
	public double getWaveguideLengthInCm() {
		return Math.sqrt(gloco.getNumberOfCores())*sizeInCm;
	}
}
