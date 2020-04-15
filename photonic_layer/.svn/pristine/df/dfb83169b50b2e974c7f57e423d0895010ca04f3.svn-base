package NASA.Tests;

import NASA.Modulator.GrapheneRingModulator;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestGrapheneModulator implements Experiment {

	GrapheneRingModulator ringMod ;
//	double fermi_ev ;
	
	public TestGrapheneModulator(
			GrapheneRingModulator ringMod
//			double fermi_ev
			) {
		this.ringMod = ringMod ;
//		this.fermi_ev = fermi_ev ;
		
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", ringMod.getInputLambda().getWavelengthNm());
		dp.addProperty("Graphene Voltage (V)", ringMod.getVoltage());
		dp.addProperty("Fermi Level (eV)", ringMod.getGraphene().getFermiLevel_eV());
		dp.addResultProperty("Fermi Level (eV)", ringMod.getGraphene().getFermiLevel_eV());
		double deltaPhi = ringMod.getModulator().getDeltaPhi(ringMod.getInputLambda()) ;
		dp.addResultProperty("|S21|^2 (dB)", MoreMath.Conversions.todB(ringMod.getModulator().getS21(deltaPhi).absSquared()));
		dp.addResultProperty("|S21|^2 ", ringMod.getModulator().getS21(deltaPhi).absSquared());
		dp.addResultProperty("Kappa", ringMod.getKappa());
		dp.addResultProperty("BW (GHz)", ringMod.getModulator().getBandwidthHz()/1e9);
/*		dp.addProperty("Fermi Level (eV)", fermi_ev);
		dp.addResultProperty("Re(Neff)", ringMod.getRealNeff(fermi_ev));
		dp.addResultProperty("Imag(Neff)", ringMod.getImagNeff(fermi_ev));*/
		man.addDataPoint(dp);
	}

}
