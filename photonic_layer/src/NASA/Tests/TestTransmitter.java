package NASA.Tests;

import NASA.Link.Transmitter;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestTransmitter implements Experiment {

	Transmitter trans ;
	
	public TestTransmitter(
			Transmitter trans 
			){
		this.trans = trans ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Number of Channels", trans.getNumberOfChannels());
		dp.addResultProperty("Power Penalty (dB)", trans.getTotalPenalty());
		dp.addResultProperty("Wg loss (dB)", trans.getWgLossdB());
		dp.addResultProperty("Modulator IL (dB)", trans.getModulatorILdB());
		dp.addResultProperty("Modulator ER", trans.getModulatorER());
		dp.addResultProperty("Modulator OOK", trans.getModulatorOOK());
		dp.addResultProperty("Modulator Penalty", trans.getModulatorPenaltydB());
		dp.addResultProperty("Grating Loss (dB)", trans.getGratingLossdB());
		dp.addResultProperty("Channel Spacing (nm)", trans.getChannelSpacingNm());
		man.addDataPoint(dp);
	}

}
