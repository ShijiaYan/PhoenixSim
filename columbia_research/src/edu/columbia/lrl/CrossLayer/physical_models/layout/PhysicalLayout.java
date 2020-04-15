//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.columbia.lrl.CrossLayer.physical_models.layout;

import java.util.List;
import java.util.Map;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractLaserModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;


public abstract class PhysicalLayout {

	public PhysicalLayout() {
	}

	public abstract Map<String, String> getAllParameters();

	public abstract double getUnavailabilityTime();

	public abstract LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(Execution var1,
			PhysicalParameterAndModelsSet var2, AbstractLinkFormat var3);

	public List<PowerConsumption> getPowerConsumptions(PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat, boolean withLaser) {
		LayoutWorseCaseProperties layoutWorseCase = this
				.getLayoutPropertiesForaGivenNumberOfWavelengths((Execution) null, modelSet, linkFormat);
		List<PowerConsumption> list = this.getLayoutSpecificConsumption(modelSet, linkFormat);
		AbstractLaserModel laserModel = modelSet.getLaserModel();
		if (laserModel.isPowerFixed()) {
			double opticalPowerAtReceiverdBm = laserModel.getEmittedPowerdBm() - layoutWorseCase.getTotalPowerPenalty();
			list.addAll(modelSet.getSignallingModel().getPowerConsumptions(opticalPowerAtReceiverdBm, modelSet,
					linkFormat));
			if (withLaser) {
				list.add(modelSet.getSingleLaserConsumption(laserModel,
						PhysicalParameterAndModelsSet.dBmTomW(laserModel.getEmittedPowerdBm()),
						linkFormat.getNumberOfChannels()));
			}
		} else {
			list.addAll(modelSet.getSignallingModel().getPowerConsumptionsWithMinimalLaser(modelSet, linkFormat));
			if (withLaser) { list.add(modelSet.getMinimalSingleLaserConsumption(linkFormat, layoutWorseCase)); }
		}

		return list;
	}

	public abstract List<PowerConsumption> getLayoutSpecificConsumption(PhysicalParameterAndModelsSet var1,
			AbstractLinkFormat var2);

	public double getLayoutSpecificMaximumToleratedOpticalPower() {
		return -1.0D;
	}

	private double getMaxOpticalPower(PhysicalParameterAndModelsSet modelSet) {
		double d = this.getLayoutSpecificMaximumToleratedOpticalPower();
		return d < 0.0D ? modelSet.getMaximumAggregatedOpticalPower() : d;
	}

	public double getPowerBudget_dB(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		double budgetDB = this.getMaxOpticalPower(modelSet)
				- modelSet.getSignallingModel().getReceiverSensitivity(modelSet, linkFormat);
		return budgetDB;
	}

	public double getPowerBudgetPerChannel_dB(PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
		double budgetDB = this.getMaxOpticalPower(modelSet)
				- 10.0D * Math.log10((double) linkFormat.getNumberOfChannels())
				- modelSet.getSignallingModel().getReceiverSensitivity(modelSet, linkFormat);
		return budgetDB;
	}

	public LayoutWorseCaseProperties getCheckedLayoutProperties(Execution ex, PhysicalParameterAndModelsSet modelSet,
			AbstractLinkFormat linkFormat) throws LayoutException {
		int wavelengths = linkFormat.getNumberOfChannels();
		LayoutWorseCaseProperties lp = this.getLayoutPropertiesForaGivenNumberOfWavelengths(ex, modelSet, linkFormat);
		double wavelengthFactor = 10.0D * Math.log10((double) wavelengths);
		if (lp.getTotalPowerPenalty() + wavelengthFactor > this.getPowerBudget_dB(modelSet, linkFormat)) {
			throw new LayoutException("Too many wavelengths (" + wavelengths + ") for budget");
		} else if (modelSet.getLaserModel().isPowerFixed() && modelSet.getLaserModel().getEmittedPowerdBm()
				+ wavelengthFactor > this.getMaxOpticalPower(modelSet)) {
			throw new LayoutException("Too many wavelengths (" + wavelengths + ") for budget with given laser power");
		} else {
			return lp;
		}
	}

	public LayoutWorseCaseProperties getWavelengthOptimizedLayoutProperties(Execution ex, AbstractLinkFormat linkFormat,
			PhysicalParameterAndModelsSet modelSet) throws LayoutException {
		int min = 0;
		int max = -1;
		int wavelengths = 16;
		double budgetDB = this.getPowerBudget_dB(modelSet, linkFormat);
		double totalPowerPenalty = 1.7976931348623157E308D;
		LayoutWorseCaseProperties lp = null;

		while (min != max) {
			linkFormat.setNumberOfWavelengths(wavelengths);
			lp = this.getLayoutPropertiesForaGivenNumberOfWavelengths(ex, modelSet, linkFormat);
			totalPowerPenalty = lp.getTotalPowerPenalty();
			double wavelengthFactor = 10.0D * Math.log10((double) wavelengths);
			if (totalPowerPenalty + wavelengthFactor < budgetDB) {
				min = Math.max(min, wavelengths);
				if (max < 0) {
					wavelengths *= 2;
				} else {
					wavelengths = min + (int) Math.ceil((double) (max - min) / 2.0D);
				}
			} else {
				if (max < 0) {
					max = wavelengths - 1;
				} else {
					max = Math.min(wavelengths - 1, max);
				}

				wavelengths = min + (max - min) / 2;
			}
		}

		linkFormat.setNumberOfWavelengths(wavelengths);
		if (lp == null) {
			throw new LayoutException("Too little budget for a single wavelength");
		} else {
			return lp;
		}
	}
}
