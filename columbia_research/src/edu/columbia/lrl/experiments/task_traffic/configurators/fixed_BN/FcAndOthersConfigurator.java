package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;


import ch.epfl.general_libraries.clazzes.ParamName;

public class FcAndOthersConfigurator extends AbstractFixedBNPConfigurator {

	public FcAndOthersConfigurator(@ParamName(name="Fc") final double Fc_, 
								 @ParamName(name="rhox") final double rhox, 
								 @ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fc = getFc(Fc_);
            double beta = getBetaFromC(rhoc, Fc);
            double Fx = getFx(rhox, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}	
	
	public FcAndOthersConfigurator(@ParamName(name="Fc") final double Fc_, 
									@ParamName(name="F") final double F, 
									@ParamName(name="rhox") final float rhox) {
		toInvoke = () -> {
            double Fc = getFc(Fc_);
            double Fx = Fc/F;
            double beta = getBetaFromX(rhox, Fx);
            double rhoc = getRhoc(Fc, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}	

	public FcAndOthersConfigurator(@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="F") final float F, 
			@ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fc = getFc(Fc_);
            double Fx = Fc/F;
            double beta = getBetaFromC(rhoc, Fc);
            double rhox = getRhox(Fx, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}
	
	public FcAndOthersConfigurator(@ParamName(name="Fc") final float Fc_, 
			@ParamName(name="zeta") final double zeta, 
			@ParamName(name="rhox") final double rhox) {
		toInvoke = () -> {
            double Fc = getFc(Fc_);
            double xi = getXiFromZetaP(zeta, P);
            double rhoc = getRhocFromXi(rhox, xi);
            double beta = getBetaFromC(rhoc, Fc);
            double Fx = getFx(rhox, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}	

	public FcAndOthersConfigurator(@ParamName(name="Fc") final double Fc, 
			@ParamName(name="zeta") final float zeta, 
			@ParamName(name="rhoc") final float rhoc) {
		toInvoke = () -> {
            double xi = getXiFromZetaP(zeta, P);
            double rhox = getRhoxFromXi(rhoc, xi);
            double beta = getBetaFromC(rhoc, Fc);
            double Fx = getFx(rhox, beta);
            configureInternal(Fx, Fc, rhox, rhoc, beta);
        };
	}	
	
}
