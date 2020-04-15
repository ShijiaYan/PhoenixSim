package Simulations.waveguides.curvedWg;

import PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.BendLossCalculate;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class CurveLossSim implements Experiment {

	BendLossCalculate bendLoss ;
	double tStart, tEnd ;

	public CurveLossSim(
			double tStart,
			double tEnd,
			BendLossCalculate bendloss
			) {
		this.tStart = tStart ;
		this.tEnd = tEnd ;
		this.bendLoss = bendloss ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Name", bendLoss.getCurveModel().getName());
		dp.addProperty("tStart", tStart);
		dp.addProperty("tEnd", tEnd);
		dp.addProperty("X", bendLoss.getCurveModel().getX(tEnd));
		dp.addResultProperty("Y", bendLoss.getCurveModel().getY(tEnd));
		dp.addProperties(bendLoss.getCurveModel().getAllParameters());
		dp.addResultProperties(bendLoss.getCurveModel().getAllParameters());
		dp.addResultProperty("Loss (dB)", bendLoss.getLossDB(tStart, tEnd));
		dp.addResultProperty("Loss (dB/cm)", bendLoss.getLossDBperCm(tStart, tEnd));
		dp.addResultProperty("radius of curvature", bendLoss.getCurveModel().getRadiusOfCurvature(tEnd));
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "PhotonicElements" ;
		String classString = "Simulations.waveguides.curvedWg.CurveLossSim" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
