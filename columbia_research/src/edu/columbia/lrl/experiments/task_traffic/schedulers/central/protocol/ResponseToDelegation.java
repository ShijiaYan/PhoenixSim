package edu.columbia.lrl.experiments.task_traffic.schedulers.central.protocol;

public class ResponseToDelegation {
	
	private Integer nodeIndex;
	
	public ResponseToDelegation(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}
	
	public Integer getNodeIndex() {
		return nodeIndex;
	}

}
