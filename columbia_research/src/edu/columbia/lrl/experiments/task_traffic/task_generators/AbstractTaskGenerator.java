package edu.columbia.lrl.experiments.task_traffic.task_generators;

import java.util.Map;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractTaskGenerator implements ExperimentBlock {
	
	private double initPlusAggrTimeNS;
	private double schedPlusRetrieve;
	
	protected double averageTaskPerMs;
	
	private PRNStream stream;
	protected IrregularTrafficApplication appl;
	
	private ExponentialDist expDist;
	
	private double horizon;
	
	protected int taskCounter = 0;	
	
	protected abstract RootTask getRootTask(IrregularTrafficApplication appl, double timeNS, PRNStream stream);

	public abstract double getMeanCommunicationFootprintPerCompoundTask__Fc(IrregularTrafficApplication appl);	
	public abstract double getMeanNumberOfTasksInCompoundTask();

	public abstract double getTaskSizesForA_Fc(IrregularTrafficApplication appl);
	public abstract double getTaskFlopsForA_Fx(double Fx);
	
	protected abstract InitFeedback setMeanInitPlusAggrTimeNSInternal(double time);
	protected abstract InitFeedback setMeanSchedPlusRetrieveInternal(double size);
	
	public InitFeedback setMeanInitPlusAggrTimeNS(double time) {
		this.initPlusAggrTimeNS = time;
		return setMeanInitPlusAggrTimeNSInternal(time);
	}
	
	public InitFeedback setMeanSchedPlusRetrieve(double size) {
		this.schedPlusRetrieve = size;
		return setMeanSchedPlusRetrieveInternal(size);
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		double time = initPlusAggrTimeNS;
		Map<String, String> m = new SimpleMap<>(4);
		m.put("task average time", time+"");
		m.put("task average bits", schedPlusRetrieve+"");
		m.put("task average flops", appl.getFlopsForTimeNS(time)+"");	
		return m;
	}
	
	public double getMeanFlopsPerCompoundTask_Fx() {
		double oneTaskInFlops = appl.getFlopsForTimeNS(initPlusAggrTimeNS);
		return getMeanNumberOfTasksInCompoundTask()*oneTaskInFlops;
	}	
	
	public InitFeedback  configure(LWSIMExperiment experiment, IrregularTrafficApplication appl) {
		this.appl = appl;
		averageTaskPerMs = appl.getConfigurator().getBeta();
		// potentially here check if Fx or Fc are negative, or if taskPropalready has ss or tt
		
		// by default, values are imposed from Fx and Fc
		double flops = getTaskFlopsForA_Fx(appl.getConfigurator().getFx());
		double time = appl.getTimeNSforFlops(flops);
		InitFeedback  failure = setMeanInitPlusAggrTimeNS(time);
		if (failure != null) return failure;
		failure = setMeanSchedPlusRetrieve(getTaskSizesForA_Fc(appl));
		if (failure != null) return failure;
		
		taskCounter = 0;
		stream = experiment.getRandomStreamForTraffic();
		expDist = new ExponentialDist(averageTaskPerMs/1000000d);
		horizon = 0;
		//horizon = expDist.inverseF(stream.nextDouble());		
		return null;
	}	
	
	public RootTask getNextTask(IrregularTrafficApplication appl) {
		if (stream == null) throw new IllegalStateException("Task generator must be initialized first");
		RootTask task = getRootTask(appl, horizon, stream);
		double newTaskInterTime = expDist.inverseF(stream.nextDouble());
		horizon = horizon + newTaskInterTime;
		return task;
	}

	public double getArrivalRatePerMS() {
		return averageTaskPerMs;
	}
	
	public double getComputationLoadInFlopsPerNS(IrregularTrafficApplication appl) {
		double tasks = getMeanFlopsPerCompoundTask_Fx();
		double arrivalsPerNs = averageTaskPerMs/1000000d;
		return tasks * arrivalsPerNs;
	}

/*	public AbstractTaskPropertiesGenerator getTaskPropertiesGenerator() {
		return taskPropGen;
	}*/

	/**
	 * Returns in bits/s the expected load of this generator
	 */
	public double getCommunicationLoadInBitsPerS(IrregularTrafficApplication appl) {
		return getMeanCommunicationFootprintPerCompoundTask__Fc(appl) * getArrivalRatePerMS() * 1e3;
	}

}
