package Simulations.rings.addDrop;


import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.RingStructures.AddDrop.AddDropFirstOrder;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;


public class TestAddDropRingHeater implements Experiment{

	AddDropFirstOrder ring ;
	
	public TestAddDropRingHeater(
				@ParamName(name="Set Working Wavelength (nm)") Wavelength inputLambda,
				@ParamName(name="Choose Waveguide Properties") WgProperties wgProp,
				@ParamName(name="Radius (micron)") double radiusMicron ,
				@ParamName(name="input Kappa") double inputKappa ,
				@ParamName(name="output Kappa") double outputKappa,
				@ParamName(name="Thermal Heater") SimpleHeater H
			){
		this.ring = new AddDropFirstOrder(inputLambda, wgProp, radiusMicron, 180, inputKappa, outputKappa, true, H) ; // symmetric structure
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
			
		DataPoint dp = new DataPoint() ;
		
		dp.addProperty("Ring radius (micron)", ring.radiusMicron);
		dp.addProperty("Wavelength (nm)", ring.inputLambda.getWavelengthNm());
		dp.addProperty("input Kappa", ring.DC1.getKappa());
		dp.addProperty("output Kappa", ring.DC2.getKappa());
		dp.addProperty("Heater Phase Shift (degree)", ring.H.getPhaseShiftDegree()); // two curved waveguides
		
		dp.addResultProperty("Thru Transmission (dB)", MoreMath.Conversions.todB(ring.S21.absSquared()));
		dp.addResultProperty("Drop Transmission (dB)", MoreMath.Conversions.todB(ring.S41.absSquared()));
		
		man.addDataPoint(dp);
	}

}
