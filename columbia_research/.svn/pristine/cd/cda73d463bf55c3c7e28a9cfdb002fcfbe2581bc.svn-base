package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.awt.Color;

import umontreal.iro.lecuyer.probdist.Distribution;
import ch.epfl.general_libraries.random.PRNStream;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class RVBasedTaskPropertiesGenerator extends
		AbstractTaskPropertiesGenerator {
	
	private Distribution initTime;
	private Distribution aggTime;
	private Distribution sendSize;
	private Distribution retrieveSize;
	
	public RVBasedTaskPropertiesGenerator(Distribution initTime,
										  Distribution aggTime,
										  Distribution sendSize,
										  Distribution retrieveSize) {
		this.initTime = initTime;
		this.aggTime = aggTime;
		this.sendSize = sendSize;
		this.retrieveSize = retrieveSize;
	}
	
	private double value(Distribution d, PRNStream s) {
		int trials = 0;
		double val = -1;
		while (val < 0 && trials < 20) {
			val = d.inverseF(s.nextDouble());
			trials++;
		}
		if (trials == 20) throw new IllegalStateException("Impossible to generate positive value");	
		return val;
	}

	@Override
	public RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream s) {
		double iTime = value(initTime, s);
		double aTime = value(aggTime, s);
		return new RootTask(iTime, aTime, 
				appl.getInitialSchedulingBits(), appl.getFinalSchedulingBits(), timeNS);
	}

	@Override
	public Task getTask(PRNStream s, Color color) {
		return new Task(value(initTime, s), value(aggTime, s), (int)value(sendSize, s), (int)value(retrieveSize, s), color);
	}

	@Override
	public double getMeanInitTime() {
		return initTime.getMean();
	}

	@Override
	public double getMeanAggrTime() {
		return aggTime.getMean();
	}

	@Override
	public double getMeanSchedulingTaskBits() {
		return sendSize.getMean();
	}

	@Override
	public double getMeanRetrieveTaskBits() {
		return retrieveSize.getMean();
	}

	@Override
	public InitFeedback setMeanInitPlusAggrTimeNS(double t) {
		return new InitFeedback("Can't change SSJ Rv mean");
	}

	@Override
	public InitFeedback setMeanSchedPlusRetrieve(double ss) {
		return new InitFeedback("Can't change SSJ Rv mean");
	}
	
}
