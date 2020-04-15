//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.ParetoPoint;
import ch.epfl.general_libraries.utils.ParetoSet;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.layout.PhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;
import edu.columbia.lrl.CrossLayer.physical_models.util.Number_X_RateFormat;


public class PhysicalLayoutBandwidthPowerParetoExperiment implements Experiment {

	private int[] nbChannels;
	private double[] rates;
	private double[] FSRs = new double[] { 2.5E-8D };
	private PhysicalLayout layout;
	private PhysicalParameterAndModelsSet modeSet;
	private boolean paretoOnly = true;
	private double utilization = 1.0D;
	private int mode;

	public PhysicalLayoutBandwidthPowerParetoExperiment(PhysicalLayout layout, PhysicalParameterAndModelsSet modeSet,
			int[] nbChannels, double[] rates) {
		this.nbChannels = nbChannels;
		this.layout = layout;
		this.modeSet = modeSet;
		this.rates = rates;
	}

	public PhysicalLayoutBandwidthPowerParetoExperiment(PhysicalLayout layout, PhysicalParameterAndModelsSet modeSet,
			int[] nbChannels, double[] rates, boolean paretoOnly) {
		this.nbChannels = nbChannels;
		this.layout = layout;
		this.modeSet = modeSet;
		this.rates = rates;
		this.paretoOnly = paretoOnly;
	}

	public PhysicalLayoutBandwidthPowerParetoExperiment(PhysicalLayout layout, PhysicalParameterAndModelsSet modeSet,
			int[] nbChannels, double[] rates, double utilization, boolean paretoOnly) {
		this.nbChannels = nbChannels;
		this.layout = layout;
		this.modeSet = modeSet;
		this.rates = rates;
		this.paretoOnly = paretoOnly;
		this.utilization = utilization;
	}

	public PhysicalLayoutBandwidthPowerParetoExperiment(PhysicalLayout layout, PhysicalParameterAndModelsSet modeSet,
			@ParamName(name = "Number of channels") int[] nbChannels,
			@ParamName(name = "Total rates (mode 1) or channel rate (modes 2 or 3)") double[] rates,
			@ParamName(name = "Utilization") double utilization, @ParamName(name = "FSRs to consider") double[] FSRs,
			@ParamName(name = "mode") int mode, @ParamName(name = "Save pareto designs only") boolean paretoOnly) {
		this.nbChannels = nbChannels;
		this.layout = layout;
		this.modeSet = modeSet;
		this.rates = rates;
		this.FSRs = FSRs;
		this.paretoOnly = paretoOnly;
		this.utilization = utilization;
		this.mode = mode;
	}

	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		Execution execution = new Execution();
		Object paretoSet;
		if (this.paretoOnly && this.mode == 1) {
			paretoSet = new ParetoSet(2);
		} else {
			paretoSet = new ArrayList();
		}

		DataPoint dp = new DataPoint();
		dp.addProperties(this.layout.getAllParameters());
		dp.addProperties(this.modeSet.getAllParameters());
		dp.addProperty("utilization", this.utilization);

		for (int i = 0; i < this.rates.length; ++i) {
			double maxAggr = 0.0D;
			PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower maxAggreBP = null;
			double minPow = 1.0D / 0.0;
			PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower minPowBP = null;

			for (int j = 0; j < this.nbChannels.length; ++j) {
				double[] var18;
				int var17 = (var18 = this.FSRs).length;

				for (int var16 = 0; var16 < var17; ++var16) {
					double FSR = var18[var16];

					try {
						this.modeSet.getConstants().setFullFSR(FSR);
						Number_X_RateFormat format;
						if (this.mode == 1) {
							format = new Number_X_RateFormat(this.nbChannels[j],
									this.rates[i] / (double) this.nbChannels[j]);
						} else {
							format = new Number_X_RateFormat(this.nbChannels[j], this.rates[i]);
						}

						LayoutWorseCaseProperties layProp = this.layout
								.getLayoutPropertiesForaGivenNumberOfWavelengths(execution, this.modeSet, format);
						double availBudget = this.layout.getPowerBudget_dB(this.modeSet, format);
						double requiredBudget = layProp.getTotalPowerPenalty()
								+ 10.0D * Math.log10((double) this.nbChannels[j]);
						if (requiredBudget < availBudget) {
							List<PowerConsumption> pcs = this.layout.getPowerConsumptions(this.modeSet, format, true);
							double maxPower = PowerConsumption.compute(pcs, this.utilization, this.utilization, 1,
									this.nbChannels[j]);
							double powerPerBit = maxPower / format.getAggregateRateInGbs();
							PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower bp = new PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower(
									this.rates[i], format, layProp, powerPerBit, pcs, FSR, this.mode);
							if (format.getAggregateRateInGbs() > maxAggr) {
								maxAggr = format.getAggregateRateInGbs();
								maxAggreBP = bp;
							}

							if (powerPerBit < minPow) { minPow = powerPerBit; minPowBP = bp; }

							if (this.paretoOnly) {
								if (this.mode == 1) { ((ParetoSet) paretoSet).addCandidate(bp); }
							} else {
								((ArrayList) paretoSet).add(bp);
							}
						}
					} catch (WrongExperimentException var31) {
					}
				}
			}

			if (this.mode == 2 && maxAggreBP != null) { ((ArrayList) paretoSet).add(maxAggreBP); }

			if (this.mode == 3 && minPowBP != null) { ((ArrayList) paretoSet).add(minPowBP); }
		}

