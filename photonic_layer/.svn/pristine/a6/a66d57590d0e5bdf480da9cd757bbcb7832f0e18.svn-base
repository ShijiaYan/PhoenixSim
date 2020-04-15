package PhotonicElements.InputSources;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class SingleLambdaCWLaser extends AbstractInputSource {

	double lambdaSourceNm ;
	double constantPowerMW ;
	double sourceLineWidthMHz ;
	double sourceLineWidthNm ;
	double efficiency ;
	double phaseDegree ;
	boolean isON ;
	
	public SingleLambdaCWLaser(
			@ParamName(name="Is the Source ON?") boolean isON,
			@ParamName(name="Wavelength of CW Source (nm)", default_="1550") double lambdaSourceNm,
			@ParamName(name="CW Source Linewidth (MHz)", default_="100") double sourceLinewidthMHz,
			@ParamName(name="Power Level (mW)", default_="1") double constantPowerMW,
			@ParamName(name="Input Phase of Electric Field (degree)", default_="0") double phaseDegree ,
			@ParamName(name="Wall Plug Efficiency (0<...<1)", default_="0.1") double efficiency
			){
		this.isON = isON ;
		this.lambdaSourceNm = lambdaSourceNm ;
		this.sourceLineWidthMHz = sourceLinewidthMHz ;
		this.sourceLineWidthNm = sourceLinewidthMHz * (1/1000) * (1/125) ;
		this.phaseDegree = phaseDegree ;
		if(isON){
			this.constantPowerMW = constantPowerMW ;
		}
		else{
			this.constantPowerMW = 0 ;
		}
		this.efficiency = efficiency ;
	}

	@Override
	public double getPowerAtInputWavelengthMW(Wavelength inputLambda) {
		double inputLambdaNm = inputLambda.getWavelengthNm() ;
		if(Math.abs(inputLambdaNm-lambdaSourceNm)<=sourceLineWidthNm){
			return constantPowerMW;
		}
		else{
			return 0 ;
		}
	}

	@Override
	public Complex getElectricFieldAtInputWavelength(Wavelength inputLambda) {
		double inputLambdaNm = inputLambda.getWavelengthNm() ;
		if(Math.abs(inputLambdaNm-lambdaSourceNm)<=sourceLineWidthNm){
			double phaseRadian = phaseDegree * Math.PI/180 ;
			double amplitude = Math.sqrt(constantPowerMW) ;
			Complex phase = new Complex(phaseRadian, 0) ;
			Complex Efield = phase.times(new Complex(0,1)).exp().times(amplitude) ;
			return  Efield ;
		}
		else{
			return new Complex(0,0) ;
		}
	}

	@Override
	public double getWallPlugEfficiency(Wavelength inputLambda) {
		return efficiency ;
	}

	@Override
	public double getPowerAtInputWavelengthdBm(Wavelength inputLambda) {
		double inputLambdaNm = inputLambda.getWavelengthNm() ;
		if(Math.abs(inputLambdaNm-lambdaSourceNm)<=sourceLineWidthNm){
			return 10*Math.log10(constantPowerMW) ;
		}
		else{
			return -100 ; // a very low power level (-infinity dBm)
		}
	}

	@Override
	public String getInputSourceState() {
		if(isON){
			return "ON-" ;
		}
		else{
			return "OFF-" ;
		}
	}

	@Override
	public double getPhaseOfElectricFieldDegree(Wavelength inputLambda) {
		return phaseDegree;
	}

	@Override
	public double getPhaseOfElectricFieldRadian(Wavelength inputLambda) {
		return phaseDegree*Math.PI/180 ;
	}
	



}
