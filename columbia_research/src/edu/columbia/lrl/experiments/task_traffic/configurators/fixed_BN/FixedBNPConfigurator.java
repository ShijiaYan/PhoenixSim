package edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.configurators.AbstractTaskGenerationConfigurator;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;

public class FixedBNPConfigurator extends AbstractTaskGenerationConfigurator {
	
	private AbstractFixedBNPConfigurator subConf;
	
	public FixedBNPConfigurator(
			@ParamName(name="Baseline bandwidth in Gb/s ?")  double B, 
			@ParamName(name="Number of nodes in system") int N,
			@ParamName(name="Compute node power in [flop/ns]", default_="1") float computePow,
			AbstractFixedBNPConfigurator subConf) {
		super();
		this.subConf = subConf;
		this.N = N;
		this.B = B;
		this.P = computePow;
	}
	
	@Override
	public InitFeedback initAndsetNumberOfNotComputingNodes(IrregularTrafficApplication appl, LWSIMExperiment exp, AbstractTaskGenerator taskGenerator) {
		super.taskgen = taskGenerator;
		return subConf.configure(appl, exp, B, N, P);	
	}

}