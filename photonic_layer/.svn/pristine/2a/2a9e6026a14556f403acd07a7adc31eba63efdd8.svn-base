package PhotonicElements.GratingCouplers;

import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class SimpleGratingCoupler {

	// Simple grating coupler is defined as a flat loss for all wavelengths
	
	double lossdB = 4 ;
	Wavelength inputLambda ;
	
	public SimpleGratingCoupler(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda
			){
		this.inputLambda = inputLambda ;
	}
	
	public double getLossdB(){
		return lossdB ;
	}
	
}
