package edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.protocols.ResponseToScheduler;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.protocol.DelegationMessage;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.protocol.ResponseToDelegation;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public abstract class AbstractTaskExecutionModel {

	public abstract void executeTask(IrregularTrafficApplication appl, Task rt, ActionManager c, Time ref, int rank, TaskExecutionAnalyser taskExecAnalyser) throws InterruptedException;


	public void runComputeNode(IrregularTrafficApplication appl, TaskExecutionAnalyser taskExecAnalyser, ActionManager c, int rank, Time ref) throws InterruptedException {
		try {
			while (!c.isToBeTerminated()) { // simulation can be running until a given number of packets
				Object r = c.blockingReadFromAny(ref, false);
				double initTime = ref.getNanoseconds();			
				if (r instanceof RootTask) {
					RootTask rt = (RootTask)r;
					executeTask(appl, rt, c, ref, rank, taskExecAnalyser);
					c.send(ref, new ResponseToScheduler(rank, rt, false), (int)rt.getResultSize(), 0);				
				//	System.out.println(rank + " is free again");
					taskExecAnalyser.online(rank, ref.getNanoseconds() - initTime);
				}
				if (r instanceof DelegationMessage) {				
					DelegationMessage msg = (DelegationMessage)r;
					executeTask(appl, msg.t, c, ref, rank, taskExecAnalyser);
					c.send(ref, new ResponseToDelegation(rank), (int)msg.t.getResultSize(), msg.issuingNode, 1/* high priority */);
					taskExecAnalyser.online(rank, ref.getNanoseconds() - initTime);
				}
				if ( r instanceof String) {
					return;
				}
			}
		} finally {
			appl.executionEnd(rank, ref);			
		}	
	}
	
	protected void delegateTask(Integer allocated, Task dele, ActionManager c, Time ref, int rank) {
		c.send(ref, new DelegationMessage(dele, rank), (int)dele.getSchedulingSize(), allocated);	
	}


	public abstract Map<String, String> getAllParameters();
}
