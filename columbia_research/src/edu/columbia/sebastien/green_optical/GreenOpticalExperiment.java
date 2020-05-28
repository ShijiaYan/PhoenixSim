package edu.columbia.sebastien.green_optical;

import java.util.HashSet;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.PairList;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javancox.experiments.builder.object_enum.ExperimentExecutionManager;
import edu.columbia.lrl.experiments.topology_radix.abstractmodel.AbstractModel;

/*
 * TODO:
 * 
 * - Also support connection distance distribution (instead of mean)
 * - 
 */

public class GreenOpticalExperiment implements Experiment {
	
	private static final String defLinkBudg = "0:0.3:30";
	private static final String defLineRate = "10:1:50";
	private static final String defAmpRate = "0";	
	
	private static HashSet<AbstractOpticalSwitchModel> switchModelSet = new HashSet<>();
	private static HashSet<AbstractTransceiverModel> transModelSet = new HashSet<>();
	private static HashSet<AbstractAmplifierModel> amplModelSet = new HashSet<>();
	
	private AbstractOpticalSwitchModel switchModel;
	private AbstractTransceiverModel transModel;
	private AbstractAmplifierModel amplModel;
	private DefaultMachineModel machMod;
	private int numberOfChannels;
	private double[] linkBudgetForWhichAnalyze;
	
	private double[] ampRates = TypeParser.parseDouble(defAmpRate);
	private double[] lineRates = TypeParser.parseDouble(defLineRate);
	
	private int concentration;
	private double cablePP;
	
	private boolean proportional = false;
	private boolean worseCase = false;
	
	{
		ExperimentExecutionManager.registerAsCachedClass(GreenOpticalExperiment.class);
	}
	
	public GreenOpticalExperiment(AbstractOpticalSwitchModel switchModel,
			AbstractTransceiverModel transModel,
			AbstractAmplifierModel amplModel,
			DefaultMachineModel machineModel,
			@ParamName(name="Usage proportional power", default_="false") boolean proportional,
			@ParamName(name="Dimensioned for worse-case", default_="false") boolean worseCase,			
			@ParamName(name="Cable PP in dB", default_="0.5") double cablePP,			
			@ParamName(name="Number of channels", default_="1:150log20") int numberOfChannels,
			@ParamName(name="Concentration", default_="10") int concentration,
			@ParamName(name="Amplification rates", default_=defAmpRate) double[] ampRates,
			@ParamName(name="Line rates to try", default_=defLineRate) double[] lineRates) {
		this.linkBudgetForWhichAnalyze = TypeParser.parseDouble(defLinkBudg);
		
		this.switchModel = switchModel;
		this.transModel = transModel;
		this.amplModel = amplModel;
		
		this.proportional = proportional;
		this.worseCase = worseCase;
		
		this.cablePP = cablePP;
		
		this.ampRates = ampRates;
		this.lineRates = lineRates;
		
		this.concentration = concentration;
		
		this.machMod = machineModel;
		this.numberOfChannels = numberOfChannels;		
	}
	
/*	public GreenOpticalExperiment(AbstractOpticalSwitchModel switchModel,
			AbstractTransceiverModel transModel,
			AbstractAmplifierModel amplModel,
			double rateInGbs,
			int concentration,
			DefaultMachineModel machineModel,			
			@ParamName(name="Link budgets for which run the analysis", default_=defLinkBudg) double[] linkBudgetForWhichAnalyze) {
		this.linkBudgetForWhichAnalyze = linkBudgetForWhichAnalyze;
		
		this.switchModel = switchModel;
		this.transModel = transModel;
		this.amplModel = amplModel;
		
		this.concentration = concentration;
		
		this.machMod = machineModel;		
		this.aggregatedRateInGbs = rateInGbs;		
	}*/
	
/*	private void analyzeTransModel(Execution e) {
		for (double d : linkBudgetForWhichAnalyze) {
			double linkBudgetIndB = d;	
			double laserpower = transModel.getLaserPowerConsumption(aggregatedRateInGbs, 1, linkBudgetIndB);
			double nonLaserPower = transModel.getNonLaserPowerConsumption(aggregatedRateInGbs, 1, linkBudgetIndB);
			DataPoint dp = new DataPoint();
			dp.addProperty("Rate in Gb/s", aggregatedRateInGbs);
			dp.addProperty("Link budget dB", linkBudgetIndB);
			dp.addProperties(transModel.getAllProperties());
			dp.addResultProperty("transceiver laser power consumption", laserpower);
			dp.addResultProperty("transceiver non laser power consumption", nonLaserPower);			
			
			e.addDataPoint(dp);
		}
	}*/

/*	private void analyzeAmplifierModel(Execution e) {
		for (int i = -10 ; i < 20 ; i++) {
			for (double d = 0 ; d < 30 ; d = d+0.3) {
				double outputLevelIndBm = i;
				double gainIndB = d;	
				double power = amplModel.getPowerConsumption(gainIndB, outputLevelIndBm);
				DataPoint dp = new DataPoint();
				dp.addProperty("Rate in Gb/s", aggregatedRateInGbs);
				dp.addProperty("Initial req power", outputLevelIndBm);
				dp.addProperty("Gain in dB", gainIndB);
				dp.addProperties(amplModel.getAllProperties());
				dp.addResultProperty("amplifier power consumption", power);
				e.addDataPoint(dp);
			}		
		}
	}*/

/*	private void analyzeSwitchModel(Execution e) {
		for (int radix : new int[]{8, 16, 24, 32, 48, 64, 96, 128, 160, 192, 256}) {
			for (double lineRateInGbs : TypeParser.parseDouble(defLineRate)) {	
				for (int numberOfWavelengths : new int[]{1,5,10,30, 100}) {
					double pp = switchModel.getPowerPenalty(radix, lineRateInGbs, numberOfWavelengths);
					double power = switchModel.getSwitchConsumption(radix);
					DataPoint dp = new DataPoint();
					dp.addProperty("Rate in Gb/s", aggregatedRateInGbs);
					dp.addProperty("Radix", radix);
					dp.addProperties(switchModel.getAllProperties());
					dp.addResultProperty("switch power penalty", pp);
					dp.addResultProperty("switch power consumption", power);				
					e.addDataPoint(dp);	
				}
			}
		}
	}*/

