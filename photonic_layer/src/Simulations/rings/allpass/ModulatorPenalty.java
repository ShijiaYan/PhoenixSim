package Simulations.rings.allpass;

import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.RingStructures.AllPass.RingWgLocked;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.ConstantBendLossModel;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class ModulatorPenalty implements Experiment {

	RingWgLocked ring0, ring1 ;

	public ModulatorPenalty(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="radius (um)") double radiusMicron,
			@ParamName(name="kappa") double kappa,
			@ParamName(name="shift of resonance (nm)") double resShiftNm,
			@ParamName(name="loss (dB/cm) @ no doping") double alpha0,
			@ParamName(name="loss (dB/cm) @ with doping") double alpha1
			) {
		ring0 = new RingWgLocked(inputLambda, new WgProperties(1000, 0, 1.1, new ConstantBendLossModel(alpha0)), radiusMicron, new CompactCoupler(kappa), 1550) ;
		ring1 = new RingWgLocked(inputLambda, new WgProperties(1000, 0, 1.1, new ConstantBendLossModel(alpha1)), radiusMicron, new CompactCoupler(kappa), 1550-resShiftNm) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		double er = ring1.getS21().absSquared()/ring0.getS21().absSquared() ;
		double ER_dB = Math.abs(MoreMath.Conversions.todB(er)) ;
		double er_penalty_dB = Math.abs(MoreMath.Conversions.todB(Math.abs((er+1)/(er-1)))) ;
		double IL_dB = Math.abs(MoreMath.Conversions.todB(ring1.getS21().absSquared())) ;
		double OOK_penalty_dB = Math.abs(MoreMath.Conversions.todB((er+1)/(2*er))) ;
		if(er<1){
			er = 1/er ;
			ER_dB = Math.abs(MoreMath.Conversions.todB(er)) ;
			er_penalty_dB = Math.abs(MoreMath.Conversions.todB(Math.abs((er+1)/(er-1)))) ;
			IL_dB = Math.abs(MoreMath.Conversions.todB(ring0.getS21().absSquared())) ;
			OOK_penalty_dB = Math.abs(MoreMath.Conversions.todB((er+1)/(2*er))) ;
		}

		DataPoint dp = new DataPoint() ;
		dp.addProperty("Laser Wavelength (nm)", ring0.getWavelength().getWavelengthNm());
		dp.addResultProperty("ring0_thru (dB)", MoreMath.Conversions.todB(ring0.getS21().absSquared()));
		dp.addResultProperty("ring1_thru (dB)", MoreMath.Conversions.todB(ring1.getS21().absSquared()));
		dp.addResultProperty("ER (dB)", ER_dB);
		dp.addResultProperty("ER penalty (dB)", er_penalty_dB);
		dp.addResultProperty("IL (dB)", IL_dB);
		dp.addResultProperty("Modulator Penalty (dB)", er_penalty_dB+IL_dB+OOK_penalty_dB);
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.rings.allpass.ModulatorPenalty" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
