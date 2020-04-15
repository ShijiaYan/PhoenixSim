package edu.columbia.lrl.experiments.task_traffic.protocols;

public class NodeAllocationRequest {
	
	public int requestedNodes;
	public int issuingNode;
	
	public NodeAllocationRequest(int requestedNodes, int issuingNode) {
		this.requestedNodes = requestedNodes;
		this.issuingNode = issuingNode;
	}

}
