package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.configurators.AbstractSubConf;

public abstract class AbstractFixedBNPConfigurator extends AbstractSubConf {


	
	public InitFeedback  configure(IrregularTrafficApplication appl, LWSIMExperiment exp,  double B, int N, double P) {
		// this must occur first, appl might be required
		this.appl = appl;
		this.exp = exp;		
		this.B = B;
		this.N = N;
		this.P = P;
		this.computingNodes = N - getNonComputingNodes();
		toInvoke.run();
		return configurationReturnedFailure;
	}
	
	protected void configureInternal(double Fx, double Fc, double rhox, double rhoc, double beta) {
		configurationReturnedFailure = appl.getConfigurator().configure(exp, appl, B, (int)N, Fx, Fc, rhox, rhoc, beta);
	}
	
}
