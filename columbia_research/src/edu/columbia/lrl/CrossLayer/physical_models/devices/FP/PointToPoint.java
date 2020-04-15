package edu.columbia.lrl.CrossLayer.physical_models.devices.FP;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import edu.columbia.lrl.CrossLayer.physical_models.devices._old.AbstractDemodulator__;

public class PointToPoint implements Experiment {
	

	 private int numCouplers = 2 ;
	 private int numChannels ;
//	 private double laserPower ;
	 private double couplerLossDB ;
	 
	 Constants ct ;
	 LaserModel laser ;
//	 DPSKMachZehnderModulator modulatorModel ;
//	 OOKMachZehnderModulator modulatorModel ;
	 AbstractMZM modulatorModel ;
	 GeDetector detec ;
	 AwgMux awgMultiplexer ;
	 AwgDemux awgDemultiplexer ;
//	 DPSKDemodulator demodulatorModel ;
	 AbstractDemodulator__ demodulatorModel ;
	 
//	 AwgSwitch awgSwitch ;
	 
	 



//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	 
	 // Constructor to initialize the input arguments
	 public PointToPoint(

		      			  @ParamName(name="Constants") Constants ct,
			 		      @ParamName(name="Number of Channels") int numChannels,
//			 			  @ParamName(name="Number of Couplers") int numCouplers,
			 			  @ParamName(name="Coupler Loss (dB)", default_ = "1") double couplerLossDB,
			 		      @ParamName(name="Laser Model", defaultClass_ = LaserModel.class) LaserModel laser,
//			 			  @ParamName(name="Modulator Model") DPSKMachZehnderModulator modulatorModel,
			 			  @ParamName(name="Modulator Model") AbstractMZM modulatorModel,
			 			  @ParamName(name="AWG multiplexer") AwgMux awgMultiplexer,
			 			  @ParamName(name="AWG Demultiplexer") AwgDemux awgDemultiplexer,
//			 		      @ParamName(name="DPSK Demodulator Model") DPSKDemodulator demodulatorModel,
			 		      @ParamName(name="Demodulator Model") AbstractDemodulator__ demodulatorModel,
//			 		      @ParamName(name="AWG Switch Model") AwgSwitch awgSwitch,
			 			  @ParamName(name="Detector") GeDetector detec
			   			  ){
		 this.modulatorModel = modulatorModel ;
		 this.detec = detec ;
//		 this.numCouplers = numCouplers ;
		 this.couplerLossDB = couplerLossDB ;
		 this.awgMultiplexer = awgMultiplexer ;
		 this.awgDemultiplexer = awgDemultiplexer ;
		 this.laser = laser ;
		 this.demodulatorModel = demodulatorModel ;
		 this.ct = ct ;
		 this.numChannels = numChannels ;
	 }
	 

	
	 
//*********************Result Manager***************************************
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		
		
		
		DataPoint dp = new DataPoint() ;
		Execution ex = new Execution() ;
		
		// Modulator Power Penalty
		 double modPowerPenalty = modulatorModel.getPassivePowerPenalties() + modulatorModel.getActivePowerPenalties() ;
		 double modPowerConsumption = modulatorModel.getModulatorPowerConsumption() ;
		 double modEnergyPerBit = modPowerConsumption / modulatorModel.getModulationRateGbps() ; 
		 
/*		 dp.addResultProperty("Passive Loss (dB)", modulatorModel.getPassivePowerPenalties() );
		 dp.addResultProperty("Modulator ERPP (dB)", modulatorModel.getModERPP() );
		 dp.addResultProperty("Modulator IL (dB)", modulatorModel.getModInsertionLoss() );
		 dp.addResultProperty("Modulator OOK (dB)", modulatorModel.getModOOK() );
		 dp.addResultProperty("Modulator Penalty (dB)", modPowerPenalty);*/
		 
/*		 dp.addResultProperty("Insertion Loss (dB)", modulatorModel.getInsertionLoss());
		 dp.addResultProperty("Duty Cycle Penalty (dB)", modulatorModel.getDutyCyclePenalty() );
		 dp.addResultProperty("Pulse Carver Penalty (dB)", modulatorModel.getPulseCarverPenalty() );
		 dp.addResultProperty("Modulation Penalty (dB)", modulatorModel.getModulationPenalty());
		 dp.addResultProperty("Total Penalty (dB)", modPowerPenalty);*/

		 
		 // AWG Mux power penalty
		 double awgMuxPowerPenalty = awgMultiplexer.getPowerPenalties(numChannels) ;
		 double awgMuxPowerConsumption = awgMultiplexer.getAwgMuxPowerConsumption(modulatorModel.getModulationRateGbps()) ;
		 
