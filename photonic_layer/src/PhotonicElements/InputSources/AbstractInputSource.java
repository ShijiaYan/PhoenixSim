package PhotonicElements.InputSources;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;


public abstract class AbstractInputSource {
	
	public abstract String getInputSourceState() ;
	public abstract double getPowerAtInputWavelengthMW(Wavelength inputLambda) ;
	public abstract double getPowerAtInputWavelengthdBm(Wavelength inputLambda) ;
	public abstract Complex getElectricFieldAtInputWavelength(Wavelength inputLambda) ;
	public abstract double getPhaseOfElectricFieldDegree(Wavelength inputLambda) ;
	public abstract double getPhaseOfElectricFieldRadian(Wavelength inputLambda) ;
	public abstract double getWallPlugEfficiency(Wavelength inputLambda) ;


}
