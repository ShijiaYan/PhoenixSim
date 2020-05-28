package archives.common.physical_layer;

import java.util.Map;







import archives.common.physical_layer.die.AbstractDieModel;
import archives.common.physical_layer.loss_models.AbstractSwitchingRingModel;
import archives.common.physical_layer.loss_models.DefaultCombSwitchLossModel;
import archives.common.physical_layer.loss_models.DefaultModDemodulatorLossModel;
import archives.common.physical_layer.loss_models.PscanResult;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;


public class PscanPhyExperiment implements Experiment {

	AbstractDieModel die;
	AbstractSwitchingRingModel switchRingMod;
	DefaultModDemodulatorLossModel modDemoMod;
	GlobalConstantSet gloco;

	double lossPerCMInDB;
	double singleCouplerLoss;
	double jitterPP;
	double photoDiodeSensitivity;
	double powerThreshold;
	double guardBandDuration;
	double useRatio;
	int blockSize;
	ConfigFlags flags;

	public PscanPhyExperiment(
			@ParamName(name = "Die model") AbstractDieModel die,
			@ParamName(name = "Global constants set") GlobalConstantSet gloco) {
		this(die, new DefaultCombSwitchLossModel(),
				new DefaultModDemodulatorLossModel(), gloco, 0.92, 1, 2, -22,
				125, 3, 1, 4096, new ConfigFlags(false, true, false, false));
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Loss per cm in dB", lossPerCMInDB + "");
		map.put("Single coupler loss in dB", singleCouplerLoss + "");
		map.put("Jitter power penalty in dB", jitterPP + "");
		map.put("Photodiode sensitivity in dBm", photoDiodeSensitivity + "");
		map.put("Power threshold in mw", powerThreshold + "");
		map.put("Guardband duration", guardBandDuration + "");
		map.put("Use ratio", useRatio + "");
		map.put("Block size per site in bits", blockSize + "");
		map.putAll(die.getAllParameters());
		map.putAll(switchRingMod.getAllParameters());
		map.putAll(modDemoMod.getAllParameters());
		map.putAll(gloco.getAllParameters());
		return map;
	}

	public static class ConfigFlags {
		boolean lossDetail;
		boolean lossGroupDetail;
		boolean optAndCompDetail;
		boolean specDataDetail;

		public ConfigFlags(
				@ParamName(name = "Loss detail", default_ = "false") boolean lossDetail,
				@ParamName(name = "Loss group detail", default_ = "true") boolean lossGroupDetail,
				@ParamName(name = "Opt and comp details", default_ = "false") boolean optAndCompDetail,
				@ParamName(name = "Spec data details", default_ = "false") boolean specDataDetail) {
			this.lossDetail = lossDetail;
			this.lossGroupDetail = lossGroupDetail;
			this.optAndCompDetail = optAndCompDetail;
			this.specDataDetail = specDataDetail;
		}
	}