		 // AWG Demux power penalty
		 double effectiveRate = modulatorModel.getEffectiveModulationRate() ;
		 double awgDemuxPowerPenalty = awgDemultiplexer.getPowerPenalties(ct, effectiveRate, numChannels, null) ;
		 double awgDemuxPowerConsumption = awgDemultiplexer.getAwgDeMuxPowerConsumption(modulatorModel.getModulationRateGbps()) ;
		 
		 // DPSK Demodulator Power Penalty
		 double demodPowerPenalty = demodulatorModel.getPowerPenalties(modulatorModel.getModulationDutyCycle()) ;
		 double demodPowerConsumption = demodulatorModel.getDemodulatorPowerConsumption(modulatorModel.getModulationRateGbps()) ;
		 double demodEnergyPerBit = demodPowerConsumption / modulatorModel.getModulationRateGbps() ;
		 

		 double totPP = modPowerPenalty + awgMuxPowerPenalty + awgDemuxPowerPenalty + demodPowerPenalty + numCouplers * couplerLossDB ;
		 
		 double sensitivity = detec.getSensitivitydB(ct, modulatorModel.getModulationRate()) ;
		 double linkBudgetdB = laser.getLaserPowerdBm() - sensitivity ;
		 
		 double requiredLaserPowerdBm = sensitivity + totPP ;
		 double laserPowermW = Math.pow(10, requiredLaserPowerdBm/10) / laser.getLaserefficiency() ;
		 
//		 double laserPowermW = Math.pow(10, laser.getLaserPowerdBm() /10) / laser.getLaserefficiency() ;
		 
		 double totLinkPowerConsumption = (laserPowermW * 1e3 + modPowerConsumption+demodPowerConsumption)*numChannels
				 + awgMuxPowerConsumption + awgDemuxPowerConsumption ;
		 
		 double LinkEnergyPerBit = totLinkPowerConsumption/(numChannels * modulatorModel.getModulationRateGbps()) ; // Note: total aggregate rate
		 
		dp.addResultProperty("Receiver Sensitivity (dBm)", sensitivity );
		dp.addResultProperty("Modulator Penalty (dB)", modPowerPenalty);
		dp.addResultProperty("Mux Penalty (dB)", awgMuxPowerPenalty);
		dp.addResultProperty("Modulator Energy (pJ/bit)", modEnergyPerBit * 1e-3 );
		dp.addResultProperty("Receiver Energy (pJ/bit)", demodEnergyPerBit * 1e-3 );
		dp.addResultProperty("Link Energy (pJ/bit)", LinkEnergyPerBit * 1e-3 ); // report in pJ/bit
		dp.addResultProperty("Demodulator Penalty (dB)", demodPowerPenalty );
		dp.addResultProperty("DeMux Penalty (dB)", awgDemuxPowerPenalty  );
		dp.addResultProperty("Total Link Penalty (dB)", totPP  );
		dp.addResultProperty("Link Power Budget (dB)", linkBudgetdB);
		dp.addResultProperty("Required Laser Power (dBm)", requiredLaserPowerdBm);
		dp.addResultProperty("Differential Budget", linkBudgetdB-totPP );
		dp.addResultProperties(modulatorModel.getAllParameters());
		dp.addResultProperties(demodulatorModel.getAllParameters());
		dp.addResultProperties(laser.getAllParameters());
		dp.addResultProperty("Modulation Format", modulatorModel.getClass().getSimpleName());
		
		dp.addProperty("Number of Channels", numChannels);
		dp.addProperty("Data Rate (Gb/s)", modulatorModel.getModulationRate()/1e9);
		dp.addProperty("Duty Cycle", modulatorModel.getModulationDutyCycle());
		
		ex.addDataPoint(dp);
		man.addExecution(ex);
		
		
		
		
	}


}
