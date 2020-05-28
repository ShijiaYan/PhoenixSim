package edu.columbia.lrl.CrossLayer;

import java.util.List;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.demux.ring_array.RingBasedFilterArrayModelAdaptiveIL;
import edu.columbia.lrl.CrossLayer.physical_models.layout.AbstractPhysicalLayout;
import edu.columbia.lrl.CrossLayer.physical_models.layout.LayoutException;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;


public class PhysicalLayoutExperiment implements Experiment {

    private AbstractPhysicalLayout layout;
    private PhysicalParameterAndModelsSet modeSet;
    private AbstractLinkFormat linkFormat;
    private boolean withPenaltyDetails;
    private double utilization;
    // private boolean withPhyParams;

    public PhysicalLayoutExperiment(@ParamName(name = "Physical Layout") AbstractPhysicalLayout layout,
                                    @ParamName(name = "Link Format") AbstractLinkFormat linkFormat,
                                    @ParamName(name = "Physical Parameters and Models Set") PhysicalParameterAndModelsSet modeSet,
                                    @ParamName(name = "Include power penalty details", default_ = "false") boolean withPenaltyDetails) {
        this.layout = layout;
        this.modeSet = modeSet;
        this.linkFormat = linkFormat;
        this.withPenaltyDetails = withPenaltyDetails;
        this.utilization = 1;
    }

    public PhysicalLayoutExperiment(@ParamName(name = "Physical Layout") AbstractPhysicalLayout layout,
                                    @ParamName(name = "Link Format") AbstractLinkFormat linkFormat,
                                    @ParamName(name = "Physical Parameters and Models Set") PhysicalParameterAndModelsSet modeSet,
                                    @ParamName(name = "Include power penalty details", default_ = "false") boolean withPenaltyDetails,
                                    @ParamName(name = "Utilization (for power calc)", default_ = "1") double utilization) {
        this.layout = layout;
        this.modeSet = modeSet;
        this.linkFormat = linkFormat;
        this.withPenaltyDetails = withPenaltyDetails;
        this.utilization = utilization;
    }

    @Override
    public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
        LayoutWorseCaseProperties layProp; // model for Power Penalty
        Execution execution = new Execution();
        if (!linkFormat.isValid()) {
            /*
             * Formats are assumed valid by default, but overriding class can
             * override this method to declare a combination of parameters invalid
             */
            return;
        }
        try {
            boolean optimizedWavelengths = false;
            if (linkFormat.isNumberOfChannelFixed()) {
                layProp = layout.getLayoutPropertiesForaGivenNumberOfWavelengths(execution, modeSet, linkFormat);
            } else {
                layProp = layout.getWavelengthOptimizedLayoutProperties(execution, linkFormat, modeSet);
                optimizedWavelengths = true;
            }

            int wavelengths = linkFormat.getNumberOfChannels();

            List<PowerConsumption> powerWithLaser = layout.getPowerConsumptions(modeSet, linkFormat, true);
            List<PowerConsumption> powerWithoutLaser = layout.getPowerConsumptions(modeSet, linkFormat, false);

            double staticPower = PowerConsumption.compute(powerWithLaser, 0, 0, 1, wavelengths);
            double maxPower = PowerConsumption.compute(powerWithLaser, 1, utilization, 1, wavelengths);
            double maxPowerWithoutLaser = PowerConsumption.compute(powerWithoutLaser, 1, utilization, 1, wavelengths);

            DataPoint dp = new DataPoint();
            dp.addProperty("traffic utilization", utilization + "");
            dp.addProperties(layout.getAllParameters());
            dp.addProperties(modeSet.getAllParameters());
            dp.addProperties(linkFormat.getAllParameters());
            dp.addProperty("ideal gap", RingBasedFilterArrayModelAdaptiveIL.lastGap);
            double availBudget = layout.getPowerBudget_dB(modeSet, linkFormat);
            double sensibility = modeSet.getSignallingModel().getReceiverSensitivity(modeSet, linkFormat);

            dp.addResultProperty("available budget dB", availBudget);
            dp.addResultProperty("available budget per channel dB",
                    layout.getPowerBudgetPerChannel_dB(modeSet, linkFormat));
            if (!optimizedWavelengths) {
                double requiredBudget = layProp.getTotalPowerPenalty() + 10 * Math.log10(wavelengths);

                // test
                dp.addResultProperty("laser eff", modeSet.getLaserModel().getLaserEfficiency(wavelengths,
                        PowerConsumption.dBmTomW(sensibility + layProp.getTotalPowerPenalty())));
                //

                dp.addResultProperty("Required opt power budget for layout (dB)", requiredBudget);
                dp.addResultProperty("Required laser power (dBm)", sensibility + requiredBudget);
                dp.addResultProperty("Required laser power (mW)",
                        PowerConsumption.dBmTomW(sensibility + requiredBudget));
                dp.addProperty("Layout feasibility", requiredBudget < availBudget);
            }
            if (withPenaltyDetails) {
                double totalPP = 0;
                for (PowerPenalty pp : layProp.getPowerPenalties()) {
                    DataPoint dp2 = dp.getDerivedDataPoint();
                    dp2.addProperty("power penalty name", pp.getName());
                    dp2.addProperty("power penalty group", pp.getCategory());
                    dp2.addResultProperty("power penalty value (dB)", pp.getTotalPowerPenalty());
                    if (pp.isMultiplied()) {
                        dp2.addResultProperty("individual PP value (dB)", pp.getIndividualPowerPenalty());
                    }
                    totalPP += pp.getTotalPowerPenalty();
                    execution.addDataPoint(dp2);
                }
                // adding a total category
                DataPoint dp2 = dp.getDerivedDataPoint();
                dp2.addProperty("power penalty name", "total");
                dp2.addProperty("power penalty group", "total");
                dp2.addResultProperty("power penalty value (dB)", totalPP);
                execution.addDataPoint(dp2);
            }
            if (withPenaltyDetails) {
                for (PowerConsumption pc : powerWithLaser) {
                    DataPoint dp2 = dp.getDerivedDataPoint();
                    dp2.addProperty("Consumption name", pc.getOrigin());
                    double mW = pc.compute(1, 1, 1, wavelengths);
                    dp2.addResultProperty("Consumption (mW)", mW);
                    dp2.addResultProperty("Consumption per type (pJ/bit)", mW / linkFormat.getAggregateRateInGbs());
                    execution.addDataPoint(dp2);
                }
            }

            DataPoint general = dp.getDerivedDataPoint();
            general.addResultProperty("Layout power penalty (dB)", layProp.getTotalPowerPenalty());
            general.addResultProperty("Layout propagation latency (ns)", layProp.propagationLatencyNS);
            general.addResultProperty("Layout static power consumption (mW)", staticPower);
            general.addResultProperty("Layout power consumption at 100% (mW)", maxPower);
            general.addResultProperty("Energy-per-bit (on-chip) (pJ)",
                    maxPowerWithoutLaser / linkFormat.getAggregateRateInGbs());
            general.addResultProperty("Energy-per-bit (pJ)", maxPower / linkFormat.getAggregateRateInGbs());
            general.addResultProperty("Aggregate line rate (Gb/s)", linkFormat.getAggregateRateInGbs());

            execution.addDataPoint(general);
            man.addExecution(execution);
        } catch (LayoutException e) {
            throw new IllegalStateException(e);
        }
    }

}