	public static void clearCache() {
		switchModelSet.clear();
		transModelSet.clear();
		amplModelSet.clear();
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		Execution execution = new Execution();	
		

		
	/*	if (!switchModelSet.contains(switchModel)) {
			analyzeSwitchModel(e);
			switchModelSet.add(switchModel);
		}
		if (!amplModelSet.contains(amplModel)) {
			analyzeAmplifierModel(e);
			amplModelSet.add(amplModel);
		}
		if (!transModelSet.contains(transModel)) {
			analyzeTransModel(e);
			transModelSet.add(transModel);
		}*/
		for (double lineRateInGbs : lineRates) {	
			
			double aggregatedRateInGbs = numberOfChannels*lineRateInGbs;
		
			double unroundedPlanes = machMod.getNodeBandwidthInGbs()/aggregatedRateInGbs;
			int numberOfPlanes = (int)Math.ceil(machMod.getNodeBandwidthInGbs()/aggregatedRateInGbs);
			double bwPerPlane = unroundedPlanes/(double)numberOfPlanes;

			AbstractModel abdMod = new AbstractModel(machMod.getNumberOfNodes(), concentration, 1, bwPerPlane);

			if (abdMod.runFindC()) {

				PairList<Integer, Integer> distanceHist = abdMod.getNodeDistanceHistogram();
				double averageDistance = abdMod.getAverageDistance();
				int numberOfSwitches = abdMod.getNumberOfSwitches();
				int radix = abdMod.getUnroundedRadix();

				DataPoint dp = getDefaultDataPoint(aggregatedRateInGbs);
				dp.addProperty("Number of planes", numberOfPlanes);
				dp.addProperty("Bandwidth per plane", bwPerPlane);			

				DataPoint global = dp.getDerivedDataPoint();
				global.addResultProperty("Average distance", averageDistance);
				global.addResultProperty("Number of switches", numberOfSwitches);
				global.addResultProperty("Number of switches (all planes)", numberOfSwitches*numberOfPlanes);			
				global.addResultProperty("Number of ports", numberOfSwitches*radix);
				global.addResultProperty("Switch radix", radix);
				global.addResultProperty("Average traffic per plane", machMod.getNumberOfNodes()* averageDistance*bwPerPlane);
				global.addResultProperty("Opt traf", abdMod.getOptTraffic()+"");
				global.addResultProperty("Capacity", abdMod.getOptCapacity()+"");
				//		global.addResultProperty("Number of links per plane", abdMod.getLinks());


				for (double ampRate : ampRates) {

					DataPoint dp2 = dp.getDerivedDataPoint();

					dp2.addProperty("Amplification rate", ampRate);
					dp2.addProperty("wavelength line rate", lineRateInGbs);				
					if (aggregateConsumption(
							machMod.getNumberOfNodes(), 
							numberOfPlanes,
							bwPerPlane,
							distanceHist, 
							numberOfSwitches, 
							radix, 
							aggregatedRateInGbs,
							lineRateInGbs,
							ampRate,
							dp2,
							execution) >= 0) {
					}
				}
				execution.addDataPoint(global);				
			}
					
			man.addExecution(execution);
		}
		
	}
	
