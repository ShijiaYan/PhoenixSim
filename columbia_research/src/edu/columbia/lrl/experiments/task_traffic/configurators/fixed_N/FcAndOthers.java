package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_N;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FcAndOthers extends AbstractFixedNPConfigurator {

	public FcAndOthers(@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="F") final double F, 
			@ParamName(name="Zeta") final double zeta, 
			@ParamName(name="rhox") final float rhox) {
		toInvoke = () -> {
            double Fc = getFx(Fc_);
            double Fx = Fc/F;
            double beta = getBetaFromX(rhox, Fx);
            double xi = getXiFromZetaP(zeta, P);
            double rhoc = getRhocFromXi(rhox, xi);
            double B = getBFromZeta(F, zeta);
            configureInternal(B, Fx, Fc, rhox, rhoc, beta);
        };
	}
	
	public FcAndOthers(@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="F") final double F, 
			@ParamName(name="Zeta") final double zeta, 
			@ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fc = getFx(Fc_);
            double Fx = Fc/F;
            double xi = getXiFromZetaP(zeta, P);
            double rhox = getRhoxFromXi(rhoc, xi);
            double beta = getBetaFromX(rhox, Fx);
            double B = getBFromZeta(F, zeta);
            configureInternal(B, Fx, Fc, rhox, rhoc, beta);
        };
	}
	
	public FcAndOthers(@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="B") final float B, 
			@ParamName(name="Xi") final double xi, 
			@ParamName(name="rhoc") final double rhoc) {
		toInvoke = () -> {
            double Fc = getFx(Fc_);
            double zeta = getZetaFromPxi(P, xi);
            double F = getFfromZetaB(zeta, B);
            double Fx = Fc/F;

            double rhox = getRhoxFromXi(rhoc, xi);
            double beta = getBetaFromX(rhox, Fx);
            configureInternal(B, Fx, Fc, rhox, rhoc, beta);
        };
	}	
	
}
