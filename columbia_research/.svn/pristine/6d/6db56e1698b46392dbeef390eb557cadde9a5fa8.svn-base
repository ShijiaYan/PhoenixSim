package edu.columbia.ke.generic.dataStucture;

public class WeightedAvlNode extends AvlNode {
	
	private int weight;

	public WeightedAvlNode(int k) {
		super(k);
		weight = 1;
	}
	
	public void updateWeight(WeightedAvlNode l, WeightedAvlNode r ){
		weight = 1 + l.weight + r.weight;
	}

}
