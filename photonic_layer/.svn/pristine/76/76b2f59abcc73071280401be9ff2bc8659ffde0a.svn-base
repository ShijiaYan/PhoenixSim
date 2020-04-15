package PhotonicElements.InputSources;

import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import ch.epfl.general_libraries.clazzes.ParamName;

public class OOKSource extends AbstractInputSource {

	double lambdaSourceNm ;
	double freqSourceHz ;
	double constantPowerMW ;
	double dataRateGbps ;
	double efficiency ;
	double phaseDegree ;
	boolean isON ;
	
	public OOKSource(
			@ParamName(name="Is the Source ON?") boolean isON,
			@ParamName(name="Center Wavelength (nm)", default_="1550") double lambdaSourceNm,
			@ParamName(name="Data Rate (Gbps)", default_="10") double dataRateGbps,
			@ParamName(name="Average Modulation Power (mW)", default_="1") double constantPowerMW,
			@ParamName(name="Input Phase of Electric Field (degree)", default_="0") double phaseDegree ,
			@ParamName(name="Wall Plug Efficiency (0<...<1)", default_="0.1") double efficiency
			){
		this.isON = isON ;
		this.lambdaSourceNm = lambdaSourceNm ;
		this.dataRateGbps = dataRateGbps ;
		this.phaseDegree = phaseDegree ;
		if(isON){
			this.constantPowerMW = constantPowerMW ;
		}
		else{
			this.constantPowerMW = 0 ;
		}
		this.efficiency = efficiency ;
		this.freqSourceHz = 3e8/(lambdaSourceNm*1e-9) ;
	}

	@Override
	public double getPowerAtInputWavelengthMW(Wavelength inputLambda) {
		double inputFreqHz = inputLambda.getFreqHz() ;
		double rate = dataRateGbps * 1e9 ;
		double arg = (inputFreqHz-freqSourceHz)/rate ;
		if (arg == 0){
			return constantPowerMW/rate ;
		}
		else{
			return constantPowerMW/rate * Math.pow(Math.sin(Math.PI*arg)/(Math.PI*arg), 2) ;
		}
		
	}

	@Override
	public Complex getElectricFieldAtInputWavelength(Wavelength inputLambda) {
			double phaseRadian = phaseDegree * Math.PI/180 ;
			double amplitude = Math.sqrt(getPowerAtInputWavelengthMW(inputLambda)) ;
			Complex phase = new Complex(phaseRadian, 0) ;
			Complex Efield = phase.times(new Complex(0,1)).exp().times(amplitude) ;
			return  Efield ;
	}

	@Override
	public double getWallPlugEfficiency(Wavelength inputLambda) {
		return efficiency ;
	}

	@Override  // have to be careful with this one, spectral power is mW/Hz
	public double getPowerAtInputWavelengthdBm(Wavelength inputLambda) {
		double inputFreqHz = inputLambda.getFreqHz() ;
		double rate = dataRateGbps * 1e9 ;
		double arg = (inputFreqHz-freqSourceHz)/rate ;
		if (arg == 0){
			return 10*Math.log10(constantPowerMW) ;
		}
		else{
			return 10*Math.log10(constantPowerMW * Math.pow(Math.sin(Math.PI*arg)/(Math.PI*arg), 2)) ;
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
	
	public double getDataRateGbps(){
		return dataRateGbps ;
	}


}
