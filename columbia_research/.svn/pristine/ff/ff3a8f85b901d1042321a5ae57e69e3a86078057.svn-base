package edu.columbia.lrl.experiments.task_traffic.schedulers;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;

public abstract class AbstractScheduler {
	
	protected IrregularTrafficApplication application;
//	private AbstractTaskGenerationConfigurator configurator;
	private int nbtasks;
	protected LWSIMExperiment experiment;
	private TaskExecutionAnalyser taskExecAnalyser;	
	
//	protected double nodePowerGflopPerSecond;	
	
	private int taskCreatedSoFar;
	
	public abstract void runImpl(ActionManager c, int rank, Time ref)
			throws InterruptedException;
	
/*	public double getArrivalRatePerMS() {
		return application.getConfigurator().getBeta();
	}*/
	
	public void init(LWSIMExperiment experiment, IrregularTrafficApplication application, int nbtasks) {
		taskCreatedSoFar = 0;
		this.experiment = experiment;
		this.application = application;
	//	this.configurator = configurator;
		this.nbtasks = nbtasks;	
		taskExecAnalyser = new TaskExecutionAnalyser(application);
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = SimpleMap.getMap("Number of tasks", nbtasks +"");
		return map;
	}
	
/*	public AbstractTaskGenerator getTaskGenerator() {
		return configurator.getTaskGenerator();
	}	*/
	
	public RootTask getNextTask() {
		taskCreatedSoFar++;
		return application.getConfigurator().getTaskGenerator().getNextTask(application);
	}
	
	public LWSIMExperiment getLWSimExperiment() {
		return experiment;
	}
	
	public boolean hasMoreTaskToGenerate() {
		return taskCreatedSoFar <= nbtasks;
	}
	
	public int getNumberOfTaskToSchedule() {
		return nbtasks;
	}
	
	public TaskExecutionAnalyser getTaskExecutionAnalyser() {
		return taskExecAnalyser;
	}
	
	public abstract int getNumberOfNotComputingNodes();
	public abstract void addApplicationInfo(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes);
}