	public PscanPhyExperiment(
			@ParamName(name = "Die model") AbstractDieModel die,
			@ParamName(name = "Switch ring model") AbstractSwitchingRingModel switchRingMod,
			@ParamName(name = "Modulator and filter model") DefaultModDemodulatorLossModel modDemoMod,
			@ParamName(name = "Global constants set") GlobalConstantSet gloco,
			@ParamName(name = "Loss per cm in dB", default_ = ".92") double lossPerCMInDB, // 0.92
			@ParamName(name = "Single coupler loss in dB", default_ = "1") double singleCouplerLoss, // 1
			@ParamName(name = "Jitter power penalty in dB", default_ = "2") double jitterPP, // 2
			@ParamName(name = "Photodiode sensitivity in dBm", default_ = "-22") double photoDiodeSensitivity, // -22
			@ParamName(name = "Power threshold in mw", default_ = "125") double powerThreshold, // 125
			@ParamName(name = "Guardband duration in ns", default_ = "3") double guardBandDuration, // 3
			@ParamName(name = "Use ratio", default_ = "1") double useRatio, // 1
			@ParamName(name = "Block size per site in bits", default_ = "4096") int blockSize,
			@ParamName(name = "Data output flags", defaultClass_ = PscanPhyExperiment.ConfigFlags.class) ConfigFlags flags) {
		this.die = die;
		this.switchRingMod = switchRingMod;
		this.modDemoMod = modDemoMod;
		this.gloco = gloco;
		this.lossPerCMInDB = lossPerCMInDB;
		this.singleCouplerLoss = singleCouplerLoss;
		this.jitterPP = jitterPP;
		this.photoDiodeSensitivity = photoDiodeSensitivity;
		this.powerThreshold = powerThreshold;
		this.guardBandDuration = guardBandDuration;
		this.useRatio = useRatio;
		this.blockSize = blockSize;
		this.flags = flags;
	}

	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		
		if (!gloco.isSiteFixed() || !gloco.isWavelengthFixed()) {
			int sites = 16;
			int min = 0;
			int max = -1;
			while (min != max) {
				if (!gloco.isSiteFixed()) {
					gloco.setNumberOfSites(sites);
				} else {
					gloco.setNumberOfWavelengths(sites);
				}
				if (tryAndstore(man, false)) {
					min = Math.max(min, sites);
					if (max < 0)
						sites *= 2;
					else
						sites = min
								+ (int) Math.ceil((double) (max - min) / 2d);
				} else {
					if (max < 0)
						max = sites - 1;
					else
						max = Math.min(sites - 1, max);
					sites = min + (max - min) / 2;
				}
			}
			if (!gloco.isSiteFixed()) {
				gloco.setNumberOfSites(max);
			} else {
				gloco.setNumberOfWavelengths(max);
			}
			tryAndstore(man, true);
		} else {
			tryAndstore(man, true);
		}
	}

	private boolean tryAndstore(AbstractResultsManager man, boolean store) {
		try {

		//	int bitPerBranch = blockSize * gloco.getSitesPerBranch();
			
			//for basic bus, bit per branch = block size
		//	if( gloco.getSitesPerBranch() == gloco.getNumberOfCores() ) {
		//		bitPerBranch = blockSize;
		//	}
			
		//	double nsForTheseBits = (double) bitPerBranch
		//			/ (double) (gloco.getNumberOfWavelengths() * gloco
		//					.getGbPerSecondPerChannel());
			//double useRatio = nsForTheseBits
			//		/ (nsForTheseBits + guardBandDuration);
			
			double effectiveBandwidth = useRatio
					* gloco.getNumberOfWavelengths()
					* gloco.getGbPerSecondPerChannel();

			
			if (gloco.getDual() == 2) {
				PscanResult result;
				boolean feasible;
				DataPoint glo = new DataPoint(getAllParameters());
				result = computeDS();

				// should return power in dBm
				double total_power_reqDS = result.getSumOfPowers()
						+ photoDiodeSensitivity;
				double loss_dB = result.getSumOfPowers();
				double total_powDS = Math.pow(10, total_power_reqDS / 10);
				feasible = total_powDS < powerThreshold;

				glo.addProperty("feasible", feasible + "");
				if (store && !Double.isInfinite(total_powDS)) {
					DataPoint dp = glo.getDerivedDataPoint();
					
					dp.addResultProperty("Total loss dB", loss_dB);
					
					dp.addResultProperty(
							"Required power for iSCA Switches tuning (W)",
							getPowerFromSwitchTuningDS() / 1000);
					dp.addResultProperty(
							"Required power for iSCA Modulators tuning (W)",
							getPowerFromModsTuningDS() / 1000);
					dp.addResultProperty(
							"Required power for iSCA All Modulating (W)",
							(getPowerFromModsTuningDS() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);
					// There is only one receiving at a time hence the receiver
					// circuitry doesn't have to be multiplied
					// with the number of cores.
					dp.addResultProperty(
							"Required total power iSCA (W)",
							(getLaserPowerUS(total_powDS)
									+ getPowerFromModsTuningDS()
									+ getPowerFromSwitchTuningDS()
									+ gloco.getPDandReceiverCircuitry_mW()
									* gloco.getNumberOfWavelengths() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);

					dp.addResultProperty(
							"Required total power without laser iSCA (W)",
							(getPowerFromModsTuningDS()
									+ getPowerFromSwitchTuningDS()
									+ gloco.getPDandReceiverCircuitry_mW()
									* gloco.getNumberOfWavelengths() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);

					dp.addResultProperty(
							"Required input optical power iSCA (mW)",
							total_powDS);

					if (!gloco.isWavelengthFixed()) {
						dp.addResultProperty("Max Number of Wavelength",
								gloco.getNumberOfWavelengths());

						dp.addResultProperty(
								"Max achievable bandwidth (Gb/s)",
								(double) gloco.getNumberOfWavelengths()
										* useRatio
										* gloco.getGbPerSecondPerChannel());
						
						
						double bitnanosec = gloco.getNumberOfWavelengths() * gloco.getGbPerSecondPerChannel();
						double packetLengthNS = (double)blockSize/bitnanosec;
						double use = packetLengthNS / (packetLengthNS + guardBandDuration);
						
						dp.addResultProperty(
								"Max bandwidth (Gb/s)",
								(double) gloco.getNumberOfWavelengths()
										* use
										* gloco.getGbPerSecondPerChannel());
					}
					if (!gloco.isSiteFixed())
						dp.addResultProperty("Max sites",
								gloco.getNumberOfCores());

					Execution ex = new Execution();
					result.store(ex, glo, this);
					ex.addDataPoint(dp);
					man.addExecution(ex);
				} // end store
				return feasible;

			} else {
			//	PscanResult result = null;
				boolean feasible;
				DataPoint glo = new DataPoint(getAllParameters());
			//	ResultManager resultMan = new ResultManager(glo, man);
			//	result = computeUS(resultMan);

				// for compiler to work
				double total_pow = 0;		
				double loss_dB = 0;
				
				// should return power in dBm
			//	double total_power_req = result.getSumOfPowers() + photoDiodeSensitivity;
			//	double total_pow = Math.pow(10, total_power_req / 10);
				feasible = total_pow < powerThreshold;
				

				
			//	double loss_dB = result.getSumOfPowers();

				System.out.println("LOSS: " + loss_dB);
				
				
				glo.addProperty("feasible", feasible + "");
				if (store /* && !Double.isInfinite(total_pow)*/) {
					DataPoint dp = glo.getDerivedDataPoint();

					dp.addResultProperty("Total loss dB", loss_dB);
					
					dp.addResultProperty(
							"Required power for SCA switches tuning (W)",
							getPowerFromSwitchTuningUS() / 1000);
					dp.addResultProperty(
							"Required power for SCA Modulators tuning (W)",
							getPowerFromModsTuningUS() / 1000);
					dp.addResultProperty(
							"Required power for SCA All Modulating (W)",
							(getPowerFromModsTuningUS() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);
					dp.addResultProperty(
							"Required power for SCA receiver circuitry (W)",
							gloco.getPDandReceiverCircuitry_mW() * gloco
									.getNumberOfWavelengths() / 1000);
					dp.addResultProperty("Required power for SCA laser (W)",
							getLaserPowerUS(total_pow) / 1000);
					dp.addResultProperty(
							"Required total power SCA (W)",
							(getLaserPowerUS(total_pow)
									+ getPowerFromModsTuningUS()
									+ getPowerFromSwitchTuningUS()
									+ gloco.getPDandReceiverCircuitry_mW()
									* gloco.getNumberOfWavelengths() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);
					dp.addResultProperty(
							"Required total power without laser US (W)",
							(getPowerFromModsTuningUS()
									+ getPowerFromSwitchTuningUS()
									+ gloco.getPDandReceiverCircuitry_mW()
									* gloco.getNumberOfWavelengths() + gloco
									.getModDriverPower_mW()
									* gloco.getNumberOfWavelengths()) / 1000);

					dp.addResultProperty(
							"Required total power efficiency SCA (pJ/bit)",
							(getLaserPowerUS(total_pow)
									+ getPowerFromModsTuningUS() + getPowerFromSwitchTuningUS())
									/ effectiveBandwidth
									+ gloco.getPDandReceiverCircuitry()
									+ gloco.getModDriverPower());

					dp.addResultProperty(
							"Required total power efficiency SCA for raw data (pJ/bit)",
							(getLaserPowerUS(total_pow)
									+ getPowerFromModsTuningUS() + getPowerFromSwitchTuningUS())
									/ (gloco.getNumberOfWavelengths() * gloco
											.getGbPerSecondPerChannel())
									+ gloco.getPDandReceiverCircuitry()
									+ gloco.getModDriverPower());

					dp.addResultProperty("Required input optical power (mW)",
							total_pow);
					dp.addResultProperty(
							"Required input optical power per channel (mW)",
							total_pow / (double) gloco.getNumberOfWavelengths());

					// dp.addResultProperty("Ring stab consume W",
					// getStabilisationConsumptionW());

					// dp.addResultProperty("TxRx serdes W", getSerdesW());

					// dp.addResultProperty("Number of rings in design",
					// getNumberOfRingsInDesign());

					// dp.addResultProperty("Total W without laser",
					// getSerdesW()+getStabilisationConsumptionW());
					
					double bitnanosec = gloco.getNumberOfWavelengths() * gloco.getGbPerSecondPerChannel();
					double packetLengthNS = (double)(blockSize *gloco.getSitesPerBranch())/bitnanosec;
					double use = packetLengthNS / (packetLengthNS + guardBandDuration);					
					
					dp.addResultProperty(
							"Max bandwidth (Gb/s)",
							(double) gloco.getNumberOfWavelengths()
									* use
									* gloco.getGbPerSecondPerChannel());

					if (!gloco.isWavelengthFixed()) {
						dp.addResultProperty("Max Number of Wavelength",
								gloco.getNumberOfWavelengths());

						dp.addResultProperty(
								"Max achievable bandwidth (Gb/s)",
								(double) gloco.getNumberOfWavelengths()
										* useRatio
										* gloco.getGbPerSecondPerChannel());
					}
					if (!gloco.isSiteFixed())
						dp.addResultProperty("Max sites",
								gloco.getNumberOfCores());

					Execution ex = new Execution();
				//	result.store(ex, glo, this);
					ex.addDataPoint(dp);
					man.addExecution(ex);
				} // end store
				return feasible;
			} // end else
		} // end try
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		}
	}

	// return the power from tuning the modulators in mJ
	private double getPowerFromModsTuningUS() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		// Mod for each each wavelength for each site +demod for the memory site
		double Mod_heater_power = gloco.getNumberOfWavelengths()
				* (gloco.getNumberOfCores() + 1)
				* gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT()
						/ (gloco.getNumberOfWavelengths() * fsr_freq) + 1.0 / gloco
						.getNumberOfWavelengths());

		return Mod_heater_power;
	}

	// return the power from tuning the modulators in mJ
	private double getPowerFromModsTuningDS() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		// Mod for each each wavelength for each site +demod for the memory site
		double Mod_heater_power = gloco.getNumberOfWavelengths()
				* (gloco.getNumberOfCores() + 1)
				* gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT()
						/ (gloco.getNumberOfWavelengths() * fsr_freq) + 1.0 / gloco
						.getNumberOfWavelengths());

		return Mod_heater_power;
	}

