package edu.columbia.lrl.experiments.task_traffic.task_generators;

import ch.epfl.general_libraries.random.PRNStream;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.TestTask;

public class TestTaskGenerator extends AbstractTaskGenerator {		

	
	@Override
	public double getMeanNumberOfTasksInCompoundTask() {
		return TestTask.getNumberOfSubTasks();
	}
	
	@Override
	protected RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream stream) {
		TestTask tt = new TestTask(timeNS);
		tt.setDescriptions(taskCounter++);
		return tt;
	}

	@Override
	public double getMeanCommunicationFootprintPerCompoundTask__Fc(IrregularTrafficApplication appl) {
		return TestTask.getCommunicationFootprint(appl);
	}


	@Override
	public double getTaskSizesForA_Fc(IrregularTrafficApplication appl) {
		throw new IllegalStateException("Not implemented");
	}
	@Override
	public double getTaskFlopsForA_Fx(double fx) {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	protected InitFeedback setMeanInitPlusAggrTimeNSInternal(double time) {
		System.out.println("Warning: test task are fixed");
		return null;
	}

	@Override
	protected InitFeedback setMeanSchedPlusRetrieveInternal(double size) {
		System.out.println("Warning: test task are fixed");
		return null;
	}

}
