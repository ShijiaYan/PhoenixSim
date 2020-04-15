package edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers;

import java.util.Arrays;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;

public class FlatClientPoolManager extends AbstractPoolManager {
	
	int participants;
	
	protected boolean[] assignments;
	protected int[] reservationIds;
	protected int[] reservationBy;
	protected double[] reservationStartTimes;
	
	int assigned = 0;
	
	public FlatClientPoolManager(@ParamName(name="Shuffle allocations", default_="true") boolean shuffleNode) {
		this.shuffle = shuffleNode;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Shuffled allocations", shuffle+"");
	}	
	
	boolean shuffle = false;	

	public void init(int participants) {
		this.participants = participants;
		this.assignments = new boolean[participants];
		this.reservationIds = new int[participants];
		this.reservationBy = new int[participants];
		this.reservationStartTimes = new double[participants];
		Arrays.fill(assignments, false);
	}
	
	public double getReservationStartTime(int index) {
		return reservationStartTimes[index];
	}
	
	public synchronized void releaseNode(int index) {
		if (assignments[index] == true) { 
			reservationBy[index] = -1;
			assignments[index] = false;
			assigned--;
		}
	}
	
	@Override
	public synchronized int requestANode(ActionManager action, double time, int fromAnode) {
		int firstFree = -1;
		// skipping node 0, as it is the scheduler
		
		int[] possibilities = getSortedArrayOfTrials(action, fromAnode, false);
		
		for (int j = 0 ; j < possibilities.length ; j++) {
			int i = possibilities[j];
			if (i == 0) continue;
			if (i == fromAnode) continue;
			if (!assignments[i]) {
				firstFree = i;
				assignments[i] = true;
				assigned++;
				reservationBy[i] = fromAnode;
				reservationStartTimes[i] = time;
				reservationIds[i]++;
				break;
			}
		}
		return firstFree;
	}
	
	public int[] getLocationWeightedRandomSelectionOfNodes(ActionManager action, int numberOfNodes, int local, boolean includeZero) {
		int[] t = getSortedArrayOfTrials(action, local, includeZero);
		if (numberOfNodes > t.length)  throw new IllegalStateException();
		int[] result = new int[numberOfNodes];
		System.arraycopy(t, 0, result, 0, numberOfNodes);
		return result;
	}

	protected int[] getSortedArrayOfTrials(ActionManager action, int fromAnode, boolean includeZero) {
		if (shuffle) {
			return action.getExperimentStream().shuffle(MoreArrays.range(includeZero?0:1, participants-1));
		} else {
			return MoreArrays.range(1, participants-1);
		}	
	}

	@Override
	public boolean isFullyFree() {
		return (assigned == 0);
	}

	@Override
	public NodeAllocationResponse getNodeAllocationResponse(NodeAllocationRequest r, ActionManager action, Time ref) {
		NodeAllocationResponse resp = new NodeAllocationResponse();
		for (int i = 0 ; i < r.requestedNodes ; i++) {
			int reserved = requestANode(action, ref.getNanoseconds(), r.issuingNode);
			if (reserved < 0) break;
			resp.addNodeIndex(reserved);
		}
		return resp;
	}	

}