	private DataPoint getDefaultDataPoint(double aggRate) {
		DataPoint dp = new DataPoint();
		dp.addProperties(amplModel.getAllProperties());
		dp.addProperties(transModel.getAllProperties());
		dp.addProperties(switchModel.getAllProperties());
		dp.addProperties(machMod.getAllProperties());		
		dp.addProperty("Aggregated line rate", aggRate);
		dp.addProperty("Concentration", concentration);
		dp.addProperty("Worse-case", worseCase);
		dp.addProperty("Cable PP", cablePP);
		
		return dp;
	}

	private double aggregateConsumption(
			int N,
			double numberOfPlanes,
			double bwPerPlane,
			PairList<Integer, Integer> distanceHist,
			int numberOfSwitches,
			int radix,
			double aggregatedRateInGbs,
			double wavelengthRateInGbs,
			double amplificationRate,
			DataPoint dp,
			Execution execution) {
		
		DataPoint localGlobal = dp.getDerivedDataPoint();
		
		int nbWavelengths = MoreMaths.ceilDiv(aggregatedRateInGbs, wavelengthRateInGbs);
		
		double switchPP = switchModel.getPowerPenalty(radix, wavelengthRateInGbs, nbWavelengths);
		
		if (Double.isInfinite(switchPP)) return -1;

		localGlobal.addResultProperty("Switch PP", switchPP);
		localGlobal.addProperty("radix", radix);
		
		double transLaserPowerAccum = 0;
		double transNonLaserPowerAccum = 0;
		double ampPowerAccum = 0;
		
		int totCheck = 0;
		
		double worseCaseLaserCons = 0;
		double worseCaseNonLaserCons = 0;
		double worseCasePP = 0;
		
		for (Pair<Integer, Integer> atDist : distanceHist) {
			
			int hops = atDist.getFirst();
			
			double distLinkPP = switchPP*(hops - 1) + cablePP * hops;
			
			double nbAmps = hops;
			
			double perAmpGain = distLinkPP*amplificationRate/nbAmps;
			
			double unCompensatedPP = distLinkPP*(1-amplificationRate);
			
			if (unCompensatedPP > worseCasePP) {
				worseCasePP = unCompensatedPP;
			}
			
			double transceiverOutputLevel = transModel.getOutputLevel(wavelengthRateInGbs, nbWavelengths, unCompensatedPP);
			double ampOutputLevel = perAmpGain + transceiverOutputLevel - switchPP;	

			double singleLaserTransceiverConsumption = transModel.getLaserPowerConsumption(wavelengthRateInGbs, nbWavelengths, unCompensatedPP);
			double singleNonLaserTransceiverConsumption = transModel.getNonLaserPowerConsumption(wavelengthRateInGbs, nbWavelengths, unCompensatedPP);
			
			double singleAmplifierConsumption;
			if (amplificationRate != 0) {
				singleAmplifierConsumption = amplModel.getPowerConsumption(perAmpGain, ampOutputLevel);
			} else {
				singleAmplifierConsumption = 0;
			}		
			if (singleLaserTransceiverConsumption + singleNonLaserTransceiverConsumption > worseCaseLaserCons + worseCaseNonLaserCons) {
				worseCaseLaserCons = singleLaserTransceiverConsumption;
				worseCaseNonLaserCons = singleNonLaserTransceiverConsumption;
			}
			
			transLaserPowerAccum += singleLaserTransceiverConsumption * atDist.getSecond();
			transNonLaserPowerAccum += singleNonLaserTransceiverConsumption * atDist.getSecond();
			
			ampPowerAccum += singleAmplifierConsumption * nbAmps * atDist.getSecond();
			
			DataPoint atDistDP = dp.getDerivedDataPoint();
			atDistDP.addProperty("Hops", hops);
			atDistDP.addResultProperty("tranceiver output level at dist (dBm)", transceiverOutputLevel);
			
			atDistDP.addResultProperty("trans laser consumption at dist (mW)", singleLaserTransceiverConsumption);
			atDistDP.addResultProperty("trans non laser consumption at dist (mW)", singleNonLaserTransceiverConsumption);

			
			atDistDP.addResultProperty("number of dest at dist", atDist.getSecond());
			atDistDP.addResultProperty("total laser trans cons. at dist", singleLaserTransceiverConsumption * atDist.getSecond());
			atDistDP.addResultProperty("total non laser trans cons. at dist", singleNonLaserTransceiverConsumption * atDist.getSecond());

			
			execution.addDataPoint(atDistDP);
			
			totCheck += atDist.getSecond();
		}	
		
		DataPoint atDistDP = dp.getDerivedDataPoint();
		
		atDistDP.addProperty("Hops", "sum");
		atDistDP.addResultProperty("total laser cons. at dist (mW)", transLaserPowerAccum);
		atDistDP.addResultProperty("total non laser cons. at dist (mW)", transNonLaserPowerAccum);
		
		execution.addDataPoint(atDistDP);
		
	
		localGlobal.addResultProperty("Worse case PP", worseCasePP);
		

		double singleSwitchConsumption = switchModel.getSwitchConsumption(radix);		
		localGlobal.addResultProperty("Switch consumption", singleSwitchConsumption);
		
		double averageLaserConsumption;
		double averageNonLaserConsumption;
		if (worseCase) {
			averageLaserConsumption = worseCaseLaserCons;
			averageNonLaserConsumption = worseCaseNonLaserCons;
		} else {
			averageLaserConsumption = transLaserPowerAccum/(double)totCheck;
			averageNonLaserConsumption = transNonLaserPowerAccum/(double)totCheck;
			atDistDP = dp.getDerivedDataPoint();
			atDistDP.addProperty("Hops", "average");
			atDistDP.addResultProperty("trans laser consumption at dist", averageLaserConsumption);	
			atDistDP.addResultProperty("trans non laser consumption at dist", averageNonLaserConsumption);				
			execution.addDataPoint(atDistDP);
		}
		double averageAmpConsumption = ampPowerAccum/(double)totCheck;
		
		localGlobal.addResultProperty("Average laser consumption per channel", averageLaserConsumption);
		localGlobal.addResultProperty("Average non laser consumption per channel", averageNonLaserConsumption);

		localGlobal.addResultProperty("Average amp consumption per channel", averageAmpConsumption);
		
		double averageTotCon = (averageAmpConsumption + averageLaserConsumption + averageNonLaserConsumption) * nbWavelengths;
		
		localGlobal.addResultProperty("Average total connection consumption", averageTotCon);
		localGlobal.addResultProperty("Total switch consumption", singleSwitchConsumption*numberOfSwitches*numberOfPlanes);
		
		double totalConsumption;
		double totalConsumptionLaser;
		double totalConsumptionNonLaser;
		if (proportional) {	
			totalConsumption = averageTotCon*N*bwPerPlane*numberOfPlanes;
			totalConsumptionLaser = averageLaserConsumption*N*bwPerPlane*numberOfPlanes* nbWavelengths;
			totalConsumptionNonLaser = averageNonLaserConsumption*N*bwPerPlane*numberOfPlanes* nbWavelengths;
		} else {
			totalConsumption = averageTotCon*N*numberOfPlanes;
			totalConsumptionLaser = averageLaserConsumption*N*numberOfPlanes* nbWavelengths;
			totalConsumptionNonLaser = averageNonLaserConsumption*N*numberOfPlanes* nbWavelengths;
		}
		
		localGlobal.addResultProperty("Total transceivers consumption", totalConsumption);			
		totalConsumption += singleSwitchConsumption*numberOfSwitches*numberOfPlanes;		
		localGlobal.addResultProperty("Total consumption in kW", totalConsumption/1e6d);

		if (!Double.isInfinite(totalConsumptionLaser)) {
			localGlobal.addResultProperty("Energy efficiency", totalConsumption/machMod.getTotalBandwidthInGbs());
			localGlobal.addResultProperty("Energy efficiency (laser)",totalConsumptionLaser/machMod.getTotalBandwidthInGbs() );
		}
		
		localGlobal.addResultProperty("Energy efficiency (switch)",singleSwitchConsumption*numberOfSwitches*numberOfPlanes/machMod.getTotalBandwidthInGbs() );
		localGlobal.addResultProperty("Energy efficiency (non-laser)",totalConsumptionNonLaser/machMod.getTotalBandwidthInGbs() );
		
		
		execution.addDataPoint(localGlobal);
		
		return totalConsumption;
	}

}
