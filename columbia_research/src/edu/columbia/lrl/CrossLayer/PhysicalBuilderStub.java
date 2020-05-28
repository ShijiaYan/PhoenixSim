package edu.columbia.lrl.CrossLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractPhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.LayoutException;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;
import edu.columbia.lrl.CrossLayer.simulator.phy_builders.PhyWrapper;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.builders.AbstractBandwidthCalculatedNBClientBuilder;
import edu.columbia.lrl.LWSim.builders.AbstractBandwidthSpecifiedNBClientBuilder;
import edu.columbia.lrl.LWSim.builders.NumberOfClientBasedBuilder;


public class PhysicalBuilderStub extends AbstractBandwidthCalculatedNBClientBuilder {

	private PhysicalParameterAndModelsSet modelsSet;
	private AbstractBandwidthSpecifiedNBClientBuilder regBui;
	private AbstractLinkFormat linkFormat;
	private int nbClients;
	private PhyWrapper wrapper;

	public PhysicalBuilderStub(
			@ParamName(name = "A set of physical models", defaultClass_ = PhysicalParameterAndModelsSet.class) PhysicalParameterAndModelsSet phyModels,
			@ParamName(name = "A link format") AbstractLinkFormat linkFormat,
			@ParamName(name = "Phy layer builder", requireInterface = PhyWrapper.class) AbstractBandwidthSpecifiedNBClientBuilder regBui) {
		if (!(regBui instanceof PhyWrapper)) {
			throw new IllegalStateException("The NBClientBuilder must implement " + PhyWrapper.class.getSimpleName());
		} else {
			this.wrapper = (PhyWrapper) regBui;
			this.modelsSet = phyModels;
			this.regBui = regBui;
			this.linkFormat = linkFormat;
		}
	}

	public void setBuilder(NumberOfClientBasedBuilder builder) {
		super.setBuilder(builder);
		this.regBui.setBuilder(this.builder);
	}

	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		this.nbClients = nbClients;
		this.wrapper.potentiallyImposeFormat(nbClients, this.linkFormat);
		if (!this.linkFormat.isValid()) {
			return new InitFeedback("Skipped one simulation as bandwidth format is invalid");
		} else {
			try {
				LayoutWorseCaseProperties properties = this.findLayoutProperties(this.lwSimExperiment.getExecution());
			} catch (LayoutException var7) {
				return new InitFeedback(var7.getReason());
			}

			int wavelengths = this.linkFormat.getNumberOfChannels();
			this.lwSimExperiment.addPropertyToDefaultDataPoint("Number of wavelengths per waveguide",
					String.valueOf(wavelengths));
			this.lwSimExperiment.addPropertyToDefaultDataPoint("Wavelength modulation rate",
					"" + this.linkFormat.getWavelengthRate() / 1.0E9D);
			this.lwSimExperiment.addPropertyToDefaultDataPoint("Total aggregated rate (Gb/s)",
					String.valueOf(this.linkFormat.getAggregateRateInGbs()));
			double channelSpacing = this.modelsSet.getConstants().wavelengthsToFreqSpacing(wavelengths);
			this.lwSimExperiment.addPropertyToDefaultDataPoint("Channel spacing", String.valueOf(channelSpacing));
			this.lwSimExperiment.setReferenceBandwidth(this.wrapper.getSrcToDestRate(this.linkFormat));
			return this.regBui.build(this.lwSimExperiment, dests, nbClients);
		}
	}

	private LayoutWorseCaseProperties findLayoutProperties(Execution ex) throws LayoutException {
		return this.linkFormat.isNumberOfChannelFixed()
				? this.getPhysicalLayout().getCheckedLayoutProperties(ex, this.modelsSet, this.linkFormat)
				: this.getPhysicalLayout().getWavelengthOptimizedLayoutProperties(null, this.linkFormat,
						this.modelsSet);
	}

	public AbstractPhysicalLayout getPhysicalLayout() {
		return this.wrapper.getPhysicalLayoutImpl(this.nbClients);
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> m = this.modelsSet.getAllParameters();
		m.putAll(this.linkFormat.getAllParameters());
		m.putAll(this.regBui.getAllParameters());
		return m;
	}

	public int getMaxPacketSizeInBits() {
		return this.regBui.getMaxPacketSizeInBits();
	}

	public double getTotalInjectionBandwidthRatio() {
		return this.regBui.getTotalInjectionBandwidthRatio();
	}

	public int[][] getNeighborhood(int fromAnode) {
		return this.regBui.getNeighborhood(fromAnode);
	}

	public void notifyEnd(double clock, int status) {
		Iterator var5 = this.modelsSet.getAllParameters().entrySet().iterator();

		while (var5.hasNext()) {
			Entry<String, String> ent = (Entry) var5.next();
			if (!this.lwSimExperiment.defaultDataPointHasProperty(ent.getKey())) {
				this.lwSimExperiment.addPropertyToDefaultDataPoint(ent.getKey(), ent.getValue());
			}
		}

		double totalPower_mW = this.wrapper.getTotalpowerMW();
		if (totalPower_mW < 0.0D) {
			Rate observedReceived = new Rate(this.lwSimExperiment.receivedBits, clock / 1000.0D);
			double receivedUtilization = observedReceived
					.divide(this.lwSimExperiment.getTotalInjectionBandwidth());
			List<PowerConsumption> consumptions = this.getPhysicalLayout().getPowerConsumptions(this.modelsSet,
					this.linkFormat, true);
			int pniPerClient = this.wrapper.getNumberOfOpticalInterfacesPerClient();
			int nbPNI = pniPerClient * this.lwSimExperiment.getNumberOfClients();
			int wavelengths = this.linkFormat.getNumberOfChannels();
			double circuitUtil = this.lwSimExperiment.totalCacheTime * 1000000.0D / (clock * (double) nbPNI);
			totalPower_mW = PowerConsumption.compute(consumptions, circuitUtil, receivedUtilization, nbPNI,
					wavelengths);
			this.lwSimExperiment.addGlobalResult("Wavelengths per client", wavelengths * pniPerClient);
			this.lwSimExperiment.addGlobalResult("Circuit util", circuitUtil);
		}

		double totalEnergy = clock / 1.0E9D * (totalPower_mW / 1000.0D);
		double energyPerDeliveredBit = totalEnergy / (double) this.lwSimExperiment.receivedBits;
		this.lwSimExperiment.addGlobalResult("Energy per delivered bit (pJ)",
				String.valueOf(energyPerDeliveredBit * 1.0E12D));
		this.lwSimExperiment.addGlobalResult("Total energy (J)", totalEnergy);
		this.lwSimExperiment.addGlobalResult("Total average power (mW)", totalPower_mW);
		LayoutWorseCaseProperties layProp = this.getPhysicalLayout()
				.getLayoutPropertiesForaGivenNumberOfWavelengths(null, this.modelsSet, this.linkFormat);
		this.lwSimExperiment.addGlobalResult("Layout power penalty (dB)", layProp.getTotalPowerPenalty());
		this.regBui.notifyEnd(clock, status);
	}
}
