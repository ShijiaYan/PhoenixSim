package PhotonicElements.Waveguides.CurvedWaveguide.BendLoss;

import PhotonicElements.Utilities.MathLibraries.AdaptiveIntegral;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.CurveModel.AbstractCurveModel;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLoss.CurveModel.LossModel;
import flanagan.integration.IntegralFunction;

public class BendLossCalculate {

	LossModel lossModel ;
	AbstractCurveModel curveModel ;

	public BendLossCalculate(
			LossModel lossModel,
			AbstractCurveModel curveModel
			){
		this.lossModel = lossModel ;
		this.curveModel = curveModel ;
	}

	public double getLossDB(double tStart, double tEnd){
		IntegralFunction func = t -> lossModel.getAlpha(curveModel.getRadiusOfCurvature(t))*curveModel.getDS(t)*1e-4;

		AdaptiveIntegral integral = new AdaptiveIntegral(func, tStart, tEnd) ;
		return integral.getIntegral() ;
	}

	public double getLossDBperCm(double tStart, double tEnd){
		return getLossDB(tStart, tEnd)/curveModel.getLength(tStart, tEnd);
	}

	public LossModel getLossModel() {
		return lossModel;
	}

	public AbstractCurveModel getCurveModel() {
		return curveModel;
	}

}
