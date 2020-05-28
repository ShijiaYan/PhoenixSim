package edu.columbia.lrl.CrossLayer.physical_models.layout;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.PowerConsumption;
import edu.columbia.lrl.CrossLayer.physical_models.PhysicalParameterAndModelsSet;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.devices.AbstractLaserModel;
import edu.columbia.lrl.CrossLayer.physical_models.devices.interleaver.AbstractInterleaver;
import edu.columbia.lrl.CrossLayer.physical_models.generic_models.xtalk.MeisamJLTXtalkModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;
import edu.columbia.lrl.CrossLayer.physical_models.util.LayoutWorseCaseProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class InterleaverLayout extends AbstractPhysicalLayout {

    private int numOfCouplers;
    private int numOfJunctions;
    private AbstractInterleaver abstractInterleaver;

    public InterleaverLayout(AbstractInterleaver abstractInterleaver,
                             @ParamName(name = "Number of Couplers", default_ = "3") int numOfCouplers,
                             @ParamName(name = "Number of Y-Junctions", default_ = "0") int numOfJunctions) {
        this.abstractInterleaver = abstractInterleaver;
        this.numOfCouplers = numOfCouplers;
        this.numOfJunctions = numOfJunctions;
    }

    public Map<String, String> getAllParameters() {
        Map<String, String> map = this.abstractInterleaver.getAllParameters();
        map.put("Interleaver Groups", String.valueOf(this.abstractInterleaver.getPortNumbers()));
        return map;
    }

    public double getUnavailabilityTime() {
        return 0.0D;
    }

    public ArrayList<PowerPenalty> getModelSetPenalties(Execution ex, PhysicalParameterAndModelsSet modelSet,
                                                        AbstractLinkFormat linkFormat) {
        int wavelengths = linkFormat.getNumberOfChannels();
        int interleaverPort = abstractInterleaver.getPortNumbers();
        int modulationEffWavelengths = (int) Math.ceil((double) wavelengths / (double) interleaverPort);
        linkFormat.setNumberOfWavelengths(modulationEffWavelengths); // calculating modulation in one group
        ArrayList<PowerPenalty> ppList = new ArrayList<>(modelSet.getSignallingModel().getPowerPenalties(ex, modelSet,
                linkFormat));
        double waveguideLengthCM = modelSet.getInterfaceWaveguideLengthModel()
                .getModulatorWaveguideLengthCm(modulationEffWavelengths);
        waveguideLengthCM += modelSet.getInterfaceWaveguideLengthModel()
                .getFilterWaveguideLengthCm(modulationEffWavelengths);
        double modelLoss = modelSet.getWaveguideLoss() * waveguideLengthCM;
        modelLoss += modelSet.getCouplerLoss() * (double) this.getNumOfCouplers();
        modelLoss += modelSet.getCrossingLoss();
        modelLoss += modelSet.getJunctionLoss() * (double) this.numOfJunctions;
        ppList.add(new PowerPenalty("ModelSetPassivePenalties", "Passive devices", modelLoss));
        linkFormat.setNumberOfWavelengths(wavelengths); // setting back wavelength number for further calculations

        return ppList;
    }

    public LayoutWorseCaseProperties getLayoutPropertiesForaGivenNumberOfWavelengths(Execution ex,
                                                                                     PhysicalParameterAndModelsSet modelSet, AbstractLinkFormat linkFormat) {
        LayoutWorseCaseProperties prop = new LayoutWorseCaseProperties(linkFormat.getNumberOfChannels());
        prop.addPowerPenalties(this.getModelSetPenalties(ex, modelSet, linkFormat));
        prop.addPowerPenalties(this.abstractInterleaver.getPowerPenalties(modelSet, linkFormat, ex, 0.0D));
        MeisamJLTXtalkModel xtalkModel = new MeisamJLTXtalkModel(false);
        double leaverXTalk = Math.pow(10.0D, this.abstractInterleaver.getLeaverXTalk() / 10.0D);
        double xtalkPP_DB = xtalkModel.getXtalkPP_DB(leaverXTalk, modelSet.getBer(), 1.0D);
        prop.addPowerPenalty(new PowerPenalty("Interleaver XTalk", "Interleaver", xtalkPP_DB));
        return prop;
    }

    public List<PowerConsumption> getPowerConsumptions(PhysicalParameterAndModelsSet modelSet,
                                                       AbstractLinkFormat linkFormat, boolean withLaser) {
        int wavelengths = linkFormat.getNumberOfChannels();
        int interleaverGroup = this.abstractInterleaver.getPortNumbers();
        int effWavelengths = (int) Math.ceil((double) wavelengths / (double) interleaverGroup);
        LayoutWorseCaseProperties layoutWorseCase = this
                .getLayoutPropertiesForaGivenNumberOfWavelengths(null, modelSet, linkFormat);
        List<PowerConsumption> list = this.getLayoutSpecificConsumption(modelSet, linkFormat);
        AbstractLaserModel laserModel = modelSet.getLaserModel();
        linkFormat.setNumberOfWavelengths(effWavelengths);
        if (laserModel.isPowerFixed()) {
            double opticalPowerAtReceiverdBm = laserModel.getEmittedPowerdBm() - layoutWorseCase.getTotalPowerPenalty();
            List<PowerConsumption> signalPowerConsumptions = modelSet.getSignallingModel()
                    .getPowerConsumptions(opticalPowerAtReceiverdBm, modelSet, linkFormat);
            list.addAll(signalPowerConsumptions);
            if (withLaser) {
                list.add(modelSet.getSingleLaserConsumption(laserModel,
                        PhysicalParameterAndModelsSet.dBmTomW(laserModel.getEmittedPowerdBm()),
                        linkFormat.getNumberOfChannels()));
            }
        } else {
            list.addAll(modelSet.getSignallingModel().getPowerConsumptionsWithMinimalLaser(modelSet, linkFormat));
            if (withLaser) {
                list.add(modelSet.getMinimalSingleLaserConsumption(linkFormat, layoutWorseCase));
            }
        }

        linkFormat.setNumberOfWavelengths(wavelengths);
        return list;
    }

    public List<PowerConsumption> getLayoutSpecificConsumption(PhysicalParameterAndModelsSet modelSet,
                                                               AbstractLinkFormat linkFormat) {
        return new ArrayList<>(this.abstractInterleaver.getDevicePowerConsumptions(modelSet,
                linkFormat));
    }

    public int getInterleaverPort() {
        return this.getAbstractInterleaver().getPortNumbers();
    }

    public int getNumOfCouplers() {
        return this.numOfCouplers;
    }

    public AbstractInterleaver getAbstractInterleaver() {
        return this.abstractInterleaver;
    }
}
