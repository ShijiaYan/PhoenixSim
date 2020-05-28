package edu.columbia.lrl.experiments.task_traffic.schedulers.work_stealing;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.AbstractPoolManager;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.FlatClientPoolManager;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractScheduler;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;

public class WorkstealingScheduler extends AbstractScheduler {

	double stealingFreqNS;
	double coverageRate;
	AbstractPoolManager poolManager;
	int seed;
	boolean withSeed;
	PRNStream stream;
	
	protected int scheduled = 0;
	protected int terminated = 0;
	public int done = 0;
	
	private int participants;
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="10") double stealingFreqMCS) {
		this(stealingFreqMCS, 1d);
	}
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="10") double stealingFreqMCS, 
								@ParamName(name="Seed", default_="0") int seed) {
		this(stealingFreqMCS, 1d, seed);
	}	
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="0.1") double stealingFreqMCS,
			@ParamName(name="Stealing request coverage", default_="10") double coverage) {
		this(stealingFreqMCS, coverage, new FlatClientPoolManager(true));
	}	
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="0.1") double stealingFreqMCS,
			@ParamName(name="Stealing request coverage", default_="10") double coverage,
			@ParamName(name="Seed", default_="0") int seed) {
		this(stealingFreqMCS, coverage, seed, new FlatClientPoolManager(true));
	}
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="10") double stealingFreqMCS,
			@ParamName(name="Stealing request coverage", default_="10") double coverage,
			@ParamName(name="Pool manager", defaultClass_=FlatClientPoolManager.class) AbstractPoolManager poolManager) {
		this.stealingFreqNS = 1000*stealingFreqMCS;
		this.coverageRate = coverage;
		this.poolManager = poolManager;
	}	
	
	public WorkstealingScheduler(@ParamName(name="Stealing freq in mcs", default_="10") double stealingFreqMCS,
			@ParamName(name="Stealing request coverage", default_="10") double coverage,
			@ParamName(name="Seed", default_="0") int seed,
			@ParamName(name="Pool manager", defaultClass_=FlatClientPoolManager.class) AbstractPoolManager poolManager) {
		this.stealingFreqNS = 1000*stealingFreqMCS;
		this.coverageRate = coverage;
		this.poolManager = poolManager;
		this.seed = seed;
		withSeed = true;
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = super.getAllParameters();
		map.put("Stealing period", stealingFreqNS+"");	
		map.put("Stealing coverage", coverageRate+"");
		if (withSeed)
			map.put("Stealing seed", seed+"");
		return map;
	}
	
	public void init(LWSIMExperiment experiment, IrregularTrafficApplication application, int nbtasks) {
		super.init(experiment, application, nbtasks);
		if (withSeed) {
			stream = PRNStream.getDefaultStream(seed +1000);
		} else {
			stream = experiment.getRandomStreamForEverythingButTraffic();
		}
		scheduled = 0;
		terminated = 0;
		done = 0;
	}
	
	public double nextDouble() {
		double d = stream.nextDouble();
		return d;
	}

	public void runImpl(ActionManager c, int rank, Time ref) throws InterruptedException {		
		if (rank == 0) {
			participants = c.getParticipantNumber();
			poolManager.init(participants);			
			getTaskExecutionAnalyser().init(participants, participants);
		}
		WorkStealingAgent agent = new WorkStealingAgent(application, this, c, rank);
		agent.proceed(ref);		
	}
	
	public boolean hasMoreTasksToSchedule() {
		return scheduled < getNumberOfTaskToSchedule();
	}
	
	public boolean hasStillWorkToDo() {
		return terminated < getNumberOfTaskToSchedule();
	}
	
	@Override
	public RootTask getNextTask() {
		scheduled++;
		return super.getNextTask();
	}	
	
	@Override
	public void addApplicationInfo(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes) {
		getTaskExecutionAnalyser().store(lwSimExp, ref, analyseNodes);
	}

	@Override
	public int getNumberOfNotComputingNodes() {
		return 0;
	}	

}
