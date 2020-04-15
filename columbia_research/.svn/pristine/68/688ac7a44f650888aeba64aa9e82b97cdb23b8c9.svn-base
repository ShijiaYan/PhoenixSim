package edu.columbia.sebastien.green_optical.elecSERDES;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class LinearSERDESModel extends AbstractSERDESModel {
	
	private double baseConsumptionInPjB;
	private double pJPerGbs;
	
	public LinearSERDESModel (@ParamName(name="Base in pJ/bit", default_="1.496") double baseConsumptionInPjB, 
			@ParamName(name="Addi in pJ/bit/Gb/s", default_="0.189") double pJbPerGbs) {
		this.baseConsumptionInPjB = baseConsumptionInPjB;
		this.pJPerGbs = pJbPerGbs;
	}

	@Override
	public double getPjPerBit(double rateInGbs) {
		return baseConsumptionInPjB + rateInGbs * pJPerGbs;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Serdes model", "linear", "Base serdes", baseConsumptionInPjB+"", "Serdes slope", pJPerGbs+"");
	}

}
