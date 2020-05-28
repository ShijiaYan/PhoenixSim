//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk;

import java.util.Map;
import ch.epfl.general_libraries.utils.SimpleMap;


public class AlternativeXtalkPPModel extends AbstractXtalkPPModel {

	public AlternativeXtalkPPModel() {
	}

	public double getXtalkPP_DB(double xtalkPower, int berIndex, double modulationER) {
		return -10.0D * Math.log10(1.0D - 2.0D * Math.sqrt(xtalkPower));
	}

	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}
}
