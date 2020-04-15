package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_N;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.configurators.AbstractSubConf;

public abstract class AbstractFixedNPConfigurator extends AbstractSubConf {	

	InitFeedback configure(IrregularTrafficApplication appl, LWSIMExperiment exp, int N, double P) {
		this.appl = appl;
		this.N = N;
		this.P = P;
		this.computingNodes = N - getNonComputingNodes();
		this.exp = exp;
		toInvoke.run();
		return configurationReturnedFailure;		
	}
	
	protected void configureInternal(double B, double Fx, double Fc, double rhox, double rhoc, double beta) {
		configurationReturnedFailure = appl.getConfigurator().configure(exp, appl,  B, (int)N, Fx, Fc, rhox, rhoc, beta);
	}
	
	protected double getBFromZeta(double F, double zeta) {
		return zeta*F;
	}	
	
	protected double getBFromRhoC(double rhoc, double Fc, double beta) {
		return beta*1e3*Fc/(rhoc*N*1e9);
	}
	
}
