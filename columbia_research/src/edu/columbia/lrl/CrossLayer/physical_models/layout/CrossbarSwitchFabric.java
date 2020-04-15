package edu.columbia.lrl.CrossLayer.physical_models.layout;

/*
public class CrossbarSwitchFabric extends AbstractSwitchFabric {

	PhysicalParameterAndModelsSet devices;
	
	public CrossbarSwitchFabric() {	}
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}
	
	@Override
	public void setSwitchRadix(int radix) {
		super.init(radix, new Crossbar(radix));		
	}

	@Override
	public int[][][][] getPaths() {
		return generator.getPossiblePaths();
	}

	@Override
	public int[][][][] getSwitches() {
		return generator.getSwitchPaths();
	}

	@Override
	public int[][][][] getSwitchIDs() {
		return generator.getSwitchIDs();
	}

	@Override
	public int[][][][] getSwitchStates() {
		return generator.getSwitchStates();
	}

	@Override
	public String toString() {
		return "Crossbar";
	}

	@Override
	public void init(DataManager dataManager, ResultManager resultManager, PhysicalParameterAndModelsSet devices) {
		this.devices = devices;		
	}

	@Override
	public LayoutProperties getLayoutProperties(PhysicalParameterAndModelsSet modelSet, int wavelengths) {
		
		double effectiveIndex = Constants.getSwitchRingEffectiveIndex();
		double crossingWidth = Constants.getWaveguideCrossingWidthCm();		
		double couplerLoss = modelSet.getCouplerLoss() * 2;
		
		//Evaluating worst case path through cross bar
		//	**** Assuming here that worst-case path is the one just inside the outer most path
		//  Eliminated self paths
		//  Eliminated outer switch and crossings on outside (reason why outer path is not worst-case)
		int numThrough = (switchRadix-3) * 2;
		int numDrop = 1;
		int numCrossings = (switchRadix-2) * 2;
		
		double switchLoss = modelSet.get1x2SwitchModel().getInsertionLossAndPowerPenalty(wavelengths, numThrough, numDrop);
		double crossingLoss = modelSet.getCrossingLoss() * numCrossings;
		
		//Calculate length of waveguide required
		double switchRingRadius = Constants.getSpeedOfLight() / (2*Math.PI*effectiveIndex*Constants.wavelengthsToChannelSpacing(wavelengths));
		double waveguideLength = 
				switchRingRadius * (numThrough + numDrop) + //through switches
				crossingWidth * numCrossings + //Here, interpreting crossing width as crossbar cell spacing
				Constants.getExtraWaveguideLength(); //some hard-to-predict amount of extra interconnect needed
		double waveguideLoss = modelSet.getWaveguideLoss() * waveguideLength;

		double linkPropLatencyNS = (waveguideLength)/(100*Constants.getSpeedOfLight())*1e9;
		// TODO remove jitterpenalty here
		double powerPenalty = couplerLoss + switchLoss + crossingLoss + waveguideLoss + modelSet.getJitterPenalty();

		LayoutProperties prop = new LayoutProperties(powerPenalty, linkPropLatencyNS);
		
		return prop;
	}

	@Override
	public List<PowerConsumption> getPowerConsumptions(PhysicalLayoutBasedLWSimExperiment phyExp) {
		List<PowerConsumption> p = phyExp.getPhysicalParameterAndModelsSet().get1x2SwitchModel().getDevicePowerConsumptions(phyExp);	
		for (PowerConsumption pc : p) {
			pc.multiply("Switch radix", switchRadix);
			pc.multiply("Switch stages", switchRadix * (switchRadix-1) - 1);
		}
		return p;
	}
}*/
