package Simulations.rings.addDrop;

import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.InputSources.OOKSource;
import PhotonicElements.RingStructures.BackScattering.ClosedForm.AddDropSymmetricBS;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.TerminatorAndReflector.LumpedReflector;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class AddDropBSOOK implements Experiment {

	OOKSource Source ;
	double inputKappa, outputKappa, radius_um, r, lambda_nm ;
	WgProperties WgProp ;
	
	
	public AddDropBSOOK(
			OOKSource source,
			Wavelength inputLambda,
			WgProperties wgProp,
			@ParamName(name="input kappa") double inputKappa,
			@ParamName(name="output kappa") double outputKappa,
			@ParamName(name="radius (um)") double radius_um,
			@ParamName(name="r") double r
			) {
		this.Source = source ;
		this.WgProp = wgProp ;
		this.inputKappa = inputKappa ;
		this.outputKappa = outputKappa ;
		this.radius_um = radius_um ;
		this.r = r ;
		this.lambda_nm = inputLambda.getWavelengthNm() ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint();
		StripWg stripWg = new StripWg(new Wavelength(lambda_nm), WgProp.getWidthNm(), WgProp.getHeightNm()) ;
		ModeStripWgTE modeTE = new ModeStripWgTE(stripWg, 0, 0) ;
		double neff = modeTE.getEffectiveIndex() ;
		double phi_rad = 2*Math.PI/(lambda_nm*1e-9)* neff *(2*Math.PI*radius_um*1e-6) ;
		AddDropSymmetricBS adr = new AddDropSymmetricBS(inputKappa, outputKappa, 1, phi_rad, new LumpedReflector(r, 0)) ;
		dp.addProperty("Wavelength (nm)", lambda_nm);
		dp.addResultProperty("Drop Spectrum (dB)",  MoreMath.Conversions.todB(Source.getPowerAtInputWavelengthMW(new Wavelength(lambda_nm))*Source.getDataRateGbps()*1e9)+
				MoreMath.Conversions.todB(adr.S41.absSquared()));
		dp.addResultProperty("OOK Spectrum (dB)", MoreMath.Conversions.todB(Source.getPowerAtInputWavelengthMW(new Wavelength(lambda_nm))*Source.getDataRateGbps()*1e9));
		dp.addResultProperty("Drop (dB)", MoreMath.Conversions.todB(adr.S41.absSquared()));
		man.addDataPoint(dp);
	}

}