		Iterator var33 = ((Iterable) paretoSet).iterator();

		while (var33.hasNext()) {
			PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower dd = (PhysicalLayoutBandwidthPowerParetoExperiment.BandwidthPower) var33
					.next();
			AbstractLinkFormat format = dd.format;
			Iterator var36 = dd.powerCons.iterator();

			while (var36.hasNext()) {
				PowerConsumption pc = (PowerConsumption) var36.next();
				DataPoint dp2 = dp.getDerivedDataPoint();
				dp2.addProperties(format.getAllParameters());
				dp2.addProperty("Consumption name", pc.getOrigin());
				dp2.addProperty("FSR", dd.FSR);
				double consumptionmW = pc.compute(this.utilization, this.utilization, 1, format.getNumberOfChannels());
				dp2.addResultProperty("Consumption (mW)", consumptionmW);
				dp2.addResultProperty("Energy need (pJ/bit)",
						consumptionmW / (format.getAggregateRateInGbs() * this.utilization));
				dp2.addResultProperty("Aggregate line rate_ (Gb/s)", format.getAggregateRateInGbs());
				execution.addDataPoint(dp2);
			}

			double sensibility = this.modeSet.getSignallingModel().getReceiverSensitivity(this.modeSet, format);
			double requiredBudget = dd.layProp.getTotalPowerPenalty()
					+ 10.0D * Math.log10((double) format.getNumberOfChannels());
			DataPoint global = dp.getDerivedDataPoint();
			global.addProperties(format.getAllParameters());
			global.addProperty("Photodetector sensibility", sensibility);
			global.addResultProperty("Energy per bit", dd.power);
			global.addResultProperty("Aggregate line rate (Gb/s)", format.getAggregateRateInGbs());
			global.addProperty("FSR", dd.FSR);
			Constants ct = this.modeSet.getConstants();
			double centerLambda = ct.getCenterWavelength();
			double radiusMicron = 1000000.0D * centerLambda * centerLambda / (27.60203305443992D * dd.FSR);
			global.addProperty("ring radius um", radiusMicron);
			global.addResultProperty("Required laser power (dBm)", sensibility + requiredBudget);
			global.addResultProperty("Required laser power (mW)",
					Math.pow(10.0D, (sensibility + requiredBudget) / 10.0D));
			execution.addDataPoint(global);
		}

		man.addExecution(execution);
	}

	private static class BandwidthPower extends ParetoPoint {

		private double power;
		private AbstractLinkFormat format;
		private List<PowerConsumption> powerCons;
		private LayoutWorseCaseProperties layProp;
		private double FSR;
		private double aggreBW;
		private int mode;

		private BandwidthPower(double aggreBW, AbstractLinkFormat format, LayoutWorseCaseProperties layProp,
				double power, List<PowerConsumption> powerCons, double FSR, int mode) {
			this.format = format;
			this.power = power;
			this.powerCons = powerCons;
			this.layProp = layProp;
			this.FSR = FSR;
			this.aggreBW = aggreBW;
			this.mode = mode;
		}

		public double getValueOfDimensionN(int n) {
			if (this.mode == 1) {
				switch (n) {
					case 0:
						return this.power;
					case 1:
						return this.aggreBW;
					default:
						throw new IllegalStateException("Illegal value");
				}
			} else if (this.mode == 2) {
				return this.aggreBW;
			} else if (this.mode == 3) {
				return this.power;
			} else {
				throw new IllegalStateException();
			}
		}

		public boolean isDimensionNtheHigherTheBetter(int n) {
			if (this.mode == 1) {
				if (n == 0) { return false; }

				if (n == 1) { return true; }
			} else {
				if (this.mode == 2) { return true; }

				if (this.mode == 3) { return false; }
			}

			throw new IllegalStateException();
		}

		public int getDimensions() {
			return this.mode == 1 ? 2 : 1;
		}
	}
}
