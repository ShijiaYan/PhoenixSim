package edu.columbia.ke.generic.dataStucture;

/** Here is the AVL-Node class for Completenesse **/
public class AvlNode {
	public AvlNode left;
	public AvlNode right;
	public AvlNode parent;
	public int key;
	public int balance;
	public int weight;

	public AvlNode(int k) {
		left = right = parent = null;
		balance = 0;
		key = k;
		weight = 1;
	}

	public String toString() {
		return "" + key;
	}
	
	public void updateWeight(){
		weight = 1;
		if (left != null)
			weight += left.weight;
		if (right != null)
			weight += right.weight;
	}
}
