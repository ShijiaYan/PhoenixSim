package edu.columbia.lrl.experiments.task_traffic.schedulers.work_stealing;

import java.util.Iterator;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.protocols.ThreadResponse;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractThreadManagingAgent;
import edu.columbia.lrl.experiments.task_traffic.schedulers.ThreadInfo;
import edu.columbia.lrl.experiments.task_traffic.schedulers.work_stealing.protocol.StealRequest;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;

public class WorkStealingAgent extends AbstractThreadManagingAgent {
	
	private WorkstealingScheduler parent;
	private ActionManager c;
	private int rank;
	private double lastAsk;
	private RootTask nextTask;
	private boolean justAsked = false;
	
	/**
	 * This variable is used to mark an agent as "done" when all tasks have been executed
	 */
	private boolean done = false;
	
	/**
	 * Used to name the task
	 */
	private int localTaskCounter = 0;

	public WorkStealingAgent(IrregularTrafficApplication appl, WorkstealingScheduler parent,
			ActionManager c, int rank) {
		super(appl, parent.getTaskExecutionAnalyser(), rank, c);
		this.parent = parent;
		this.c = c;
		this.rank = rank;
		this.lastAsk = -Double.MAX_VALUE;
	}
	
	public void proceed(Time ref) throws InterruptedException {
		generateNextTaskAndTimeout(ref);
		scheduleTimeoutForAskAround(ref, true);
		while (!done && !c.isToBeTerminated()) {	
			if (c.getReceptionQueueSize() > 0 || !hasLocalWork()) {
				communicate(ref);
			}
			checkNextTask(ref);
			boolean hasAdvancedWork = finishCurrentWork(ref);
			if (hasAdvancedWork == false && deque.size() > 0) {
				ThreadInfo newWork = deque.removeFirst();
				startWork(ref, newWork);
				hasAdvancedWork = true;
			}
			if (hasAdvancedWork == false && online) {
				online = false;
				parent.getTaskExecutionAnalyser().online(rank, ref.getNanoseconds() - onlineStart);
			}
			askAround(ref);
			if (!c.isToBeTerminated()) {
				c.yield(ref);
			}
		}
		if (online) {
			parent.getTaskExecutionAnalyser().online(rank, ref.getNanoseconds() - onlineStart);
		}
		synchronized (parent) {
			parent.done++;
			appl.executionEnd(rank, ref);
			c.doSomeJob(ref, 1000, "end");
		}
		c.barrierDone(ref);
	}
	
	private void checkNextTask(Time ref) {
		if (nextTask != null && nextTask.getOccurenceTimeNS() <= ref.getNanoseconds()) { // timeout is for new task
			deque.addLast(new ThreadInfo(nextTask, rank, null, ""+localTaskCounter++));
			generateNextTaskAndTimeout(ref);
		}
	}
	
	private void generateNextTaskAndTimeout(Time ref) {
		if (parent.hasMoreTasksToSchedule()) {
			nextTask = parent.getNextTask();
			c.scheduleTimeout(ref, nextTask.getOccurenceTimeNS() - ref.getNanoseconds());			
		} else {
			nextTask = null;
		}
	}
	
	private void communicate(Time ref) throws InterruptedException {
		do {
			Object r = c.blockingReadFromAny(ref, false); 
			if (r != null) {
				if (r instanceof StealRequest) {
					if (deque.size() > 1) {
						ThreadInfo toSend = null;
						for (Iterator<ThreadInfo> it = deque.iterator() ; it.hasNext() ; ) {
							ThreadInfo temp = it.next();
							if (temp.stealings < 3) {
								toSend = temp;
								it.remove();
								break;
							}
						}
						if (toSend != null) {
							toSend.stealings++;
							c.send(ref, toSend, (int)toSend.task.schedulingSize, ((StealRequest) r).sourceIndex);
						}
					}
				}
				if (r instanceof ThreadInfo) {
					ThreadInfo resp = (ThreadInfo)r;
					deque.addLast(resp);
				}
				if (r instanceof ThreadResponse) {
					ThreadResponse resp = (ThreadResponse)r;
					if (resp.source.parent != null) {
						resp.source.parent.receivedSubResponses++;
					}
					// check that resp.source.parent is well in openened threads ?
				}
				if (r instanceof String) {
					done = true;
				}
			}	
		}
		while (c.getReceptionQueueSize() > 0 && !c.isToBeTerminated());
	}
	
	private void askAround(Time ref) {
		if ((ref.getNanoseconds() - lastAsk >= parent.stealingFreqNS || !justAsked) && !done ) {
			if (!hasLocalWork()) {
				StealRequest sr = new StealRequest(rank);
				int size = appl.GENERIC_CONTROL_MESSAGE_SIZE;
				if (parent.coverageRate >= 1) {
					c.broadcastButMe(ref, sr, size);
				} else {
					int participants = c.getParticipantNumber() - 1;
					int nbReq = (int)Math.min(Math.max(1d, (double)participants *parent.coverageRate), participants-1);
					int[] dests = parent.poolManager.getLocationWeightedRandomSelectionOfNodes(c, nbReq, rank, true);
                    for (int d : dests) {
                        if (d == rank) continue;
                        c.send(ref, sr, size, d);
                    }
				}
				
				justAsked = true;
			}
			scheduleTimeoutForAskAround(ref, false);
		}
	}
	
	private void scheduleTimeoutForAskAround(Time ref, boolean first) {
		double d;
		if (first) {
			d = this.parent.nextDouble() * parent.stealingFreqNS;
			lastAsk = ref.getNanoseconds() - parent.stealingFreqNS + d;
			justAsked = true;
		} else {
			d = parent.stealingFreqNS;
			lastAsk = ref.getNanoseconds();	
		}
		c.scheduleTimeout(ref, d);
	
	}
	
	@Override
	protected void startWork(Time ref, ThreadInfo work) {
		super.startWork(ref, work);
		justAsked = false;
	}

	@Override
	public void rootTaskTerminated(RootTask rootTask, Time ref) {
		parent.terminated++;
		parent.getTaskExecutionAnalyser().taskCompleted(rootTask, ref);
		if (parent.hasStillWorkToDo() == false) {
			c.broadcastButMe(ref, "done", 0);
			this.done = true;
		}
	}

}