/*	private double getPowerFromModsTuningDSperRing() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		// Mod for each each wavelength for each site +demod for the memory site
		double Mod_heater_power = gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT()
						/ (gloco.getNumberOfWavelengths() * fsr_freq) + 1.0 / gloco
						.getNumberOfWavelengths());

		return (Mod_heater_power);
	}*/

	// return the power from tuning the switches in mJ. Depends on the
	// architecture it is for the thermal tuning
	private double getPowerFromSwitchTuningUS() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		int count = 2 * (int) Math.ceil((double) gloco.getNumberOfCores()
				/ gloco.getSitesPerBranch());
		if (gloco.getSitesPerBranch() == gloco.getNumberOfCores())
			count = 0;

		double Switch_heater_power = count * gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT() / fsr_freq + 0.5);

		return Switch_heater_power;
	}

	private double getPowerFromSwitchTuningDS() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		// The downsteram is without branches a simple serpentine
		int count = (int) Math.ceil(gloco.numberOfCores);

		double Switch_heater_power = count * gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT() / fsr_freq + 0.5);
		return Switch_heater_power;
	}

	
	@SuppressWarnings("unused")
	private double getPowerFromSwitchTuningPerRing() {

		double fsr_freq = gloco.getChannelSpacing() / 1e9;

		int count = (int) Math.ceil(gloco.numberOfCores);
		if (gloco.getSitesPerBranch() == gloco.getNumberOfCores())
			count = 0;

		double Switch_heater_power = gloco.getThermalPowerperFSR()
				* (0.5 * 10 * gloco.getDeltaT() / fsr_freq + 0.5);
		return Switch_heater_power;
	}

	// the total power in pico mJ
	private double getLaserPowerUS(double total_pow) {

		return total_pow / gloco.getMMlaserEfficiency(); // 0.4pJ/bit for the
															// detector bank and
															// 0.1 pJ/bit for
															// modulators driver
	}

	@SuppressWarnings("unused")
	private PscanResult computeUS(/*ResultManager resultMan*/) {
		switchRingMod.init(gloco);
		modDemoMod.init(gloco);
		die.init(gloco);

		PscanResult result = new PscanResult(gloco.getNumberOfWavelengths());

		result.addPowerDissipatedDB(
				lossPerCMInDB * die.getWaveguideLengthInCm(), "Waveguide",
				"waveguide loss");
		result.addPowerDissipatedDB(3 * singleCouplerLoss, "Couplers",
				"coupler loss");
		result.addPowerDissipatedDB(jitterPP, "Jitter", "Jitter power penalty");

		modDemoMod.getModulatorLossAndExtraArrayLoss(gloco.getSitesPerBranch(),
				result);

		modDemoMod.getDemultiplexingLossAndPenalty(result);

		int numberThroughUS;
		// 2 Drop switches for the US and 1 for the DS
		int numberDropUS = 2;

		// the factor 2 comes from the clustering architechture i.e. single bus;
		// For the dual bus it will be 1 !
		if (gloco.getDual() == 1)
			numberThroughUS = (int) Math.ceil((double) gloco
					.getNumberOfCores() / (double) gloco.getSitesPerBranch()) - 1;
		else
			numberThroughUS = 2 * ((int) Math.ceil((double) gloco
					.getNumberOfCores() / (double) gloco.getSitesPerBranch()) - 1);

		if (gloco.getSitesPerBranch() == gloco.getNumberOfCores()) {
			numberThroughUS = 0;
			numberDropUS = 0;
		}
		switchRingMod.getLossThroughILdropILandSwitchPP(result, numberDropUS,
				numberThroughUS);

		return result;
	}

	// For now (11/25/13 in the iSCA direction there4 is no branching or
	// clustering
	private PscanResult computeDS() {
		switchRingMod.init(gloco);
		modDemoMod.init(gloco);
		die.init(gloco);

		PscanResult result = new PscanResult(gloco.getNumberOfWavelengths());

		result.addPowerDissipatedDB(
				lossPerCMInDB * die.getWaveguideLengthInCm(), "Waveguide",
				"waveguide loss");
		result.addPowerDissipatedDB(3 * singleCouplerLoss, "Couplers",
				"coupler loss");
		result.addPowerDissipatedDB(jitterPP, "Jitter", "Jitter power penalty");

		modDemoMod.getModulatorLossAndExtraArrayLoss(1, result);

		modDemoMod.getDemultiplexingLossAndPenalty(result);

		int numberThroughDS = (int) Math
				.ceil(gloco.getNumberOfCores()) - 1;

		// 2 Drop switches for the US and 1 for the DS; the dS is a serpentine
		int numberDropDS = 1;
		switchRingMod.getLossThroughILdropILandSwitchPP(result, numberDropDS,
				numberThroughDS);

		return result;
	}

	// From Sebastien
	private int getNumberOfRingsInDesign() {
		int count = 2 * 2 * gloco.numberOfCores;
		if (gloco.getSitesPerBranch() == gloco.getNumberOfCores())
			count = 0;

		count += gloco.getNumberOfWavelengths() * gloco.getNumberOfCores() * 2;
		return count;
	}

	@SuppressWarnings("unused")
	private double getStabilisationConsumptionW() {
		return getNumberOfRingsInDesign() * gloco.getThermalStabConsumptionW();
	}

	@SuppressWarnings("unused")
	private double getSerdesW() {
		return 2 * gloco.nbWavelengths * gloco.getRate().getInBitsSeconds()
				* gloco.getPerBitPowerEfficiencySERDES();
	}

	public boolean withSpecificData() {
		return flags.specDataDetail;
	}

	public boolean withOptimizedAndComputerValues() {
		return flags.optAndCompDetail;
	}

	public boolean withGroupLossDetail() {
		return flags.lossGroupDetail;
	}

	public boolean withLossDetail() {
		return flags.lossDetail;
	}

}
