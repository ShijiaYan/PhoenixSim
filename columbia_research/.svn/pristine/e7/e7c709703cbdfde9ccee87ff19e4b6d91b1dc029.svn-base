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
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeDoneMessage;
import edu.columbia.lrl.experiments.task_traffic.schedulers.central.protocol.ResponseToDelegation;
import edu.columbia.lrl.experiments.task_traffic.task_generators.tasks.Task;

public class EvenBetterExecutionModel extends AbstractTaskExecutionModel {
	
	public EvenBetterExecutionModel() {}
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
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
			c.send(ref, request, appl.NODE_ALLOCATION_MESSAGE_SIZE, 0, 1); // 0 is the scheduler
			Object reception = c.blockingRead(ref, 0);
			if (reception == null) {
				if (c.isToBeTerminated()) {
					System.out.println("Dying, return");
					return;
				} else {
					throw new IllegalStateException("Should not");
				}
			}
			NodeAllocationResponse resp = ((NodeAllocationResponse)reception);
			ArrayList<Integer> allocation = new ArrayList<Integer>(resp.allocNodes);
			ArrayList<Integer> allocated = new ArrayList<Integer>();
		//	if (rank == 12)
		//	System.out.println(rank + " initial alloc is " + allocation);
			if (allocation.size() > 0) {
				int awaitedResponses = 0;				
				while (allocation.size() > 0 && subTaskListCopy.size() > 0) {
					Integer alloc = allocation.get(0);
					if (subTaskListCopy.size() > 0) {
						Task dele = subTaskListCopy.removeLast();
						delegateTask(alloc, dele, c, ref, rank);
						allocation.remove(alloc);
						allocated.add(alloc);
					/*	if (rank == 12)
						System.out.println(rank + " usage of "+  alloc + " free are " + allocation + " busy are " + allocated);*/
						awaitedResponses++;
					}
				}
				while (subTaskListCopy.size() > 0) {
					if (allocation.size() > 0 && !(allocation.get(0) instanceof Integer)) {
						throw new IllegalStateException();
					}
					// do some of the work myself
					if (subTaskListCopy.size() > 0) {
						Task myTask = subTaskListCopy.removeLast();
						executeTask(appl,myTask, c, ref, rank, taskExecAnalyser);
					}
					while (awaitedResponses > 0) {
						Object o = c.blockingReadFromSome(ref, allocated);
						if (o == null) {
							die(c);
							return;
						}
						if (!(o instanceof ResponseToDelegation)) throw new IllegalStateException("Object is " + o.getClass());
						ResponseToDelegation del = (ResponseToDelegation)o;		
						if (subTaskListCopy.size() <= (allocation.size() + 1)) {
							if (!allocated.remove(del.getNodeIndex())) throw new IllegalStateException();
							
						//	if (rank == 12)
						//	System.out.println(rank + " no more need of "+  del.getNodeIndex() + " free are " + allocation + " busy are " + allocated);
							
						//	if (del.getNodeIndex() == 9) {
						//		System.out.println(" 2 releases 9 " + allocated);
						//	}
							c.send(ref, new NodeDoneMessage(del.getNodeIndex()), appl.NODE_DONE_MESSAGE_SIZE, 0, 1);
						} else {
							Task dele = subTaskListCopy.removeLast();
							delegateTask(del.getNodeIndex(), dele, c, ref, rank);							
						}
						awaitedResponses--;
					}
					awaitedResponses = allocated.size();
				}
			} else {
				for (Task ttt : subTaskListCopy) {
					executeTask(appl, ttt, c, ref, rank, taskExecAnalyser);					
				}
			}
			// release the allocation
			if (allocation.size() > 0) {
				c.send(ref, new NodeDoneMessage(allocation), appl.NODE_DONE_MESSAGE_SIZE, 0, 1);		
				//if (rank == 12)
				//	System.out.println(rank + " end redeem of "  + allocation);
			}
		}
		t.executeAgg(ref, c);
	}
	
	private void die(ActionManager c) {
		if (c.isToBeTerminated()) {
			System.out.println("Dying, return");
			return;
		} else {
			throw new IllegalStateException("Should not");
		}		
	}

}
