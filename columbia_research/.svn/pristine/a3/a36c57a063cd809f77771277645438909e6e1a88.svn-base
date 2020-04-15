package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;


public class SingleSwitchNetworkLayout extends PhysicalLayout {


	PhysicalParameterAndModelsSet devices;
	AbstractSwitchFabric switchFabric;
	PhysicalLayout pni;
	
	public SingleSwitchNetworkLayout(
			@ParamName(name="Switch fabric") AbstractSwitchFabric switchFabric,
			@ParamName(name="Photonic interface") PhysicalLayout pni
			) {
		this.switchFabric = switchFabric;
		this.pni = pni;
	}
	
	@Override
	public double getUnavailabilityTime() {
		throw new IllegalStateException("To be defined");
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = switchFabric.getAllParameters();
		map.putAll(pni.getAllParameters());
		return map;
	}
		
	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(
			Execution ex, 
			PhysicalParameterAndModelsSet modelSet, 
			AbstractLinkFormat linkFormat) {

		
		LayoutWorseCaseProperties switchProperties = switchFabric.getLayoutPropertiesForaGivenNumberOfWavelengths(ex, modelSet, linkFormat);
		LayoutWorseCaseProperties pniProperties = pni.getLayoutPropertiesForaGivenNumberOfWavelengths(ex, modelSet, linkFormat);
		
		LayoutWorseCaseProperties switchProp = new LayoutWorseCaseProperties(switchProperties, pniProperties);
		
		return switchProp;
	}
	
	public String toString() {
		return "SwitchedNetworkLayout";
	}

	@Override
	public List<PowerConsumption> getLayoutSpecificConsumption(
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		List<PowerConsumption> p = switchFabric.getPowerConsumptions(modelSet, linkFormat, true);
		List<PowerConsumption> interface_ = pni.getPowerConsumptions(modelSet, linkFormat, true);
		p.addAll(interface_);
		return p;
	}

/*	@Override
	public Map<String, Double> getPowerConsumption(PhysicalLayoutBasedLWSimExperiment phyExp, double utilization) {
		Map<String, Double> fabricConsumption = switchFabric.getPowerConsumption(phyExp, utilization);
		Map<String, Double> pniConsumption = Utils.multiplyPowers(pni.getPowerConsumption(phyExp, utilization), phyExp.getNumberOfClients());
		fabricConsumption.putAll(pniConsumption);
		return fabricConsumption;
	}*/

}
