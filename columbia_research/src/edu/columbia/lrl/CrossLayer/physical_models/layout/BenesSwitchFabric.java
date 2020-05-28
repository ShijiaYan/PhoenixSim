//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.devices.switches.Abstract2x2Switch;
import edu.columbia.lrl.CrossLayer.physical_models.devices.switches.AbstractRingBasedSwitch;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;
import edu.columbia.lrl.switch_arch.BenesSwitchGenerator;


public class BenesSwitchFabric extends AbstractSwitchFabric {

	double extraWaveguideLength;
	double crossingWidth;
	BenesSwitchGenerator archGenerator;
	AbstractSwitchFabric switch1x2;
	Abstract2x2Switch switch2x2;

	public BenesSwitchFabric(AbstractRingBasedSwitch switch1x2, Abstract2x2Switch switch2x2,
			@ParamName(name = "Extra waveguide length (cm)", default_ = ".001") double extraWaveguideLength,
			@ParamName(name = "Crossing width (cm)", default_ = ".001") double crossingWidth,
			@ParamName(name = "Use multi-chip optimization", default_ = "false") boolean useMultichipOptimization) {
		this.extraWaveguideLength = extraWaveguideLength;
		this.crossingWidth = crossingWidth;
	}

	public BenesSwitchFabric(AbstractRingBasedSwitch switch1x2, Abstract2x2Switch switch2x2,
			@ParamName(name = "Extra waveguide length (cm)", default_ = ".001") double extraWaveguideLength,
			@ParamName(name = "Crossing width (cm)", default_ = ".001") double crossingWidth,
			@ParamName(name = "Use multi-chip optimization", default_ = "false") boolean useMultichipOptimization,
			int radix) {
		this(switch1x2, switch2x2, extraWaveguideLength, crossingWidth, useMultichipOptimization);
		super.setSwitchRadix(radix);
	}

	public Map<String, String> getSwitchParameters() {
		return SimpleMap.getMap("Extra waveguide length (cm)", String.valueOf(this.extraWaveguideLength),
				"Crossing width (cm)", String.valueOf(this.crossingWidth));
	}

	public String toString() {
		return "BenesSwitchFabric";
	}

	public void setSwitchRadix(int radix) {
		super.setSwitchRadix(radix);
		this.archGenerator = new BenesSwitchGenerator(radix);
	}

	public double getUnavailabilityTime() {
		throw new IllegalStateException("Not meant to be used this way");
	}

	public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(Execution ex,
			PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		int numStages = this.getNumberOfStages();
		LayoutWorseCaseProperties prop = new LayoutWorseCaseProperties(linkFormat.getNumberOfChannels());
		prop.addPowerPenalty(modelSet.getCouplerPenalty().multiply(2));
		prop.addPowerPenalty(this.get2x2SwitchModel().getPowerPenalty(linkFormat, numStages));
		int numCrossings = (int) ((double) (super.switchRadix * 2) - (MoreMaths.log2(this.switchRadix) + 1.0D) * 2.0D);
		prop.addPowerPenalty(modelSet.getCrossingPenalty().multiply(numCrossings));
		double switchElementSize = this.get2x2SwitchModel().getSize() / 100.0D;
		double switchArrayHeight = Math.max(switchElementSize * (double) this.switchRadix / 2.0D,
				(double) (this.switchRadix / 2 - 1) * this.crossingWidth);
		double switchArrayWidth = switchElementSize * (double) numStages;
		double worstCaseDist = 2.0D
				* Math.sqrt(Math.pow(switchArrayHeight, 2.0D) + Math.pow(switchArrayWidth / 2.0D, 2.0D));
		prop.addPowerPenalty(modelSet.getWaveguidePenalty().multiply(worstCaseDist));
		double linkPropLatencyNS = worstCaseDist / (100.0D * modelSet.getConstants().getSpeedOfLight()) * 1.0E9D;
		prop.setLinkLatency(linkPropLatencyNS);
		return prop;
	}

	public int getNumberOfStages() {
		if (this.switchRadix <= 0) {
			throw new IllegalStateException(
					"Radix of benes switch not provided. Should be fixed by builder or given as parameter");
		} else {
			return 2 * (int) Math.ceil(Math.log(this.switchRadix) / Math.log(2.0D)) - 1;
		}
	}

	public List<PowerConsumption> getLayoutSpecificConsumption(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) {
		List<PowerConsumption> p = this.get2x2SwitchModel().getDevicePowerConsumptions(modelSet, linkFormat);

		for (PowerConsumption pc : p) {
			pc.multiply("Switch radix", this.switchRadix);
			pc.multiply("Switch stages", this.getNumberOfStages());
		}

		return p;
	}

	public AbstractSwitchFabric get1x2SwitchModel() {
		return this.switch1x2;
	}

	public Abstract2x2Switch get2x2SwitchModel() {
		return this.switch2x2;
	}

	public int getNumberOfLinksInvolved() {
		return this.archGenerator.getNumberOfLinksInvolved();
	}

	public int[][][][] getPaths() {
		return this.archGenerator.getPossiblePaths();
	}

	public int getNumberOfSwitches() {
		return this.getNumberOfStages() * this.switchRadix / 2;
	}
}
