package PhotonicElements.InputSources.CombLaser.GeneralComb;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.InputSources.SingleLambdaCWLaser;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class CombLaser extends AbstractInputSource {

	SingleLambdaCWLaser[] combLaser ;
	double numLines ;
	
	public CombLaser(
			@ParamName(name="Specify Properties of each line") SingleLambdaCWLaser[] combLaser
			){
		
		this.combLaser = combLaser ;
		this.numLines = combLaser.length ;
	}

	public double getNumberOfLines(){
		return numLines ;
	}

	@Override
	public String getInputSourceState() {
		String state = "-" ;
		for(int i=0; i<numLines; i++){
			state += combLaser[i].getInputSourceState() ;
		}
		return state;
	}

	@Override
	public double getPowerAtInputWavelengthMW(Wavelength inputLambda) {
		double powLineMW = 0 ;
		for(int i=0; i<numLines; i++){
			powLineMW += combLaser[i].getPowerAtInputWavelengthdBm(inputLambda) ;
		}
		return powLineMW;
	}

	@Override
	public double getPowerAtInputWavelengthdBm(Wavelength inputLambda) {
		return 10*Math.log10(getPowerAtInputWavelengthMW(inputLambda));
	}

	@Override
	public Complex getElectricFieldAtInputWavelength(Wavelength inputLambda) {
		Complex Efield = new Complex(0,0) ;
		for(int i=0; i<numLines; i++){
			Efield = Efield.plus(combLaser[i].getElectricFieldAtInputWavelength(inputLambda)) ;
		}
		return Efield;
	}

	@Override
	public double getPhaseOfElectricFieldDegree(Wavelength inputLambda) {
		Complex Efield = getElectricFieldAtInputWavelength(inputLambda) ;
		return Efield.phaseDegree();
	}

	@Override
	public double getPhaseOfElectricFieldRadian(Wavelength inputLambda) {
		Complex Efield = getElectricFieldAtInputWavelength(inputLambda) ;
		return Efield.phase();
	}

	@Override
	public double getWallPlugEfficiency(Wavelength inputLambda) {
		double eff = 0 ;
		for(int i=0; i<numLines; i++){
			eff += combLaser[i].getWallPlugEfficiency(inputLambda) ;
		}
		return eff/numLines ; // this is the average efficiency of the entire comb based on teh efficiency of all lines
	}

	public SingleLambdaCWLaser getIndividualLine(int lineNumber){
		return combLaser[lineNumber-1] ;
	}



}
