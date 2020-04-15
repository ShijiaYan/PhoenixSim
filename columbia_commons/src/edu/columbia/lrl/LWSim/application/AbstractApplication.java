package edu.columbia.lrl.LWSim.application;

import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;

import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.MoreArrays;

public abstract class AbstractApplication implements ExperimentBlock {
	
	private boolean isInit = false;
	
	private int[] runningPresences;
	public double[] runningTimes;	
	public double[] totalTimes;
	private int numberOfNodes = -1;
	
	private boolean includeNodeDetailsInMonitoring = false;
	
	public AbstractApplication() {
	}
	
	public AbstractApplication(boolean includeNodeDetailsInMonitoring) {
		this.includeNodeDetailsInMonitoring = includeNodeDetailsInMonitoring;
	}
	
	public void run(ActionManager c, int rank, Time ref) {
		try {
			totalTimes[rank] = -1;
			runImpl(c, rank, ref);
			if (totalTimes[rank] == -1) {
				totalTimes[rank] = ref.getNanoseconds();
			}
			c.setDead(ref);			
			synchronized (c) {
				c.notify();
			}

		}
		catch (Exception e){
			c.setDead(ref);		
			c.setError(e.getMessage());
			synchronized (c) {
				c.notify();
			}	
			e.printStackTrace();
		}	
	}
	
	public abstract void runImpl(ActionManager c, int rank, Time ref) throws InterruptedException;
	
	public abstract InitFeedback init(LWSIMExperiment exp);
	
	public void reset() {
		this.numberOfNodes = -1;
		totalTimes = null;
		runningTimes = null;
		runningPresences = null;
		isInit = false;
	}
	
	public InitFeedback init(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
		totalTimes = new double[numberOfNodes];
		runningTimes = new double[numberOfNodes];	
		runningPresences = new int[numberOfNodes];
		isInit = true;
		return null;
	}

	public void addApplicationInfo(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes) {
		addApplicationInfoImpl(lwSimExp, ref, analyseNodes);
	
		double totalRunningTime = getTotalRunningTime();		
		

		
		lwSimExp.addGlobalResult("Total running execution time (ns)", totalRunningTime);	
		lwSimExp.addGlobalResult("Total atomic jobs", MoreArrays.sum(runningPresences));
		lwSimExp.addGlobalResult("Speed-up", totalRunningTime/ref);
		double rho_x_eff = getComputeUtilisation();
		lwSimExp.addGlobalResult("Node utilisation (computing = rho_x_eff)", rho_x_eff);

		if (includeNodeDetailsInMonitoring) {
			
			for (int i = 1 ; i < totalTimes.length ; i++) {
				DataPoint server = lwSimExp.getDerivedDatapoint();	
				server.addProperty("rank", i);
				server.addResultProperty("time running", runningTimes[i]);
				if (totalTimes[i] == 0) {
				}
				server.addResultProperty("% of time running", 100*runningTimes[i]/totalTimes[i]);				
				server.addResultProperty("Atomic jobs", runningPresences[i]);
				server.addResultProperty("Average time per atomic job", runningTimes[i]/(double)runningPresences[i]);			
				
				lwSimExp.getExecutionObject().addDataPoint(server);
			}
		}
	}
	
	public abstract void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes);
	

	public int getNumberOfClients() {
		return numberOfNodes;
	}
	
	public boolean isInit() {
		return isInit;
	}
	
	/* Can be used to specify at which time a rank is
	 * over. If not called, the time at which all jobs
	 * and communication are over will be used as 
	 * rank "end time". This is used to compute the compute
	 * efficiency.
	 */
	public void executionEnd(int rank, Time ref) {
		totalTimes[rank] = ref.getNanoseconds();	
	}
	
	public void executeSomething(int rank, double ns) {
		runningPresences[rank]++;
		runningTimes[rank] += ns;
	}	
	
	public double getTotalRunningTime() {
		return MoreArrays.sum(runningTimes);
	}
	
	public double getTotalTime() {
		return MoreArrays.sum(totalTimes);
	}
	
	public double getComputeUtilisation() {
		return getTotalRunningTime()/getTotalTime();
	}
	
}
