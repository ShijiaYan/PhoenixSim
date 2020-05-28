package edu.columbia.lrl.experiments.task_traffic.schedulers;

import java.util.ArrayDeque;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.protocols.ThreadResponse;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public abstract class AbstractThreadManagingAgent {

	protected ArrayDeque<ThreadInfo> openedThreads = new ArrayDeque<>();
	protected ArrayDeque<ThreadInfo> deque = new ArrayDeque<>();
	
	protected TaskExecutionAnalyser taskExecAnalyser;
	
	protected int rank;
	protected ActionManager c;
	
	/**
	 * This variable is used to indicate that a node is actually engaged in a task or sub-task
	 */
	protected boolean online = false;
	
	protected double onlineStart;	
	
	protected IrregularTrafficApplication appl;
	
	public AbstractThreadManagingAgent() {}
	
	
	public AbstractThreadManagingAgent(IrregularTrafficApplication appl, TaskExecutionAnalyser taskExecAnalyser, int rank, ActionManager c) {
		this.appl = appl;
		this.taskExecAnalyser = taskExecAnalyser;
		this.rank = rank;
		this.c = c;
	}
	
	protected ThreadInfo getReadyToAggregateThread() {
		for (ThreadInfo opened : openedThreads) {
			if (opened.receivedSubResponses == opened.task.size()) {
				return opened;
			}
		}
		return null;
	}
	
	protected boolean hasLocalWork() {
		return deque.size() > 0 || getReadyToAggregateThread() != null;
	}	
	
	protected void startWork(Time ref, ThreadInfo work) {
		if (online == false) {
			onlineStart = ref.getNanoseconds();
			online = true;
		}
		if (work.task instanceof RootTask) {
			RootTask rt = (RootTask)work.task;
			rt.setTimeAtWhichExecutionStarted(ref.getNanoseconds());
		}		
		work.task.executeInit(ref, c);
		int idx = 0;
		for (Task subtask : work.task.getChildrens()) {
			deque.addLast(new ThreadInfo(subtask, rank, work, work.desc + "-" + idx++));
		}
		openedThreads.addLast(work);
	}
	
	protected boolean finishCurrentWork(Time ref) {
		ThreadInfo toRemove = getReadyToAggregateThread();
		if (toRemove != null) {
			toRemove.task.executeAgg(ref, c);
			openedThreads.remove(toRemove);
			if (toRemove.origin != rank) {
				ThreadResponse resp = new ThreadResponse(toRemove, rank);
				c.send(ref, resp, (int)toRemove.task.getResultSize(), toRemove.origin);
			} else {
				if (toRemove.parent != null)
					toRemove.parent.receivedSubResponses++;
			}
			if (toRemove.task instanceof RootTask) {
				rootTaskTerminated((RootTask)toRemove.task, ref);
			}
			return true;
		}
		return false;
	}
	
	public abstract void rootTaskTerminated(RootTask rootTask, Time ref);	
	
	
}
