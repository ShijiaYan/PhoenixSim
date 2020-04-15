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

public class DumbExecutionModel extends AbstractTaskExecutionModel {

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
			NodeAllocationRequest request = new NodeAllocationRequest(t.getChildrens().size(), rank);
			c.send(ref, request, appl.NODE_ALLOCATION_MESSAGE_SIZE, 0, 1/* high priority*/); // 0 is the scheduler
			Object reception = c.blockingRead(ref, 0);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> allocation = (ArrayList<Integer>)reception;
			if (allocation.size() > 0) {
				while (subTaskListCopy.size() > 0) {
					int awaitedResponses = 0;
					for (Integer allocated : allocation) {
						if (subTaskListCopy.size() > 0) {
							Task dele = subTaskListCopy.removeLast();
							delegateTask(allocated, dele, c, ref, rank);
							awaitedResponses++;
						}
					}
					while (awaitedResponses > 0) {
						Object o = c.blockingReadFromAnyButOne(ref, 0);
						if (!(o instanceof ResponseToDelegation)) throw new IllegalStateException("Object is " + o.getClass());
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
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

}
