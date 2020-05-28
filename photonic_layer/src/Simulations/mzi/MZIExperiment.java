package Simulations.mzi;

import PhotonicElements.InputSources.AbstractInputSource;
import PhotonicElements.MachZehnder.MZI;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.MathLibraries.Complex;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class MZIExperiment implements Experiment {

	Wavelength inputLambda ;
	Complex port1In ;
	PlasmaDispersionModel plasmaEffect ;
	MZI mzi ;
	
	public MZIExperiment(
			@ParamName(name="Set Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Set Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Set Input Source") AbstractInputSource inputSource,
			@ParamName(name="Set the length of the upper arm (micron)") double upperWgLengthMicron,
			@ParamName(name="Set the length of the lower arm (micron)") double lowerWgLengthMicron,
			@ParamName(name="Set Y-junction Loss (dB)", default_="0") double yLossdB, 
			@ParamName(name="Plasma Dispersion Model") PlasmaDispersionModel plasmaEffect
			){
		this.inputLambda = inputLambda ;
		this.plasmaEffect = plasmaEffect ;
		this.port1In = inputSource.getElectricFieldAtInputWavelength(inputLambda) ;
		this.mzi = new MZI(inputLambda, wgProp, yLossdB, upperWgLengthMicron, lowerWgLengthMicron, plasmaEffect, null) ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {

		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", inputLambda.getWavelengthNm());
		dp.addProperty("Length Difference (micron)", Math.abs(mzi.wg1.getWgLengthMicron()-mzi.wg2.getWgLengthMicron()));
		dp.addProperty("Delta Alpha (1/cm)", plasmaEffect.getDalphaPerCm());
		dp.addResultProperty("MZI transmission (dB)", 10*Math.log10(mzi.S21.absSquared()));
		
		man.addDataPoint(dp);
	}

}
