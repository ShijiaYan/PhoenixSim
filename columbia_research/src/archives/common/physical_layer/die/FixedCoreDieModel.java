package archives.common.physical_layer.die;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class FixedCoreDieModel extends AbstractDieModel {
	
	private double coreSizeInCm;
	
	public FixedCoreDieModel(@ParamName(name="Core edge size in cm", default_="0.2") double coreSizeInCm) {// 2mm
		this.coreSizeInCm = coreSizeInCm;
	}

	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Core edge size in cm", coreSizeInCm+"",
								"Die model", "Fixed Core");
	}
	
	public double getWaveguideLengthInCm() {
		return coreSizeInCm*gloco.getNumberOfCores() + coreSizeInCm*Math.sqrt(gloco.getNumberOfCores());
	}
}
