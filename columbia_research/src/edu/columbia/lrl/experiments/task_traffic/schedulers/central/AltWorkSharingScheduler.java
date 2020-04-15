package edu.columbia.lrl.experiments.task_traffic.schedulers.central;

import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.AbstractPoolManager;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.FlatClientPoolManager;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod.AbstractTaskExecutionModel;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod.EvenBetterExecutionModel;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.simulation.Time;

/**
 *  Do not forget to overide init() method in extending classes to reset the queues or any other mechanisms
 * @author rumley
 *
 */
public class AltWorkSharingScheduler extends WorkSharingScheduler implements ExperimentBlock {
	
	
	private RootTask task;
		
	
	public AltWorkSharingScheduler(@ParamName(name="Worker execution model", defaultClass_=EvenBetterExecutionModel.class) AbstractTaskExecutionModel taskMod, 
			@ParamName(name="Client pool manager", defaultClass_=FlatClientPoolManager.class) AbstractPoolManager poolManager) {	
		super(taskMod, poolManager);
	}
	
	@Override
	public void runSchedulerNode(ActionManager c, int rank, Time ref) throws InterruptedException {
		
		// create and schedule initial task
		task = getNextTask();
		c.idle(ref, task.getOccurenceTimeNS());	
		schedule(c, task, ref);
		
		prepareNextTask(ref, c);
		
		while (hasMoreTaskToGenerate() && !c.isToBeTerminated()) {	
			Object r = c.blockingReadFromAny(ref); 
			if (r != null) {			
				processReception(ref, rank, c, r);		
			} 
			if (ref.getNanoseconds() >= task.getOccurenceTimeNS()) {
			//	c.doSomeJob(timeBefore, (ref.getNanoseconds() - timeBefore.getNanoseconds()), "wait ", 2);
				// if blockingread returns null, this means that the next task has to start
				if (!schedule(c, task, ref)) {
					// enqueue
					taskQueue.addLast(task);
				}
				prepareNextTask(ref, c);
			}
		}
		while (runningTasks > 0 && !c.isToBeTerminated()) {
			Object r = c.blockingReadFromAny(ref);
			processReception(ref, rank, c, r);			
		}
		c.broadcastButMe(ref, "done", 0);	
	}
	
	private void prepareNextTask(Time ref, ActionManager c) {			
		task = getNextTask();
		c.scheduleTimeout(ref, task.getOccurenceTimeNS() - ref.getNanoseconds());		
	}
}
