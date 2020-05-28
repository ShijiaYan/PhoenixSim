package edu.columbia.lrl.experiments.task_traffic.schedulers.central.execmod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.IrregularTrafficApplication;
import edu.columbia.lrl.experiments.task_traffic.analyzers.TaskExecutionAnalyser;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeDoneMessage;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.protocol.ResponseToDelegation;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class BetterExecutionModel extends AbstractTaskExecutionModel {
	
	private boolean test = false;
	
	public BetterExecutionModel() {}
	
	public BetterExecutionModel(boolean test) {
		this.test = test;
	}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("scheduler with release", test+"");
	}

	@Override
	public void executeTask(IrregularTrafficApplication appl, Task t, ActionManager c, Time ref, int rank, TaskExecutionAnalyser taskExecAnalyser) throws InterruptedException {
		t.executeInit(ref, c);
		int subJobs = t.getChildrens().size();
		if (subJobs == 1) {
			// execute is locally
			executeTask(appl, t.getChildrens().get(0), c, ref, rank, taskExecAnalyser);
		}
		if (subJobs > 1) {
			ArrayDeque<Task> subTaskListCopy = t.getSubTasksListCopy();
			NodeAllocationRequest request = new NodeAllocationRequest(t.getChildrens().size()-1, rank);
			c.send(ref, request, appl.NODE_ALLOCATION_MESSAGE_SIZE, 0, 1/* high priority*/); // 0 is the scheduler
			Object reception = c.blockingRead(ref, 0);
			if (reception == null) {
				System.out.println("Receiving null, return");
				return;				
			}
			@SuppressWarnings("unchecked")
			ArrayList<Integer> allocation = (ArrayList<Integer>)reception;
			ArrayList<Integer> allocated = new ArrayList<>();
			if (allocation.size() > 0) {
								

				while (subTaskListCopy.size() > 0) {
					int awaitedResponses = 0;
					if (allocation.size() > 0 && !(allocation.get(0) instanceof Integer)) {
						throw new IllegalStateException();
					}
					while (allocation.size() > 0 && subTaskListCopy.size() > 0) {
						Integer alloc = allocation.get(0);
						if (subTaskListCopy.size() > 0) {
							Task dele = subTaskListCopy.removeLast();
							delegateTask(alloc, dele, c, ref, rank);
							allocation.remove(alloc);
							allocated.add(alloc);
							awaitedResponses++;
						}
					}					
					// do some of the work myself
					if (subTaskListCopy.size() > 0) {
						Task myTask = subTaskListCopy.removeLast();
						executeTask(appl, myTask, c, ref, rank, taskExecAnalyser);
					}
					while (awaitedResponses > 0) {
						Object o = c.blockingReadFromAnyButOne(ref, 0);
						if (o == null) {
							System.out.println("Receiving null, return");
							return;
						}
						if (!(o instanceof ResponseToDelegation)) throw new IllegalStateException("Object is " + o.getClass());
						ResponseToDelegation del = (ResponseToDelegation)o;
						allocated.remove(del.getNodeIndex());		
						if (subTaskListCopy.size() <= allocation.size() + 1 && test) {
						//	allocated.remove(del.getNodeIndex());
							c.send(ref, new NodeDoneMessage(del.getNodeIndex()), appl.NODE_DONE_MESSAGE_SIZE, 0, 1/* high priority*/);
						} else {
							allocation.add(del.getNodeIndex());						
						}
						awaitedResponses--;
					}
				}
				// release the allocation
				c.send(ref, new NodeDoneMessage(allocation), appl.NODE_DONE_MESSAGE_SIZE, 0, 1/* high priority*/);
			} else {
				for (Task ttt : subTaskListCopy) {
					executeTask(appl, ttt, c, ref, rank, taskExecAnalyser);					
				}
			}
		}
		t.executeAgg(ref, c);
	}

}
