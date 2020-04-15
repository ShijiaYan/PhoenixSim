package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;

import ch.epfl.general_libraries.clazzes.ParamName;

public class BetaAndOthersConfigurator extends AbstractFixedBNPConfigurator {

	public BetaAndOthersConfigurator(@ParamName(name="beta (ms-1)") final double beta, 
								 @ParamName(name="rhox") final double rhox, 
								 @ParamName(name="rhoc") final double rhoc) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(rhox, beta);
				double Fc = getFc(rhoc, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}
	
	
	public BetaAndOthersConfigurator(@ParamName(name="beta (ms-1)") final double beta, 
			@ParamName(name="rhox") final double rhox, 
			@ParamName(name="zeta") final float zeta) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(rhox, beta);
				double xi = getXiFromZetaP(zeta, P);
				double rhoc = getRhocFromXi(rhox, xi);
				double Fc = getFc(rhoc, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}
	
	public BetaAndOthersConfigurator(@ParamName(name="beta (ms-1)") final double beta, 
			@ParamName(name="rhoc") final float rhoc, 
			@ParamName(name="zeta") final double zeta) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fc = getFc(rhoc, beta);
				double xi = getXiFromZetaP(zeta, P);
				double rhox = getRhoxFromXi(rhoc, xi);
				double Fx = getFx(rhox, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}
	
	public BetaAndOthersConfigurator(@ParamName(name="beta (ms-1)") final double beta, 
			@ParamName(name="rhox") final float rhox, 
			@ParamName(name="F") final float F) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(rhox, beta);
				double Fc = F*Fx;
				double rhoc = getRhoc(Fc, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}
	
	public BetaAndOthersConfigurator(@ParamName(name="beta (ms-1)") final float beta, 
			@ParamName(name="rhoc") final float rhoc, 
			@ParamName(name="F") final double F) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fc = getFc(rhoc, beta);
				double Fx = Fc/F;
				double rhox = getRhox(Fx, beta);
				configureInternal(Fx, Fc,  rhox, rhoc, beta);		
			}
		};
	}	
	
	
}
