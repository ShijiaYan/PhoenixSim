package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_N;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FxAndOthers extends AbstractFixedNPConfigurator {
	
	public FxAndOthers(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="F") final double F, 
			@ParamName(name="Zeta") final double zeta, 
			@ParamName(name="rhox") final float rhox) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(Fx_);
				double beta = getBetaFromX(rhox, Fx);
				double xi = getXiFromZetaP(zeta, P);
				double rhoc = getRhocFromXi(rhox, xi);
				double Fc = Fx*F;
				double B = getBFromRhoC(rhoc, Fc, beta);
				configureInternal(B, Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}	

}
