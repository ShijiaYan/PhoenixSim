package Simulations.rings.bending_loss;

import PhotonicElements.DirectionalCoupler.RingWgCoupling.RingWgCoupler;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.AbstractBendLossModel;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class FinesseRing implements Experiment {

	AbstractBendLossModel bendLoss ;
	RingWgCoupler ringWgCoupling ;

	public FinesseRing(
			@ParamName(name="Ring Wg Coupling") RingWgCoupler ringWgCoupling
			) {
		this.bendLoss = ringWgCoupling.wgProp.getBendLossModel() ;
		this.ringWgCoupling = ringWgCoupling ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		double ng = 4.393 ;
		double radiusMicron = ringWgCoupling.getRadiusMicron() ;
		double lambdaNm = ringWgCoupling.getWavelengthNm() ;
		double fsrNm = lambdaNm*lambdaNm/(2*Math.PI*radiusMicron*1e3*ng) ;
		double alphaPerMeter = bendLoss.getLossPerMeter(radiusMicron) ;
		double kappa = ringWgCoupling.getS31().abs() ;
		double finesse = 2*Math.PI/(kappa*kappa+2*Math.PI*radiusMicron*1e-6*alphaPerMeter) ;
		double bandwidthNm = fsrNm/finesse ;
		double Q = lambdaNm/bandwidthNm ;

		dp.addProperty("ring radius (um)", radiusMicron);
		dp.addResultProperty("kappa", kappa);
		dp.addProperty("gap (nm)", ringWgCoupling.getGapSizeNm());
		dp.addResultProperty("FSR (nm)", fsrNm);
		dp.addResultProperty("finesse", finesse);
		dp.addResultProperty("Q factor", Q);
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.rings.bending_loss.FinesseRing" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
