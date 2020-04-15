package edu.columbia.lrl.experiments.task_traffic.clientpoolmanagers;

import java.util.Map;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationRequest;
import edu.columbia.lrl.experiments.task_traffic.protocols.NodeAllocationResponse;

public abstract class AbstractPoolManager {

	public abstract void init(int participants);
	
	public abstract double getReservationStartTime(int index);
	
	public abstract void releaseNode(int index);
	
	public abstract int requestANode(ActionManager action, double time, int fromAnode);
	
	public abstract Map<String, String> getAllParameters();

	public abstract boolean isFullyFree();
	
	/**
	 * Provide a random selection among nodes, weighted if necessary
	 * @param action
	 * @param numberOfNodes
	 * @param local
	 * @return
	 */
	public abstract int[] getLocationWeightedRandomSelectionOfNodes(ActionManager action, int numberOfNodes, int local, boolean includeZero);

	public abstract NodeAllocationResponse getNodeAllocationResponse(NodeAllocationRequest r, ActionManager action, Time ref);
	
	
}
