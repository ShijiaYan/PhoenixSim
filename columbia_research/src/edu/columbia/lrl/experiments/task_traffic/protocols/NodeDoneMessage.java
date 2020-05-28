package edu.columbia.lrl.experiments.task_traffic.protocols;

import java.util.ArrayList;

public class NodeDoneMessage {
	
	
	
	public NodeDoneMessage(ArrayList<Integer> iss) {
		this.issuingNodeIndex = new ArrayList<>(iss.size());
		this.issuingNodeIndex.addAll(iss);
	}
	
	public NodeDoneMessage(int i) {
		issuingNodeIndex = new ArrayList<>(1);
		issuingNodeIndex.add(i);
	}
	
	public ArrayList<Integer> issuingNodeIndex;

}
