package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.awt.Color;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;
import ch.epfl.general_libraries.random.PRNStream;

public abstract class AbstractTaskPropertiesGenerator {
	
	public abstract InitFeedback  setMeanInitPlusAggrTimeNS(double t);
	public abstract InitFeedback  setMeanSchedPlusRetrieve(double ss);
	
//	public abstract void init(AbstractTaskGenerator generator, double BWinBitsPerSecond, double nodePowerGflopPerSecond, int comNodes, int compNodes,  double rho_x);

	public abstract RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream s);

	public abstract Task getTask(PRNStream s, Color c);
	
	public abstract double getMeanInitTime();
	
	public abstract double getMeanAggrTime();
	
	public abstract double getMeanSchedulingTaskBits();
	
	public abstract double getMeanRetrieveTaskBits();

}
