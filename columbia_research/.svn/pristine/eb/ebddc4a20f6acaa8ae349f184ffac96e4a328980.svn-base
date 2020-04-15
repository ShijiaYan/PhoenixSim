package edu.columbia.sebastien.green_optical.simple_trans;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleTransceiverModel extends AbstractOpticalModel {
	
private double slope;
private double offset;

private double pjAt10G;
	
	public SimpleTransceiverModel(@ParamName(name="10G pJ/bit", default_="9") double pjpbAt10G, 
			@ParamName(name="100G pJ/bit", default_="99")  double pjpbAt100G) {
		this.slope = (pjpbAt100G - pjpbAt10G)/90d;
		this.offset = pjpbAt100G - 100*slope;
		this.pjAt10G = pjpbAt10G;
		
	}

	@Override
	public double getPjPerBit(double rateInGbs) {
		if (rateInGbs < 10) return pjAt10G;
		return offset + slope*rateInGbs;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Transceiver model", "simple", "optics slope", slope+"", "optics offset", offset+"");
	}

}
