package archives.common.physical_layer.general;

import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;

public class ButterflyClosWaveguideCrossingModel extends AbstractWaveguideCrossingModel {

	@Override
	public int getNumberOfCrossings(int radix) {
		
		//Crossings between stages only!
		return (int) (radix - Math.log(radix)/Math.log(2) - 1);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		return map;
	}

}
