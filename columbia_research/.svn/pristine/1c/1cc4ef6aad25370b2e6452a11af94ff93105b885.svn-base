package edu.columbia.lrl.experiments.task_traffic.analyzers;

import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.builders.AbstractNumberOfClientConfigurableBuilder;
import edu.columbia.lrl.LWSim.builders.HubNetworkBuilder;
import edu.columbia.lrl.LWSim.builders.NumberOfClientBasedBuilder;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractScheduler;
import edu.columbia.lrl.experiments.task_traffic.task_generators.AbstractTaskGenerator;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import ch.epfl.general_libraries.math.Rounder;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.MoreArrays;

public class TaskExecutionAnalyser {
	

	private IrregularTrafficApplication appl;
	private int participants;
	private int participatingToCompute;
	

	private int globalTaskCompleted = 0;
	private double globalTaskExecutionTimeNS = 0;
	private double globalTaskQueueTime = 0;
	
	double reservedTime = 0;
	
	private double[] onlineTimes;
	private int[] onlinePresences;

	
	public TaskExecutionAnalyser(IrregularTrafficApplication appl) {
		this.appl = appl;
	}
	
	
	public void init(int participants, int numberParticipatingToCompute) {
		this.participants = participants;
		this.participatingToCompute = numberParticipatingToCompute;
		onlineTimes = new double[participants];
		onlinePresences = new int[participants];
	}

