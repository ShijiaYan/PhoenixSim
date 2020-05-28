package edu.columbia.lrl.experiments.task_traffic.schedulers.hybrid.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeDoneMessage;
import edu.columbia.lrl.experiments.task_traffic.protocols.ResponseToScheduler;
import edu.columbia.lrl.experiments.task_traffic.protocols.ThreadResponse;
import edu.columbia.lrl.experiments.task_traffic.schedulers.AbstractThreadManagingAgent;
import edu.columbia.lrl.experiments.task_traffic.schedulers.ThreadInfo;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.RootTask;

public class HybridSchedulerExecutionAgent extends AbstractThreadManagingAgent {
	
	private static class Helper {
		
		public Helper(int id, boolean state) {
			this.id = id;
			this.state = state;
		}
		int id;
		boolean state;
	}
	
	ArrayList<Helper> helpers;
	int currentlyHelping;
//	boolean online = false;
	boolean askingHelp = false; // indicates if current agent has already asked some help
//	double onlineStart;
	
	boolean terminatedCorrectly = false;
	
	private boolean sharing;
	

	
	public HybridSchedulerExecutionAgent(@ParamName(name="Enable sharing of tasks", default_="true") boolean sharing) {
		this.sharing = sharing;
	}
	
	@ConstructorDef(ignore=true)
	private HybridSchedulerExecutionAgent(IrregularTrafficApplication appl, TaskExecutionAnalyser taskExecAnalyser, int rank, ActionManager action, boolean sharing) {
		super(appl, taskExecAnalyser, rank, action);
		this.sharing = sharing;
		
	}
	
	public HybridSchedulerExecutionAgent getInstance(IrregularTrafficApplication appl, TaskExecutionAnalyser taskExecutionAnalyser, int rank, ActionManager action) {
		return new HybridSchedulerExecutionAgent(appl, taskExecutionAnalyser, rank, action, this.sharing);
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Task sharing on?", sharing+"");
	}

	public void runComputeNode(ActionManager c, int rank, Time ref) throws InterruptedException {
		try {
			this.c = c;
			this.rank = rank;
			helpers = new ArrayList<>();
			
			while (!c.isToBeTerminated()) { // simulation can be running until a given number of packets
				while ((c.hasExpiredReceptions(ref) || !hasLocalWork()) && !c.isToBeTerminated()) {
					communicate(ref);
				}
				if (sharing) {
					delegate(ref);
					if (!askingHelp) {
						askForMoreHelp(ref);
					}
				}
				boolean hasAdvancedWork = finishCurrentWork(ref);
				if (hasAdvancedWork == false && deque.size() > 0) {
					ThreadInfo newWork = deque.removeFirst();
					startWork(ref, newWork);
					hasAdvancedWork = true;
				}
				if (hasAdvancedWork == false && online) {
					online = false;
					taskExecAnalyser.online(rank, ref.getNanoseconds() - onlineStart);
				}
				if (deque.size() == 0) considerReleaseNode(ref);
				if (!c.isToBeTerminated() && hasAdvancedWork) {
					c.yield(ref);
				}
			}
		}
		finally {
			if (terminatedCorrectly == false)
				System.out.println("Problem at termination");
		}
	}
	
	public void askForMoreHelp(Time ref) {
		int h = helpers.size();
		int passiveHelpers = h - currentlyHelping;
		int d = deque.size();
		// decision for asking more helpers
		if (h - currentlyHelping < d - 1) {
			int r = d-passiveHelpers-1;
			NodeAllocationRequest req = new NodeAllocationRequest(r, rank);
			c.send(ref, req, appl.NODE_ALLOCATION_MESSAGE_SIZE, 0);
			askingHelp = true;
		}
	}
	
	public void considerReleaseNode(Time ref) {
		int h = helpers.size();
		int passiveHelpers = h - currentlyHelping;
		int d = deque.size();		
		// decision for releasing helpers
		if (passiveHelpers >= d - 1 && passiveHelpers > 0) {
			int releases = passiveHelpers - (d - 1);
			ArrayList<Integer> toRelease = new ArrayList<>(releases);
			for (int i = 0 ; i < helpers.size(); i++) {
				Helper helper = helpers.get(i);
				if (!helper.state) {
					helpers.remove(i);
					releases--;
					toRelease.add(helper.id);
					if (releases == 0) break;
					i--;
				}
			}
			NodeDoneMessage msg = new NodeDoneMessage(toRelease);
			c.send(ref, msg, appl.NODE_DONE_MESSAGE_SIZE, 0);
		}
	}
	
	public void delegate(Time ref) {
		if (deque.size() == 0) return; // absolutely no work
		int limit;
		if (getReadyToAggregateThread() != null) { // has work to finish so can delegate all
			limit = 0;
		} else {
			limit = 1;
		}
		for (Helper helper : helpers) {
			if (deque.size() == limit) return;
			if (helper.state) continue;
			ThreadInfo work = getWorkToDelegate();
			if (work != null) {
				helper.state = true;
				currentlyHelping++;
				try {
					c.send(ref, work, (int)work.task.getSchedulingSize(), helper.id);
				}
				catch (Exception e) {
				}
			} else {
				return;
			}
		}
	}
	
	private ThreadInfo getWorkToDelegate() {
		for (Iterator<ThreadInfo> it = deque.iterator() ; it.hasNext() ; ) {
			ThreadInfo f = it.next();
			if (f.origin == rank) {
				it.remove();
				return f;
			}
		}
		return null;
	}
	
	public void communicate(Time ref) throws InterruptedException {
		Object r = c.blockingReadFromAny(ref, false);		
		if (r instanceof RootTask) {
			RootTask rt = (RootTask)r;
			deque.addLast(new ThreadInfo(rt, rank, null, ""));
		}
		if (r instanceof NodeAllocationResponse) {
			NodeAllocationResponse resp = (NodeAllocationResponse)r;
			for (Integer i : resp.allocNodes) {
				helpers.add(new Helper(i, false));
			}
			askingHelp = false;			
		}
		if (r instanceof ThreadInfo) {
			ThreadInfo resp = (ThreadInfo)r;
			deque.addLast(resp);			
		}
		if (r instanceof ThreadResponse) {
			ThreadResponse resp = (ThreadResponse)r;
			if (resp.source.task instanceof RootTask) {
				rootTaskTerminated((RootTask) resp.source.task, ref);
			} else {
				resp.source.parent.receivedSubResponses++;
			}
            for (Helper h : helpers) {
                if (h.id == resp.origin) {
                    h.state = false;
                    break;
                }
            }
			currentlyHelping--;
			considerReleaseNode(ref);
		}
		if ( r instanceof String) {
			appl.executionEnd(rank, ref);
			terminatedCorrectly = true;
			return;
		}
	}

	@Override
	public void rootTaskTerminated(RootTask rootTask, Time ref) {
		ResponseToScheduler resp = new ResponseToScheduler(rank, rootTask, hasLocalWork());	
		c.send(ref, resp, (int)rootTask.getResultSize(), 0);
	}
	
}
