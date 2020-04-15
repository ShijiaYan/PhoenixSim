package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.awt.Color;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class ConstantsTaskPropertiesGenerator extends AbstractTaskPropertiesGenerator {
	
	protected void setInitComputeIndex(double initComputeIndex) {
		this.initComputeIndex = initComputeIndex;
	}

	protected void setAggregateComputeIndex(double aggregateComputeIndex) {
		this.aggregateComputeIndex = aggregateComputeIndex;
	}

	private double initComputeIndex;
	private double aggregateComputeIndex;

	protected void setSchedulingSize(int schedulingSize) {
		this.schedulingSize = schedulingSize;
	}

	protected void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}

	private double schedulingSize;
	private double resultSize;
	
	public ConstantsTaskPropertiesGenerator(@ParamName(name="Flops (or time in ns if no compute power specified) for init", default_="10000") double initComputeIndex, 
											@ParamName(name="Flops (or time in ns if no compute power specified) for aggregation", default_="10000") double aggregateComputeIndex, 
											@ParamName(name="Bits to send when scheduling", default_="160000") int schedulingSize, 
											@ParamName(name="Bits returning when done", default_="320000") int resultSize) {
		this.initComputeIndex = initComputeIndex;
		this.aggregateComputeIndex = aggregateComputeIndex;
		this.schedulingSize = schedulingSize;
		this.resultSize = resultSize;
	}

	@Override
	public RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream s) {
		return new RootTask(initComputeIndex, aggregateComputeIndex, 
				appl.getInitialSchedulingBits(), appl.getFinalSchedulingBits(), timeNS);
	}

	@Override
	public Task getTask(PRNStream s, Color color) {
		return new Task(initComputeIndex, aggregateComputeIndex, schedulingSize, resultSize, color);
	}

	@Override
	public double getMeanInitTime() {
		return initComputeIndex;
	}

	@Override
	public double getMeanAggrTime() {
		return aggregateComputeIndex;
	}
	
	@Override
	public double getMeanSchedulingTaskBits() {
		return schedulingSize;
	}

	@Override
	public double getMeanRetrieveTaskBits() {
		return resultSize;
	}

	@Override
	public InitFeedback setMeanInitPlusAggrTimeNS(double t) {
		if (t <= 0) return new InitFeedback("Cannot have null or negative times");
		double actualSum = initComputeIndex + aggregateComputeIndex;
		double fact = t/actualSum;
		initComputeIndex *= fact;
		aggregateComputeIndex *= fact;
		return null;
	}

	@Override
	public InitFeedback setMeanSchedPlusRetrieve(double ss) {
		if (ss <= 0) {
			return new InitFeedback("Cannot have null or negative sizes");		
		}
		double actualSum = schedulingSize + resultSize;
		double fact = ss/actualSum;
		schedulingSize *= fact;
		resultSize *= fact;
		return null;
	}
}
