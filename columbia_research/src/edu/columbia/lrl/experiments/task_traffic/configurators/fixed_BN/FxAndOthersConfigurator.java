package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FxAndOthersConfigurator extends AbstractFixedBNPConfigurator {

	public FxAndOthersConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="rhox") final double rhox, 
			@ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fx = getFx(Fx_);
            double beta = getBetaFromX(rhox, Fx);
            double Fc = getFc(rhoc, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}

	public FxAndOthersConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="F") final double F, 
			@ParamName(name="rhox") final float rhox) {
		toInvoke = () -> {
            double Fx = getFx(Fx_);
            double beta = getBetaFromX(rhox, Fx);
            double Fc = F*Fx;
            double rhoc = getRhoc(Fc, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}	
		

	public FxAndOthersConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="F") final float F, 
			@ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fx = getFx(Fx_);
            double Fc = F*Fx;
            double beta = getBetaFromC(rhoc, Fc);
            double rhox = getRhox(Fx, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}
	
	

	public FxAndOthersConfigurator(@ParamName(name="Fx") final float Fx_, 
			@ParamName(name="zeta") final double zeta, 
			@ParamName(name="rhox") final double rhox) {
		toInvoke = () -> {
            double Fx = getFx(Fx_);
            double beta = getBetaFromX(rhox, Fx);
            double xi = getXiFromZetaP(zeta, P);
            double rhoc = getRhocFromXi(rhox, xi);
            double Fc = getFc(rhoc, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}

	public FxAndOthersConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="zeta") final float zeta, 
			@ParamName(name="rhoc") final float rhoc) {
		toInvoke = () -> {
            double Fx = getFx(Fx_);
            double xi = getXiFromZetaP(zeta, P);
            double rhox = getRhoxFromXi(rhoc, xi);
            double beta = getBetaFromX(rhox, Fx);
            double Fc = getFc(rhoc, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}

}
