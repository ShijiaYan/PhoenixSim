package Simulations.rings.addDrop;

import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.RingStructures.AddDrop.AddDropTunableBW;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestTunableAddDrop implements Experiment {

	AddDropTunableBW ring ;
	Wavelength inputLambda ;
	SimpleHeater H1, H2 ;
	double radiusMicron ;
	
	public TestTunableAddDrop(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="Angle of Tuning Region (degree)") double angleDegree,
			@ParamName(name="input Kappa 1") double inputKappa1,
			@ParamName(name="input Kappa 2") double inputKappa2,
			@ParamName(name="input Kappa 3") double inputKappa3,
			@ParamName(name="input Kappa 4") double inputKappa4,
			@ParamName(name="Heater for WG1") SimpleHeater H1,
			@ParamName(name="Heater for WG2") SimpleHeater H2
			){
		this.radiusMicron = radiusMicron ;
		this.inputLambda = inputLambda ;
		this.H1 = H1 ;
		this.H2 = H2 ;
		this.ring = new AddDropTunableBW(inputLambda, wgProp, radiusMicron, angleDegree, inputKappa1, inputKappa2, inputKappa3, inputKappa4, H1, H2) ;
	}
	

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addResultProperty("Thru Transmission (dB)", 10*Math.log10(ring.S21.absSquared()));
		dp.addResultProperty("Drop Transmission (dB)", 10*Math.log10(ring.S41.absSquared()));
		dp.addProperty("Heater 1 Phase shift (degree)", H1.getPhaseShiftDegree());
		dp.addProperty("Heater 2 Phase shift (degree)", H2.getPhaseShiftDegree());
		dp.addProperty("Kappa", ring.DC1.getKappa());

		man.addDataPoint(dp);
	}

}
