package Simulations.rings.addDrop;

import PhotonicElements.RingDesignSpace.AddDrop.AddDropDesignSpaceKappa;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestAddDropDesignSpaceKappa implements Experiment {

	AddDropDesignSpaceKappa dsGap ;
	
	public TestAddDropDesignSpaceKappa(
			AddDropDesignSpaceKappa dsGap
			) {
		this.dsGap = dsGap ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("radius (um)", dsGap.radiusMicron);
		dp.addResultProperty("radius (um)", dsGap.radiusMicron);
		dp.addProperty("wavelength (nm)", dsGap.inputLambda.getWavelengthNm());
//		dp.addResultProperty("input gap (nm)", dsGap.findGapNm(dsGap.getInputKappa()));
		dp.addResultProperty("input Kappa", dsGap.getInputKappa());
		dp.addResultProperty("output Kappa", dsGap.getOutputKappa());
		dp.addResultProperty("Bandwidth (GHz)", dsGap.getBandwidthHz()/1e9);
		dp.addResultProperty("Drop IL at Res", dsGap.getDropInsertionLossAtResonancedB());
		dp.addResultProperty("Drop IL at FSR/2", dsGap.getDropInsertionLossAtHalfFSRdB());
		dp.addResultProperty("Thru IL at Res", dsGap.getThruInsertionLossAtResonancedB());
		dp.addResultProperty("Thru IL at FSR/2", dsGap.getThruInsertionLossAtHalfFSRdB());
		dp.addResultProperty("FSR (nm)", dsGap.getFSRnm());
		man.addDataPoint(dp);
	}

}
