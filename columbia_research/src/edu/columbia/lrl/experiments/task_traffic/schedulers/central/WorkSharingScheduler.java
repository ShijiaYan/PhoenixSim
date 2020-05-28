package edu.columbia.lrl.experiments.task_traffic.schedulers.central;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;

import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.AbstractPoolManager;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeDoneMessage;
import edu.columbia.lrl.experiments.task_traffic.protocols.ResponseToScheduler;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractScheduler;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod.AbstractTaskExecutionModel;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod.EvenBetterExecutionModel;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.ExperimentBlock;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.simulation.Time;

/**
 *  Do not forget to overide init() method in extending classes to reset the queues or any other mechanisms
 * @author rumley
 *
 */
public class WorkSharingScheduler extends AbstractScheduler implements ExperimentBlock {
	
	protected AbstractTaskExecutionModel taskExecMod;
	protected AbstractPoolManager poolManager;

	ArrayDeque<RootTask> taskQueue;	
	
	protected int runningTasks = 0;
	
	protected int participants;


	
	public WorkSharingScheduler(@ParamName(name="Worker execution model", defaultClass_=EvenBetterExecutionModel.class) AbstractTaskExecutionModel taskMod, 
								AbstractPoolManager poolManager) {	
		this.taskExecMod = taskMod;	
		this.poolManager = poolManager;
		taskQueue = new ArrayDeque<>();
	}
	
	@Override
	public void runImpl(ActionManager c, int rank, Time ref)throws InterruptedException {
		if (rank == 0) {
			participants = c.getParticipantNumber();
			initWorkSharing(participants);
			getTaskExecutionAnalyser().init(participants, participants - 1);
			runSchedulerNode(c, rank, ref);
		}
		else
			taskExecMod.runComputeNode(application, getTaskExecutionAnalyser(), c, rank, ref);
	}
	
	public Map<String, String> getAllParameters() {
		// TODO Auto-generated method stub
		Map<String, String> map = super.getAllParameters();
		map.put("Task exec mod", taskExecMod.getClass().getSimpleName());
		map.put("Pool manager", poolManager.getClass().getSimpleName());
		map.putAll(taskExecMod.getAllParameters());	
		map.putAll(poolManager.getAllParameters());
		return map;
	}
	
	public void runSchedulerNode(ActionManager c, int rank, Time ref) throws InterruptedException {
		
		// create and schedule initial task
		RootTask task = getNextTask();
		c.idle(ref, task.getOccurenceTimeNS());	
		schedule(c, task, ref);
		
		task = getNextTask();
		
		while (hasMoreTaskToGenerate() && !c.isToBeTerminated()) {	
			Time timeBefore = ref.thisTime();
			double timeOutDelay = task.getOccurenceTimeNS() - ref.getNanoseconds();
			Object r = c.blockingReadFromAny(ref, timeOutDelay); //  potentially transmit some text for indicating in the timeline how many tasks are awaited
			if (r != null) {			
				processReception(ref, rank, c, r);
				if (runningTasks == 0) {
					c.idle(ref, timeOutDelay);
				}			
			} else {
				c.doSomeJob(timeBefore, ref.getNanoseconds() - timeBefore.getNanoseconds(), "wait ", TimeLine.EnumType.WAIT);
				// if blockingread returns null, this means that the next task has to start
				if (!schedule(c, task, ref)) {
					// enqueue
					taskQueue.addLast(task);
				}
				task = getNextTask();
			}
		}
		while (runningTasks > 0 && !c.isToBeTerminated()) {
			Object r = c.blockingReadFromAny(ref);
			processReception(ref, rank, c, r);		
		}
		c.broadcastButMe(ref, "done", 0);	
	}
	
	protected void processReception(Time ref, int rank, ActionManager c, Object r) throws InterruptedException {
		TaskExecutionAnalyser taskExecAnalyser = getTaskExecutionAnalyser();
		if (r instanceof ResponseToScheduler) {
			receiveResponseToScheduler(ref, (ResponseToScheduler)r, taskExecAnalyser);
			manageQueue(c, rank, ref);
		}
		if (r instanceof NodeAllocationRequest) {
			receiveAllocationRequest(c, ref, (NodeAllocationRequest)r);
		}
		if (r instanceof NodeDoneMessage) {
			releaseNode(c, ref, ((NodeDoneMessage)r).issuingNodeIndex, taskExecAnalyser);
			manageQueue(c, rank, ref);
		}		
	}

	protected boolean schedule(ActionManager action, RootTask rt, Time ref) {
		int firstFree = poolManager.requestANode(action, ref.getNanoseconds(), -1);
		if (firstFree == -1) return false; // task rejected
		runningTasks++;
		rt.assignedWorkingNodeIndex = firstFree;
		rt.setTimeAtWhichExecutionStarted(ref.getNanoseconds());		
	//	System.out.println("Scheduling " + rt.description + " to node " + firstFree);
		action.send(ref, rt, (int)rt.getSchedulingSize(), firstFree, 0);
		return true;
	}
	
	public void initWorkSharing(int participants) {
		runningTasks = 0;
		taskQueue.clear();
		this.participants = participants;
		poolManager.init(participants);
	}	

	protected void receiveResponseToScheduler(Time ref, ResponseToScheduler r, TaskExecutionAnalyser taskExecAnalyser) {	
	//	System.out.println("Task " + r.task.description + " completed");
		taskExecAnalyser.reservationEnded(ref.getNanoseconds() - poolManager.getReservationStartTime(r.respondingNodeIndex));
		poolManager.releaseNode(r.respondingNodeIndex);
		runningTasks--;
		taskExecAnalyser.taskCompleted(r.task, ref);
	}

	protected void receiveAllocationRequest(ActionManager action, Time ref, NodeAllocationRequest r) {
		NodeAllocationResponse resp = poolManager.getNodeAllocationResponse(r, action, ref);
		runningTasks += resp.allocNodes.size();
		
		action.send(ref, resp, application.GRANTED_NODE_LIST_MESSAGE_SIZE, r.issuingNode, 1);
	}

	protected void releaseNode(ActionManager action, Time ref, ArrayList<Integer> issuingNodeIndex, TaskExecutionAnalyser analyser) {
		for (Integer i : issuingNodeIndex) {
			poolManager.releaseNode(i);
			analyser.reservationEnded(ref.getNanoseconds() - poolManager.getReservationStartTime(i));
			runningTasks--;
		}
	}
	
	protected void manageQueue(ActionManager c, int rank, Time ref) throws InterruptedException {
		if (taskQueue.size() == 0) return;
		RootTask ct = taskQueue.peekFirst();
		if (schedule(c, ct, ref)) {
			taskQueue.removeFirst();
		}
	}
	
	@Override
	public void addApplicationInfo(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes) {
		getTaskExecutionAnalyser().store(lwSimExp, ref, analyseNodes);
	}

	@Override
	public int getNumberOfNotComputingNodes() {
		return 1;
	}		
	
}