	public void store(LWSIMExperiment lwSimExp, 
					  double ref, 
					  boolean analyseNodes) {
		
//		DataPoint defaultDatapoint = lwSimExp.defaultDatapoint;
		Execution executionObject = lwSimExp.getExecutionObject();
		
		try {		
			
			AbstractScheduler scheduler = appl.getScheduler();		
			AbstractTaskGenerator taskgen = appl.getConfigurator().getTaskGenerator();

			double B = lwSimExp.getReferenceBandwidth().getInBitsSeconds();
			double P = appl.getConfigurator().getNodeComputePowerFlopsPerNS();		
			double networkAccesBW = participants * B;		
			double FxInFlops = taskgen.getMeanFlopsPerCompoundTask_Fx();
			double FxInNs = appl.getTimeNSforFlops(FxInFlops); 	
			double tasks = scheduler.getNumberOfTaskToSchedule();		
			double computePowerFlopsPerNs = (double)participatingToCompute*P;
			double rhoX = Rounder.round(((double)taskgen.getComputationLoadInFlopsPerNS(appl)/computePowerFlopsPerNs), 2);		
			double Fc = taskgen.getMeanCommunicationFootprintPerCompoundTask__Fc(appl);	
			double rhoC = (double)taskgen.getCommunicationLoadInBitsPerS(appl)/networkAccesBW;
			double F = Rounder.round(Fc/FxInFlops, 2);
			double Fsec = Rounder.round(Fc/FxInNs, 2);
			double zeta = B*1e-9/F;
			double b = Fc/(double)taskgen.getMeanNumberOfTasksInCompoundTask();
			try {
				AbstractNumberOfClientConfigurableBuilder builder = ((NumberOfClientBasedBuilder)lwSimExp.getTopologyBuilder()).getSubBuilder();
				double networkLatency = ((HubNetworkBuilder)builder).getZeroLoadLatency();
				double psi = Rounder.round((b*1e9/B)/networkLatency, 2);
				lwSimExp.addPropertyToDefaultDataPoint("Psi", psi+"");
			}
			catch (Exception e) {
				
			}

			lwSimExp.addPropertyToDefaultDataPoint("Bandwidth (Gb/s)", B/1e9+"");
			lwSimExp.addPropertyToDefaultDataPoint("Compute node Power [flops/ns]", P+"");		
			lwSimExp.addPropertyToDefaultDataPoint("Predicted running time", (FxInNs*tasks) +"");	
			lwSimExp.addPropertyToDefaultDataPoint("F_x (flops)", FxInFlops+"");	
			lwSimExp.addPropertyToDefaultDataPoint("Predicted emitted bits", (Fc*tasks) +"");
			lwSimExp.addPropertyToDefaultDataPoint("F_c (bits)", Fc+"");
			if (P > 0) {
				lwSimExp.addPropertyToDefaultDataPoint("F (Byte/flop)", F/8+"" );
			}
			lwSimExp.addPropertyToDefaultDataPoint("F (Gb/s)", Fsec+"");
			lwSimExp.addPropertyToDefaultDataPoint("zeta (B/F) (flops/ns)", Rounder.round(zeta, 3)+"");
			lwSimExp.addPropertyToDefaultDataPoint("zeta/P (xi_eff)", Rounder.round((B*1e-9/F)/P, 3)+"");			
			if (tasks > 1) {
				lwSimExp.addPropertyToDefaultDataPoint("xi", Rounder.roundString((rhoX/rhoC), 2)+"");					
				lwSimExp.addPropertyToDefaultDataPoint("rho_c", Rounder.round(rhoC, 2)+"");	
				lwSimExp.addPropertyToDefaultDataPoint("rho_x", rhoX+"");
				lwSimExp.addPropertyToDefaultDataPoint("Beta (tasks per ms)", appl.getConfigurator().getTaskGenerator().getArrivalRatePerMS()+"");
			}

			DataPoint dp = lwSimExp.getDerivedDatapoint();

			double totalTime = appl.getTotalTime();
			
			double totalOnlineTime = MoreArrays.sum(onlineTimes);


			dp.addResultProperty("Average task execution time", globalTaskExecutionTimeNS/globalTaskCompleted);
			dp.addResultProperty("Average task queue time", globalTaskQueueTime/globalTaskCompleted);	
			dp.addResultProperty("Execution speed-up", FxInNs/((globalTaskExecutionTimeNS - globalTaskQueueTime)/globalTaskCompleted));
			dp.addResultProperty("Speed-up (task)", FxInNs/(globalTaskExecutionTimeNS/globalTaskCompleted));
			dp.addResultProperty("Relative speed-up", (FxInNs/(globalTaskExecutionTimeNS/globalTaskCompleted))/participants);
			double totalRunningTime = appl.getTotalRunningTime();
			double rho_x_eff = appl.getComputeUtilisation();
			dp.addResultProperty("Node utilisation (reserved)", totalOnlineTime/totalTime);
			dp.addResultProperty("Node utilisation (waiting local)", (totalOnlineTime - totalRunningTime)/totalTime);
			if (reservedTime > 0) {
				double schedOcc = reservedTime/(ref*participants);
				dp.addResultProperty("Node utilisation (reserved from scheduler)", schedOcc);
				dp.addResultProperty("Node waste (scheduler - computing)", schedOcc - rho_x_eff);
			}

			executionObject.addDataPoint(dp);

		}
		catch (IllegalStateException e) {
			System.out.println("Catched");
			e.printStackTrace();
		}		
		
	//	dp.addProperty("Predicted , value)
	
		
		if (analyseNodes) {
		
			for (int i = 1 ; i < onlinePresences.length ; i++) {
				DataPoint server = lwSimExp.getDerivedDatapoint();
				server.addProperty("rank", i);

				server.addResultProperty("time online", onlineTimes[i]);

				server.addResultProperty("% of time online", 100*onlineTimes[i]/appl.totalTimes[i]);	
				server.addResultProperty("% of time waiting", 100*onlineTimes[i]/appl.totalTimes[i] - 100*appl.runningTimes[i]/appl.totalTimes[i]);
				server.addResultProperty("Activities", onlinePresences[i]);

				server.addResultProperty("Average time per activity", onlineTimes[i]/(double)onlinePresences[i]);

				
				executionObject.addDataPoint(server);
			}
		}
	}
	
	public void taskArrived() {
//		taskArrived++;
	}

	public void taskCompleted(RootTask task, Time ref) {
		globalTaskCompleted++;
		globalTaskExecutionTimeNS += ref.getNanoseconds() - task.getOccurenceTimeNS();
		globalTaskQueueTime += task.getScheduledTime() - task.getOccurenceTimeNS();
	}
	
	public void online(int rank, double ns) {
		onlinePresences[rank]++;
		onlineTimes[rank] += ns;
	}

	public void reservationEnded(double d) {
		reservedTime += d;
	}

}
