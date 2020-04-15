package edu.columbia.lrl.experiments.ring_resonators;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.math.Rounder;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import edu.columbia.lrl.CrossLayer.physical_models.devices.rings.RingResonatorModel;
import edu.columbia.lrl.CrossLayer.physical_models.util.Constants;

public class RingResonatorAnalysisExperiment implements Experiment{
	
	private RingResonatorModel ringMod;
	private int nbChannels;
	private double bitRate;
	private double spectrum;
	
	public RingResonatorAnalysisExperiment(RingResonatorModel model,
			@ParamName(name="Considered spectrum (nm)", default_="50") double spectrum,
			@ParamName(name="Number of channels", default_="1") int nbChannels,
			@ParamName(name="Bitrate in Gb/s", default_="10") double bitRate) {
		this.ringMod = model;
		this.nbChannels = nbChannels;
		this.bitRate = bitRate;
		this.spectrum = spectrum;
	}
	
	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		double fsr = ringMod.getFSRnm();		
		
		DataPoint dp = new DataPoint(ringMod.getAllParameters());
		
		double shift = 1/8d;
		
		DataPoint dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "drop loss");
		double dropLoss = ringMod.getDropTransmissiondB(0);
		dp2.addResultProperty("metric value", Rounder.roundString(dropLoss, 2));
		man.addDataPoint(dp2);
		dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "thru loss");
		double thruLoss = ringMod.getThroughTransmissiondB(fsr*shift);
		dp2.addResultProperty("metric value", Rounder.roundString(thruLoss, 2));
		man.addDataPoint(dp2);	
		dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "drop xtalk");
		double dropXtalk = ringMod.getDropTransmissiondB(fsr*shift);
		dp2.addResultProperty("metric value", Rounder.roundString(dropXtalk, 2));
		man.addDataPoint(dp2);
		dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "thru xtalk");
		double thruXtalk = ringMod.getThroughTransmissiondB(0);
		dp2.addResultProperty("metric value", Rounder.roundString(thruXtalk, 2));
		man.addDataPoint(dp2);	
		dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "sum of losses");
		dp2.addResultProperty("metric value", Rounder.roundString(thruLoss+dropLoss, 2));
		man.addDataPoint(dp2);		
		dp2 = dp.getDerivedDataPoint();
		dp2.addProperty("metric", "sum of xtalk");
		dp2.addResultProperty("metric value", Rounder.roundString(thruXtalk+dropXtalk, 2));
		man.addDataPoint(dp2);			
		
		dp.addResultProperty("Alpha ring", ringMod.getAlphaLossdBperCm());
		dp.addResultProperty("Round trip loss", ringMod.getRoundTripLoss());
		dp.addResultProperty("Attenuation max (through)", Rounder.roundString(ringMod.getThroughTransmissiondB(0), 2));
		dp.addResultProperty("Attenuation drop", Rounder.roundString(ringMod.getDropTransmissiondB(0),8));
		
		double outThrough = ringMod.getThroughTransmission(0);
		double outDrop = ringMod.getDropTransmission(0);
		
		double dissipation = 1 - outThrough - outDrop;
		
		dp.addResultProperty("thermal dissipation in ring", dissipation);
		
		dp.addResultProperty("ER_db", ringMod.getERdB());
		dp.addResultProperty("FWHM Ghz", Rounder.roundString(ringMod.get3dbBWhz()/1e9d, 2));
		dp.addResultProperty("Q factor", ringMod.getQualityFactor());

		dp.addResultProperty("FSR", fsr);
		dp.addResultProperty("Resonances max", Math.ceil(spectrum/fsr));
		dp.addResultProperty("Min insertion loss (through)", Rounder.roundString(ringMod.getThroughTransmissiondB(fsr/2d), 2));
		dp.addResultProperty("Drop crosstalk", Rounder.roundString(ringMod.getDropTransmissiondB(fsr/2d), 2));
		
		dp.addProperty("Input kappa", ringMod.getInputKappacoeff());
		dp.addProperty("input t", ringMod.getInputTcoeff());
		dp.addProperty("kappa and t square", Math.pow(ringMod.getInputKappacoeff(), 2) + Math.pow(ringMod.getInputTcoeff(),2));
		dp.addProperty("output t", ringMod.getOutputTcoeff());
		dp.addProperty("one over round trip loss", 1/ringMod.getRoundTripLoss());
		dp.addProperty("coupling ratio (t1/alpha*t2)", ringMod.getInputTcoeff()/(Math.pow(ringMod.getRoundTripLoss(), 0.5)*ringMod.getOutputTcoeff()));
		dp.addProperty("test", ringMod.getInputTcoeff()/(Math.pow(ringMod.getRoundTripLoss(), -0.5)*ringMod.getOutputTcoeff()));
		
		dp.addProperty("t1/t2", ringMod.getInputTcoeff()/ringMod.getOutputTcoeff());
		
		if (nbChannels > 1) {
			double channelSpacing = spectrum/(double)(nbChannels-1);
		
			dp.addProperty("Channel spacing (nm)", channelSpacing);
			dp.addProperty("Nb channels", nbChannels);
			
			double equivalentFreq = Constants.wavelengthToFreq(1550);
			double downBw = equivalentFreq - bitRate*1e9;
			double altWav = Constants.freqToWavelength(downBw);
			double detun = 1550 - altWav;
			
			dp.addProperty("Wavelength guard band", channelSpacing+detun);
			
			double neighIL = ringMod.getThroughTransmissiondB(channelSpacing+detun);
			double minIL = ringMod.getThroughTransmissiondB(fsr/2d);
			
			dp.addResultProperty("Other channel insertion loss", Rounder.round(Math.min(neighIL, minIL), 2));
		}		
		
		
		
		man.addDataPoint(dp);
		
	}
	


}
