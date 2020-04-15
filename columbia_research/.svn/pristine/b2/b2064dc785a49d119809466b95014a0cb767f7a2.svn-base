package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;

import ch.epfl.general_libraries.clazzes.ParamName;

public class FxFcConfigurator extends AbstractFixedBNPConfigurator {




	public FxFcConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="rhox") final double rhox) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(Fx_);
				double Fc = getFc(Fc_);
				double beta = getBetaFromX(rhox, Fx);
				double rhoc = getRhoc(Fc, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}	


	public FxFcConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="Fc") final double Fc_, 
			@ParamName(name="rhoc") final float rhoc) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(Fx_);			
				double Fc = getFc(Fc_);		
				double beta = getBetaFromC(rhoc, Fc);
				double rhox = getRhox(Fx, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}

	public FxFcConfigurator(@ParamName(name="Fx") final double Fx_, 
			@ParamName(name="Fc") final float Fc_, 
			@ParamName(name="beta (ms-1)") final double beta) {
		toInvoke = new Runnable() {		
			@Override
			public void run() {
				double Fx = getFx(Fx_);
				double Fc = getFc(Fc_);			
				double rhox = getRhox(Fx, beta);
				double rhoc = getRhoc(Fc, beta);
				configureInternal(Fx, Fc, rhox, rhoc, beta);		
			}
		};
	}



}
