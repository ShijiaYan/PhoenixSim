package edu.columbia.lrl.CrossLayer.simulator.irregular_traffic;

import ch.epfl.general_libraries.clazzes.ParamName;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.configurators.AbstractTaskGenerationConfigurator;
import edu.columbia.lrl.experiments.task_traffic.configurators.fixed_BN.AbstractFixedBNPConfigurator;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;

public class CrossLayerFixedBNPConfigurator extends AbstractTaskGenerationConfigurator {
	
	private AbstractFixedBNPConfigurator subConf;
	
	
	public CrossLayerFixedBNPConfigurator(
			@ParamName(name="Compute node power in [flop/ns]", default_="1") float computePow,
			AbstractFixedBNPConfigurator subConf) {
		super();
		this.subConf = subConf;
		this.P = computePow;

	}
	
	@Override
	public InitFeedback initAndsetNumberOfNotComputingNodes(IrregularTrafficApplication appl, LWSIMExperiment exp, AbstractTaskGenerator taskGenerator) {
		super.taskgen = taskGenerator;
		int numClients = exp.getNumberOfClients();
		double baseBandwidth = exp.getTotalInjectionBandwidth().divide((float)numClients).getInGbitSeconds();
		return subConf.configure(appl, exp, baseBandwidth, numClients, P);	
	}

}

