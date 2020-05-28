package edu.columbia.lrl.experiments.task_traffic.schedulers.hybrid;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.AbstractPoolManager;
import edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers.FlatClientPoolManager;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeDoneMessage;
import edu.columbia.lrl.experiments.task_traffic.protocols.ResponseToScheduler;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractScheduler;
import edu.columbia.lrl.experiments.task_traffic.schedulers.hybrid.client.HybridSchedulerExecutionAgent;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;

public class HybridScheduler extends AbstractScheduler {
	
	int participants;
	private HybridSchedulerExecutionAgent execMod;
	private AbstractPoolManager poolManager;
	int finished = 0;
	
	public HybridScheduler(@ParamName(name="A hybrid scheduler", defaultClass_=HybridSchedulerExecutionAgent.class) HybridSchedulerExecutionAgent execMod, 
			               @ParamName(name="Pool manager", defaultClass_=FlatClientPoolManager.class) AbstractPoolManager poolManager) {
		this.execMod = execMod;
		this.poolManager = poolManager;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = super.getAllParameters();
		map.put("Hybrid exec model", execMod.getClass().getSimpleName());
		map.putAll(execMod.getAllParameters());
		map.putAll(poolManager.getAllParameters());
		return map;
	}	

	@Override
	public void runImpl(ActionManager c, int rank, Time ref) throws InterruptedException {
		if (rank == 0) {
			participants = c.getParticipantNumber();
			poolManager.init(participants);
			getTaskExecutionAnalyser().init(participants, participants - 1);
			runSchedulerNode(c, rank, ref);
		}
		else {
			execMod.getInstance(application, getTaskExecutionAnalyser(), rank, c).runComputeNode(c, rank, ref);
		}
	}	

	private void runSchedulerNode(ActionManager c, int rank, Time ref) throws InterruptedException {
		// create and schedule initial task
		RootTask task = getNextTask();
		c.idle(ref, task.getOccurenceTimeNS());	
		schedule(c, task, ref);
		task = getNextTask();
		c.scheduleTimeout(ref, task.getOccurenceTimeNS() - ref.getNanoseconds());		
		
		int scheduled = 1;
		int toSchedule = getNumberOfTaskToSchedule();
		while (scheduled < toSchedule  && !c.isToBeTerminated()) {		
			Object r = c.blockingReadFromAny(ref); 
			if (r != null) {			
				processReception(ref, rank, c, r);		
			} 
			if (ref.getNanoseconds() >= task.getOccurenceTimeNS()) {
				scheduled++;
				schedule(c, task, ref);
				task = getNextTask();
				c.scheduleTimeout(ref, task.getOccurenceTimeNS() - ref.getNanoseconds());		
			}
		}
		while ((!poolManager.isFullyFree() || finished < toSchedule) && !c.isToBeTerminated()) {
			Object r = c.blockingReadFromAny(ref);
			processReception(ref, rank, c, r);			
		}
		c.broadcastButMe(ref, "done", 0);	
	}
	
	protected void processReception(Time ref, int rank, ActionManager c, Object r) throws InterruptedException {
		TaskExecutionAnalyser taskExecAnalyser = getTaskExecutionAnalyser();
		if (r instanceof ResponseToScheduler) {
			receiveResponseToScheduler(ref, (ResponseToScheduler)r, taskExecAnalyser);
		}
		if (r instanceof NodeAllocationRequest) {
			receiveAllocationRequest(c, ref, (NodeAllocationRequest)r);
		}
		if (r instanceof NodeDoneMessage) {
			releaseNode(c, ref, ((NodeDoneMessage)r).issuingNodeIndex, taskExecAnalyser);
		}		
	}	


	private void releaseNode(ActionManager c, Time ref, ArrayList<Integer> issuingNodeIndex, TaskExecutionAnalyser taskExecAnalyser) {
		for (Integer i : issuingNodeIndex) {
			poolManager.releaseNode(i);
			taskExecAnalyser.reservationEnded(ref.getNanoseconds() - poolManager.getReservationStartTime(i));
		}
		
	}

	private void receiveAllocationRequest(ActionManager c, Time ref,
			NodeAllocationRequest r) {
		NodeAllocationResponse resp = poolManager.getNodeAllocationResponse(r, c, ref);
		c.send(ref, resp, application.GRANTED_NODE_LIST_MESSAGE_SIZE, r.issuingNode);
	}

	private void receiveResponseToScheduler(Time ref, ResponseToScheduler r,
			TaskExecutionAnalyser taskExecAnalyser) {
		if (!r.continuing) {
			taskExecAnalyser.reservationEnded(ref.getNanoseconds() - poolManager.getReservationStartTime(r.respondingNodeIndex));
			poolManager.releaseNode(r.respondingNodeIndex);
		}
		taskExecAnalyser.taskCompleted(r.task, ref);
		finished++;
	}

	protected void schedule(ActionManager action, RootTask rt, Time ref) {
		int firstFree = poolManager.requestANode(action, ref.getNanoseconds(), -1);
		if (firstFree >= 0) { // a free node has been found
			rt.assignedWorkingNodeIndex = firstFree;
			rt.setTimeAtWhichExecutionStarted(ref.getNanoseconds());		
		//	System.out.println("Scheduling " + rt.description + " to node " + firstFree);
			action.send(ref, rt, (int)rt.getSchedulingSize(), firstFree);
		} else {
			// send the task to a random node. Later, send in a round-robin or least load fashion
			int choosen = action.getExperimentStream().nextInt(1, participants-1);
			rt.assignedWorkingNodeIndex = choosen;
			action.send(ref, rt, (int)rt.getSchedulingSize(), choosen);
		}
	}		

	@Override
	public int getNumberOfNotComputingNodes() {
		return 1;
	}

	@Override
	public void addApplicationInfo(LWSIMExperiment lwSimExp, double ref,
			boolean analyseNodes) {
		getTaskExecutionAnalyser().store(lwSimExp, ref, analyseNodes);

	}

}
