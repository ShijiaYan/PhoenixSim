package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;

public class NumberOfWavelengthProportionalPowerPenaltyLayout extends AbstractPhysicalLayout {

	double loss;
	double passiveRingLoss;
	
	public NumberOfWavelengthProportionalPowerPenaltyLayout(
			@ParamName(name="Loss (dB)") double loss,
			@ParamName(name="Passive ring loss (dB)") double passiveRingLoss 
			) {
		this.loss = loss;
		this.passiveRingLoss = passiveRingLoss;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Loss (dB)", loss+"",
								"Passive ring loss (dB)", passiveRingLoss+"");
	}

	@Override
	public String toString() {
		return "NumberOfWavelengthProportionalPowerPenaltyLayout";
	}

	
	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(
			Execution ex,
			PhysicalParameterAndModelsSet modelSet, 
			AbstractLinkFormat linkFormat) {
		LayoutWorseCaseProperties prop = new LayoutWorseCaseProperties(linkFormat.getNumberOfChannels());
		prop.addPowerPenalty(new PowerPenalty("WDM independent penalty", "global", loss));
		prop.addPowerPenalty(new PowerPenalty("WDM dependent penalty", "global",passiveRingLoss).multiply(linkFormat.getNumberOfChannels()));
		return prop;
	}

	@Override
	public List<PowerConsumption> getLayoutSpecificConsumption(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		return new ArrayList<>(0);
	}

	@Override
	public double getUnavailabilityTime() {
		throw new IllegalStateException("Not meant to be used this way");
	}



	
	
}
