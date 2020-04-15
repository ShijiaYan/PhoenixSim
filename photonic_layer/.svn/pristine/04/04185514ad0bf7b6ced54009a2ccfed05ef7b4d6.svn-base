package Simulations.rings.addDrop;

import PhotonicElements.RingStructures.AddDrop.AddDropSecondOrder;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class TestAddDropRingSecondOrder implements Experiment {

	double radiusMicronLowerRing ;
	double radiusMicronUpperRing ;
	double radiusMicron ;
	double inputKappa ;
	double middleKappa ;
	double outputKappa ;
	
	AddDropSecondOrder ring ;
	Wavelength inputLambda ;
	
	public TestAddDropRingSecondOrder(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda ,
			@ParamName(name="Set waveguide properties") WgProperties wgProp ,
			@ParamName(name="Radius of the rings (micron)") double radiusMicron,
//			@ParamName(name="Radius of the first ring (micron)") double radiusMicronLowerRing,
//			@ParamName(name="Radius of the second ring (micron)") double radiusMicronUpperRing,
			@ParamName(name="input Kappa") double inputKappa,
			@ParamName(name="middle Kappa") double middleKappa
//			@ParamName(name="output Kappa") double outputKappa
			) {
		this.inputKappa = inputKappa ;
		this.middleKappa = middleKappa ;
//		this.outputKappa = outputKappa ;
		this.outputKappa = inputKappa ;
		this.inputLambda = inputLambda ;
		this.radiusMicron = radiusMicron ;
//		this.ring = new AddDropSecondOrder(inputLambda, wgProp, radiusMicronLowerRing, radiusMicronUpperRing, 180, 180, inputKappa, middleKappa, outputKappa) ;
		this.ring = new AddDropSecondOrder(inputLambda, wgProp, radiusMicron, radiusMicron, inputKappa, middleKappa, outputKappa) ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		
		dp.addProperty("Wavelength (nm)", inputLambda.getWavelengthNm()) ;
		dp.addProperty("middle kappa", middleKappa);
		dp.addProperty("input Kappa", inputKappa);
		dp.addProperty("output Kappa", outputKappa); 
		dp.addProperty("Radius (micron)", radiusMicron);
		dp.addResultProperty("Thru Transmission (dB)", MoreMath.Conversions.todB(ring.S21.absSquared()));
		dp.addResultProperty("Drop Transmission (dB)", MoreMath.Conversions.todB(ring.S31.absSquared()));
		dp.addResultProperty("|S41|^2 (dB)", MoreMath.Conversions.todB(ring.S41.absSquared()));
		dp.addResultProperty("|S11|^2", ring.S11.absSquared());
		dp.addResultProperty("|S21|^2", ring.S21.absSquared());
		dp.addResultProperty("|S31|^2", ring.S31.absSquared());
		dp.addResultProperty("|S41|^2", ring.S41.absSquared());
		man.addDataPoint(dp);
		
	}

}
