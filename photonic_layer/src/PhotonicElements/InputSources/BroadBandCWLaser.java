package PhotonicElements.InputSources;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class BroadBandCWLaser extends AbstractInputSource {

	double constantPowerMW ;
	double phaseDegree ;
	double efficiency ;
	boolean isON ;
	
	public BroadBandCWLaser(
			@ParamName(name="Is the Source ON?") boolean isON ,
			@ParamName(name="Power Level (mW)", default_="1") double constantPowerMW,
			@ParamName(name="Input Phase of Electric Field (degree)", default_="0") double phaseDegree ,
			@ParamName(name="Wall Plug Efficiency (0<...<1)", default_="0.1") double efficiency
			){
		this.isON = isON ;
		if(isON){
			this.constantPowerMW = constantPowerMW ;
		}
		else{
			this.constantPowerMW = 0 ;
		}
		this.phaseDegree = phaseDegree ;
		this.efficiency = efficiency ;
	}


	@Override
	public double getPowerAtInputWavelengthMW(Wavelength inputLambda) {
		return constantPowerMW;
	}

	@Override
	public Complex getElectricFieldAtInputWavelength(Wavelength inputLambda) {
		double phaseRadian = phaseDegree * Math.PI/180 ;
		double amplitude = Math.sqrt(constantPowerMW) ;
		Complex phase = new Complex(phaseRadian, 0) ;
		Complex Efield = phase.times(new Complex(0,1)).exp().times(amplitude) ;
		return  Efield ;

	}

	@Override
	public double getWallPlugEfficiency(Wavelength inputLambda) {
		return efficiency ;
	}

	@Override
	public double getPowerAtInputWavelengthdBm(Wavelength inputLambda) {
		return 10*Math.log10(constantPowerMW) ;

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
